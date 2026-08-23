package com.wifiaudio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * TCP 音频流服务器（接收端 App 使用）。
 * 协议：
 *   连接后先发送 16 字节头：
 *     [0..3]  "WFAU" magic
 *     [4]     version = 1
 *     [5]     codec: 0=PCM  1=AAC(ADTS)
 *     [6..9]  sampleRate (小端 uint32)
 *     [10]    channels
 *     [11]    bitsPerSample (PCM) / 0 (AAC)
 *     [12..15] reserved
 *   之后为连续的音频数据（PCM 原始帧 或 ADTS AAC 帧）。
 * 支持多个接收端同时连接（局域网广播）。
 */
public final class StreamServer implements Mixer.Sink {

    private static final int MAX_QUEUE_FRAMES = 240; // 约 240*1024 采样 = 5 秒缓冲上限（满则丢旧帧）

    private final int port;
    private final int codecType; // 0=PCM 1=AAC 2=OPUS
    private final int sampleRate;
    private final int channels;
    private final int mode; // 0=music 1=game
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private Thread acceptThread;
    private final List<Client> clients = new CopyOnWriteArrayList<Client>();
    private volatile byte[] opusHead = null;

    /** 设置 OPUS 模式的 OpusHead（csd-0），连接时下发给接收端 */
    public void setOpusHead(byte[] head) {
        this.opusHead = head;
    }

    private static final class Client {
        final Socket socket;
        final OutputStream out;
        final java.util.concurrent.ArrayBlockingQueue<byte[]> queue;

        Client(Socket socket, OutputStream out, int queueFrames) {
            this.socket = socket;
            this.out = out;
            this.queue = new java.util.concurrent.ArrayBlockingQueue<byte[]>(queueFrames);
        }
    }

    private final boolean useUdp;  // 发送端实际传输配置（写入协议头，接收端 auto 跟随）

    public StreamServer(int port, int codecType, int sampleRate, int channels, int mode, boolean useUdp) {
        this.port = port;
        this.codecType = codecType;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.mode = mode;
        this.useUdp = useUdp;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        acceptThread = new Thread(new Runnable() {
            @Override
            public void run() {
                acceptLoop();
            }
        }, "tcp-accept");
        acceptThread.start();
        Util.log("Tcp", "TCP stream server listening on port " + port
                + " codec=" + codecName(codecType));
    }

    private static String codecName(int c) {
        return c == 1 ? "aac" : (c == 2 ? "opus" : "pcm");
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(15000);
                final Client client = new Client(socket, socket.getOutputStream(), MAX_QUEUE_FRAMES);
                // 发送协议头
                byte[] header = new byte[16];
                header[0] = 'W'; header[1] = 'F'; header[2] = 'A'; header[3] = 'U';
                header[4] = 1; // version
                header[5] = (byte) codecType;
                putIntLE(header, 6, sampleRate);
                header[10] = (byte) channels;
                header[11] = (byte) (codecType == 0 ? 16 : 0);
                header[12] = (byte) mode;
                header[13] = (byte) (useUdp ? 1 : 0); // transport: 反映发送端实际配置
                client.out.write(header);
                client.out.flush();

                // OPUS 模式：下发 OpusHead（csd-0），接收端解码必需
                if (codecType == 2 && opusHead != null) {
                    byte[] pkt = new byte[opusHead.length + 2];
                    pkt[0] = (byte) (opusHead.length >> 8);
                    pkt[1] = (byte) opusHead.length;
                    System.arraycopy(opusHead, 0, pkt, 2, opusHead.length);
                    client.out.write(pkt);
                    client.out.flush();
                }

                clients.add(client);
                Status.clients = clients.size();
                updateClientsList();

                // 发送线程
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendLoop(client);
                    }
                }, "tcp-client");
                t.start();
                Util.log("Tcp", "client connected, total=" + clients.size());
                Status.save();
            } catch (IOException e) {
                if (running) {
                    Util.log("Tcp", "accept error: " + Util.causeMsg(e));
                }
            }
        }
    }

    private void sendLoop(Client client) {
        try {
            while (running) {
                byte[] frame = client.queue.take();
                client.out.write(frame);
                client.out.flush();
                Status.bytesSent += frame.length;
            }
        } catch (InterruptedException ignored) {
        } catch (IOException e) {
            // 客户端断开
        } finally {
            removeClient(client);
        }
    }

    private void removeClient(Client client) {
        clients.remove(client);
        try {
            client.socket.close();
        } catch (IOException ignored) {
        }
        Status.clients = clients.size();
        updateClientsList();
        Util.log("Tcp", "client disconnected, total=" + clients.size());
        Status.save();
    }

    private void updateClientsList() {
        StringBuilder sb = new StringBuilder();
        for (Client c : clients) {
            if (sb.length() > 0) sb.append(",");
            java.net.InetSocketAddress a = (java.net.InetSocketAddress) c.socket.getRemoteSocketAddress();
            if (a != null && a.getAddress() != null) sb.append(a.getAddress().getHostAddress());
        }
        Status.clientsList = sb.toString();
    }

    /** Mixer 回调（捕获线程调用，须快速返回） */
    @Override
    public void onPcm(byte[] pcm, int frames) {
        if (!running || codecType != 0) return;
        int bytes = frames * 2 * channels;
        byte[] copy = new byte[bytes];
        System.arraycopy(pcm, 0, copy, 0, bytes);
        pushToAll(copy);
    }

    /** AAC 帧回调（编码线程）。TCP 流加 2 字节大端长度前缀（可靠切帧，避免 ADTS 假同步） */
    public void onAac(byte[] adts, int len) {
        if (!running || codecType != 1) return;
        byte[] copy = new byte[len + 2];
        copy[0] = (byte) (len >> 8);
        copy[1] = (byte) len;
        System.arraycopy(adts, 0, copy, 2, len);
        pushToAll(copy);
    }

    /** OPUS 帧回调（编码线程）。TCP 流需要帧边界，加 2 字节大端长度前缀 */
    public void onOpus(byte[] frame, int len) {
        if (!running || codecType != 2) return;
        byte[] copy = new byte[len + 2];
        copy[0] = (byte) (len >> 8);
        copy[1] = (byte) len;
        System.arraycopy(frame, 0, copy, 2, len);
        pushToAll(copy);
    }

    private void pushToAll(byte[] frame) {
        for (Client c : clients) {
            if (!c.queue.offer(frame)) {
                // 队列满：丢弃最旧一帧，保持实时性
                c.queue.poll();
                c.queue.offer(frame);
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {
        }
        for (Client c : clients) {
            try {
                c.socket.close();
            } catch (IOException ignored) {
            }
        }
        clients.clear();
        if (acceptThread != null) {
            acceptThread.interrupt();
            acceptThread = null;
        }
        Status.clients = 0;
    }

    private static void putIntLE(byte[] b, int off, int v) {
        b[off] = (byte) (v & 0xFF);
        b[off + 1] = (byte) ((v >> 8) & 0xFF);
        b[off + 2] = (byte) ((v >> 16) & 0xFF);
        b[off + 3] = (byte) ((v >> 24) & 0xFF);
    }
}
