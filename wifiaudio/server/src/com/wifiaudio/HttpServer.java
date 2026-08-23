package com.wifiaudio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * HTTP 音频流服务器 —— 供 PC 直接播放（VLC / ffplay / 浏览器）：
 *   GET /             说明页
 *   GET /stream        WAV 头 + PCM 流 (audio/wav)
 *   GET /stream.aac    ADTS AAC 流 (audio/aac)
 *   GET /status        JSON 状态
 */
public final class HttpServer implements Mixer.Sink {

    private final int port;
    private final int codecType; // 0=PCM 1=AAC 2=OPUS
    private final int sampleRate;
    private final int channels;
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private Thread acceptThread;
    private final List<HttpClient> clients = new CopyOnWriteArrayList<HttpClient>();

    private static final class HttpClient {
        final Socket socket;
        final OutputStream out;
        final java.util.concurrent.ArrayBlockingQueue<byte[]> queue = new java.util.concurrent.ArrayBlockingQueue<byte[]>(120);
        volatile boolean closed = false;

        HttpClient(Socket socket, OutputStream out) {
            this.socket = socket;
            this.out = out;
        }
    }

    public HttpServer(int port, int codecType, int sampleRate, int channels) {
        this.port = port;
        this.codecType = codecType;
        this.sampleRate = sampleRate;
        this.channels = channels;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        acceptThread = new Thread(new Runnable() {
            @Override
            public void run() {
                acceptLoop();
            }
        }, "http-accept");
        acceptThread.start();
        Util.log("Http", "HTTP stream server listening on port " + port
                + " codec=" + (codecType == 1 ? "aac" : (codecType == 2 ? "opus" : "pcm")));
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handle(socket);
                    }
                }, "http-client");
                t.start();
            } catch (IOException e) {
                if (running) Util.log("Http", "accept error: " + Util.causeMsg(e));
            }
        }
    }

    private void handle(Socket socket) {
        InputStream in = null;
        try {
            socket.setSoTimeout(5000);
            in = socket.getInputStream();
            // 读请求行和头（最多 8KB）
            byte[] buf = new byte[8192];
            int total = 0;
            int idx = -1;
            while (total < buf.length) {
                int n = in.read(buf, total, buf.length - total);
                if (n < 0) return;
                total += n;
                String s = new String(buf, 0, total, StandardCharsets.ISO_8859_1);
                idx = s.indexOf("\r\n\r\n");
                if (idx >= 0) break;
            }
            if (idx < 0) return;
            String request = new String(buf, 0, idx, StandardCharsets.ISO_8859_1);
            String[] lines = request.split("\r\n");
            if (lines.length == 0) return;
            String[] parts = lines[0].split(" ");
            if (parts.length < 2) return;
            String path = parts[1];

            socket.setSoTimeout(0);

            if ("/".equals(path)) {
                sendText(socket, 200, "text/html; charset=utf-8", indexHtml(port));
            } else if ("/status".equals(path)) {
                sendText(socket, 200, "application/json", Status.toJson());
            } else if ("/stream".equals(path) || "/stream.wav".equals(path)) {
                streamWav(socket);
            } else if ("/stream.aac".equals(path)) {
                streamAac(socket);
            } else if ("/stream.opus".equals(path)) {
                streamOpus(socket);
            } else {
                sendText(socket, 404, "text/plain", "Not found. Try /stream, /stream.aac, /status");
            }
        } catch (IOException ignored) {
            // 客户端断开
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void sendText(Socket socket, int code, String type, String body) throws IOException {
        OutputStream out = socket.getOutputStream();
        byte[] data = body.getBytes(StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ").append(code).append(" OK\r\n");
        sb.append("Content-Type: ").append(type).append("\r\n");
        sb.append("Content-Length: ").append(data.length).append("\r\n");
        sb.append("Connection: close\r\n\r\n");
        out.write(sb.toString().getBytes(StandardCharsets.ISO_8859_1));
        out.write(data);
        out.flush();
    }

    /** WAV + 连续 PCM 流 */
    private void streamWav(Socket socket) throws IOException {
        HttpClient client = new HttpClient(socket, socket.getOutputStream());
        // WAV 头
        byte[] hdr = buildWavHeader(sampleRate, channels, 16);
        client.out.write(hdr);
        client.out.flush();
        clients.add(client);
        pump(client);
    }

    /** ADTS AAC 流 */
    private void streamAac(Socket socket) throws IOException {
        HttpClient client = new HttpClient(socket, socket.getOutputStream());
        clients.add(client);
        pump(client);
    }

    /** 裸 OPUS 帧流（experimental：请优先使用 Android 接收端 App 或切 AAC/PCM 供 PC 播放） */
    private void streamOpus(Socket socket) throws IOException {
        HttpClient client = new HttpClient(socket, socket.getOutputStream());
        clients.add(client);
        pump(client);
    }

    private void pump(HttpClient client) {
        try {
            while (running && !client.closed) {
                byte[] frame = client.queue.take();
                client.out.write(frame);
                client.out.flush();
                Status.bytesSent += frame.length;
            }
        } catch (InterruptedException ignored) {
        } catch (IOException e) {
            // 断开
        } finally {
            client.closed = true;
            clients.remove(client);
            try {
                client.socket.close();
            } catch (IOException ignored) {
            }
            Util.log("Http", "stream client disconnected");
        }
    }

    @Override
    public void onPcm(byte[] pcm, int frames) {
        if (!running || clients.isEmpty() || codecType != 0) return;
        int bytes = frames * 2 * channels;
        byte[] copy = new byte[bytes];
        System.arraycopy(pcm, 0, copy, 0, bytes);
        push(copy);
    }

    /** AAC 帧回调 */
    public void onAac(byte[] adts, int len) {
        if (!running || clients.isEmpty() || codecType != 1) return;
        byte[] copy = new byte[len];
        System.arraycopy(adts, 0, copy, 0, len);
        push(copy);
    }

    /** OPUS 帧回调（HTTP 流，带 2 字节大端长度前缀以便切帧） */
    public void onOpus(byte[] frame, int len) {
        if (!running || clients.isEmpty() || codecType != 2) return;
        byte[] copy = new byte[len + 2];
        copy[0] = (byte) (len >> 8);
        copy[1] = (byte) len;
        System.arraycopy(frame, 0, copy, 2, len);
        push(copy);
    }

    private void push(byte[] frame) {
        for (HttpClient c : clients) {
            if (c.closed) continue;
            if (!c.queue.offer(frame)) {
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
        for (HttpClient c : clients) {
            c.closed = true;
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
    }

    private static byte[] buildWavHeader(int sampleRate, int channels, int bits) {
        int byteRate = sampleRate * channels * bits / 8;
        int blockAlign = channels * bits / 8;
        byte[] h = new byte[44];
        h[0] = 'R'; h[1] = 'I'; h[2] = 'F'; h[3] = 'F';
        putIntLE(h, 4, 0xFFFFFFFF); // 未知大小
        h[8] = 'W'; h[9] = 'A'; h[10] = 'V'; h[11] = 'E';
        h[12] = 'f'; h[13] = 'm'; h[14] = 't'; h[15] = ' ';
        putIntLE(h, 16, 16);          // fmt chunk size
        putShortLE(h, 20, 1);         // PCM
        putShortLE(h, 22, channels);
        putIntLE(h, 24, sampleRate);
        putIntLE(h, 28, byteRate);
        putShortLE(h, 32, blockAlign);
        putShortLE(h, 34, bits);
        h[36] = 'd'; h[37] = 'a'; h[38] = 't'; h[39] = 'a';
        putIntLE(h, 40, 0xFFFFFFFF);
        return h;
    }

    private static void putIntLE(byte[] b, int off, int v) {
        b[off] = (byte) (v & 0xFF);
        b[off + 1] = (byte) ((v >> 8) & 0xFF);
        b[off + 2] = (byte) ((v >> 16) & 0xFF);
        b[off + 3] = (byte) ((v >> 24) & 0xFF);
    }

    private static void putShortLE(byte[] b, int off, int v) {
        b[off] = (byte) (v & 0xFF);
        b[off + 1] = (byte) ((v >> 8) & 0xFF);
    }

    private static String indexHtml(int port) {
        return "<html><head><title>WiFiAudio</title></head><body style=\"font-family:sans-serif\">"
                + "<h2>WiFiAudio 音频流</h2>"
                + "<ul>"
                + "<li><a href=\"/stream\">/stream</a> — WAV PCM 流 (VLC: 打开网络串流 <code>http://ip:" + port + "/stream</code>)</li>"
                + "<li><a href=\"/stream.aac\">/stream.aac</a> — AAC 流 (VLC: <code>http://ip:" + port + "/stream.aac</code>)</li>"
                + "<li><a href=\"/status\">/status</a> — 状态 JSON</li>"
                + "</ul></body></html>";
    }
}
