package com.wifiaudio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UDP 低延迟音频流服务器（游戏模式）。
 * 端口 = TCP 端口 + 1000。
 *
 * 协议：
 *   接收端 → 服务端: 4 字节请求/心跳 "WFAU"
 *   服务端 → 接收端: 16 字节格式头（与 TCP 头相同，[12]=mode [13]=transport）
 *                    之后每个音频包: 4 字节大端序列号 + 音频数据
 *   格式头每 1 秒重发一次（方便新接收端随时加入）
 *   接收端每 2 秒发一次心跳；服务端 6 秒无心跳则移除
 */
public final class UdpServer implements Mixer.Sink {

    public static final int UDP_PORT_OFFSET = 1000;
    private static final byte[] MAGIC = {'W', 'F', 'A', 'U'};
    private static final long HEARTBEAT_TIMEOUT_MS = 6000;

    private final int port;
    private final int codecType;   // 0=PCM 1=AAC 2=OPUS
    private final int sampleRate;
    private final int channels;
    private final int mode;        // 0=music 1=game
    private DatagramSocket socket;
    private volatile boolean running = false;
    private Thread recvThread;
    private Thread hbThread;
    private final Map<InetSocketAddress, Long> targets = new ConcurrentHashMap<InetSocketAddress, Long>();
    private final Map<InetSocketAddress, Integer> seqs = new ConcurrentHashMap<InetSocketAddress, Integer>();

    public UdpServer(int tcpPort, int codecType, int sampleRate, int channels, int mode) {
        this.port = tcpPort + UDP_PORT_OFFSET;
        this.codecType = codecType;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.mode = mode;
    }

    public int getPort() {
        return port;
    }

    public void start() throws IOException {
        socket = new DatagramSocket(port);
        running = true;
        recvThread = new Thread(new Runnable() {
            @Override
            public void run() {
                recvLoop();
            }
        }, "udp-recv");
        recvThread.start();
        hbThread = new Thread(new Runnable() {
            @Override
            public void run() {
                heartbeatLoop();
            }
        }, "udp-hb");
        hbThread.start();
        Util.log("Udp", "UDP server listening on port " + port + " (codec=" + codecType + ")");
    }

    private void recvLoop() {
        byte[] buf = new byte[64];
        DatagramPacket pkt = new DatagramPacket(buf, buf.length);
        while (running) {
            try {
                socket.receive(pkt);
                InetSocketAddress addr = new InetSocketAddress(pkt.getAddress(), pkt.getPort());
                if (isMagic(pkt)) {
                    boolean isNew = targets.put(addr, System.currentTimeMillis()) == null;
                    if (isNew) {
                        Status.clients = targets.size();
                        Util.log("Udp", "client joined: " + addr.getAddress().getHostAddress() + " (total=" + targets.size() + ")");
                        Status.save();
                    }
                    if (isNew) {
                        Util.log("Udp", "receiver joined: " + addr);
                    }
                    // 立即发送格式头
                    sendFormat(addr);
                }
            } catch (IOException e) {
                if (running) Util.log("Udp", "recv error: " + Util.causeMsg(e));
            }
        }
    }

    private void heartbeatLoop() {
        while (running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
            // 周期重发格式头（新接收端快速加入）
            for (InetSocketAddress addr : targets.keySet()) {
                sendFormat(addr);
            }
            // 清理超时目标
            long now = System.currentTimeMillis();
            for (Map.Entry<InetSocketAddress, Long> e : targets.entrySet()) {
                if (now - e.getValue() > HEARTBEAT_TIMEOUT_MS) {
                    targets.remove(e.getKey());
                    seqs.remove(e.getKey());
                    Util.log("Udp", "receiver timeout, removed: " + e.getKey());
                }
            }
        }
    }

    private boolean isMagic(DatagramPacket pkt) {
        if (pkt.getLength() < 4) return false;
        byte[] d = pkt.getData();
        int o = pkt.getOffset();
        return d[o] == 'W' && d[o + 1] == 'F' && d[o + 2] == 'A' && d[o + 3] == 'U';
    }

    private void sendFormat(InetSocketAddress addr) {
        try {
            byte[] header = new byte[16];
            header[0] = 'W'; header[1] = 'F'; header[2] = 'A'; header[3] = 'U';
            header[4] = 1; // version
            header[5] = (byte) codecType;
            putIntLE(header, 6, sampleRate);
            header[10] = (byte) channels;
            header[11] = (byte) (codecType == 0 ? 16 : 0);
            header[12] = (byte) mode;
            header[13] = (byte) 1; // transport: udp
            sendTo(addr, header);
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void onPcm(byte[] pcm, int frames) {
        if (!running || targets.isEmpty() || codecType != 0) return;
        int bytes = frames * 2 * channels;
        broadcast(pcm, bytes);
    }

    /** AAC 帧回调（编码线程） */
    public void onAac(byte[] adts, int len) {
        if (!running || targets.isEmpty() || codecType != 1) return;
        broadcast(adts, len);
    }

    /** OPUS 帧回调（编码线程） */
    public void onOpus(byte[] frame, int len) {
        if (!running || targets.isEmpty() || codecType != 2) return;
        broadcast(frame, len);
    }

    private void broadcast(byte[] payload, int len) {
        for (InetSocketAddress addr : targets.keySet()) {
            try {
                int seq = nextSeq(addr);
                byte[] pkt = new byte[4 + len];
                pkt[0] = (byte) (seq >> 24);
                pkt[1] = (byte) (seq >> 16);
                pkt[2] = (byte) (seq >> 8);
                pkt[3] = (byte) seq;
                System.arraycopy(payload, 0, pkt, 4, len);
                sendTo(addr, pkt);
            } catch (Throwable ignored) {
            }
        }
    }

    private int nextSeq(InetSocketAddress addr) {
        Integer v = seqs.get(addr);
        int next = (v == null) ? 0 : v + 1;
        seqs.put(addr, next);
        return next;
    }

    private void sendTo(InetSocketAddress addr, byte[] data) throws IOException {
        DatagramPacket pkt = new DatagramPacket(data, data.length, addr.getAddress(), addr.getPort());
        socket.send(pkt);
    }

    public void stop() {
        running = false;
        if (socket != null) socket.close();
        if (recvThread != null) recvThread.interrupt();
        if (hbThread != null) hbThread.interrupt();
        targets.clear();
        Status.clients = 0;
        seqs.clear();
    }

    private static void putIntLE(byte[] b, int off, int v) {
        b[off] = (byte) (v & 0xFF);
        b[off + 1] = (byte) ((v >> 8) & 0xFF);
        b[off + 2] = (byte) ((v >> 16) & 0xFF);
        b[off + 3] = (byte) ((v >> 24) & 0xFF);
    }
}
