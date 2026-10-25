package com.wifiaudio;

import com.wifiaudio.Mixer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public final class HttpServer implements Mixer.Sink {
    private static final String SERVER_ID = "WiFiAudio/4.19";
    private Thread acceptThread;
    private final int channels;
    private final int port;
    private final int sampleRate;
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private final List<HttpClient> clients = new CopyOnWriteArrayList();

    private static final class HttpClient {
        final OutputStream out;
        final Socket socket;
        final ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(120);
        volatile boolean closed = false;

        HttpClient(Socket socket, OutputStream outputStream) {
            this.socket = socket;
            this.out = outputStream;
        }
    }

    public HttpServer(int i, int i2, int i3) {
        this.port = i;
        this.sampleRate = i2;
        this.channels = i3;
    }

    public void start() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
        this.running = true;
        Thread thread = new Thread(new Runnable() { // from class: com.wifiaudio.HttpServer.1
            @Override // java.lang.Runnable
            public void run() throws IOException {
                HttpServer.this.acceptLoop();
            }
        }, "http-accept");
        this.acceptThread = thread;
        thread.start();
        Util.log("Http", "HTTP stream server listening on port " + this.port + " (PCM16 WAV)");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acceptLoop() throws IOException {
        while (this.running) {
            try {
                final Socket socketAccept = this.serverSocket.accept();
                new Thread(new Runnable() { // from class: com.wifiaudio.HttpServer.2
                    @Override // java.lang.Runnable
                    public void run() throws IOException {
                        HttpServer.this.handle(socketAccept);
                    }
                }, "http-client").start();
            } catch (IOException e) {
                if (this.running) {
                    Util.log("Http", "accept error: " + Util.causeMsg(e));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handle(Socket socket) throws IOException {
        byte[] bArr;
        int iIndexOf;
        try {
            socket.setSoTimeout(5000);
            InputStream inputStream = socket.getInputStream();
            bArr = new byte[8192];
            iIndexOf = -1;
            int i = 0;
            while (i < 8192) {
                int i2 = inputStream.read(bArr, i, 8192 - i);
                if (i2 < 0) {
                    try {
                        socket.close();
                        return;
                    } catch (IOException unused) {
                        return;
                    }
                } else {
                    i += i2;
                    iIndexOf = new String(bArr, 0, i, StandardCharsets.ISO_8859_1).indexOf("\r\n\r\n");
                    if (iIndexOf >= 0) {
                        break;
                    }
                }
            }
        } catch (IOException unused2) {
        } catch (Throwable th) {
            try {
                socket.close();
            } catch (IOException unused3) {
            }
            throw th;
        }
        if (iIndexOf < 0) {
            try {
                socket.close();
                return;
            } catch (IOException unused4) {
                return;
            }
        }
        String[] strArrSplit = new String(bArr, 0, iIndexOf, StandardCharsets.ISO_8859_1).split("\r\n");
        if (strArrSplit.length == 0) {
            try {
                socket.close();
                return;
            } catch (IOException unused5) {
                return;
            }
        }
        String[] strArrSplit2 = strArrSplit[0].split(" ");
        if (strArrSplit2.length < 2) {
            try {
                socket.close();
                return;
            } catch (IOException unused6) {
                return;
            }
        }
        String str = strArrSplit2[1];
        socket.setSoTimeout(0);
        if ("/".equals(str)) {
            sendText(socket, 200, "text/html; charset=utf-8", indexHtml(this.port));
        } else if ("/status".equals(str)) {
            sendText(socket, 200, "application/json", Status.toJson());
        } else if ("/stream".equals(str) || "/stream.wav".equals(str)) {
            streamWav(socket);
        } else {
            sendText(socket, 404, "text/plain; charset=utf-8", "Not found. Try /stream or /status");
        }
        try {
            socket.close();
        } catch (IOException unused7) {
        }
    }

    private static String reason(int i) {
        if (i == 206) {
            return "Partial Content";
        }
        if (i == 400) {
            return "Bad Request";
        }
        if (i == 409) {
            return "Conflict";
        }
        if (i == 404) {
            return "Not Found";
        }
        if (i == 405) {
            return "Method Not Allowed";
        }
        return "OK";
    }

    private void sendText(Socket socket, int i, String str, String str2) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        byte[] bytes = str2.getBytes(StandardCharsets.UTF_8);
        outputStream.write(("HTTP/1.1 " + i + ' ' + reason(i) + "\r\nServer: WiFiAudio/4.19\r\nContent-Type: " + str + "\r\nContent-Length: " + bytes.length + "\r\nCache-Control: no-cache\r\nConnection: close\r\n\r\n").getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(bytes);
        outputStream.flush();
    }

    private void sendStreamHeaders(OutputStream outputStream, String str) throws IOException {
        outputStream.write(("HTTP/1.1 200 OK\r\nServer: WiFiAudio/4.19\r\nContent-Type: " + str + "\r\nCache-Control: no-cache, no-store\r\nPragma: no-cache\r\nAccept-Ranges: none\r\nConnection: close\r\n\r\n").getBytes(StandardCharsets.ISO_8859_1));
        outputStream.flush();
    }

    private void streamWav(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        sendStreamHeaders(outputStream, "audio/wav");
        HttpClient httpClient = new HttpClient(socket, outputStream);
        outputStream.write(buildWavHeader(this.sampleRate, this.channels, 16));
        outputStream.flush();
        this.clients.add(httpClient);
        Util.log("Http", "WAV client connected: " + socket.getRemoteSocketAddress() + " " + this.sampleRate + "Hz/" + this.channels + "ch");
        pump(httpClient);
    }

    private void pump(HttpClient httpClient) throws IOException {
        Socket socket;
        while (this.running && !httpClient.closed) {
            try {
                try {
                    try {
                        httpClient.out.write(httpClient.queue.take());
                        httpClient.out.flush();
                        Status.bytesSent += r3.length;
                    } catch (InterruptedException unused) {
                        httpClient.closed = true;
                        this.clients.remove(httpClient);
                        socket = httpClient.socket;
                    }
                } catch (IOException unused2) {
                    httpClient.closed = true;
                    this.clients.remove(httpClient);
                    socket = httpClient.socket;
                } catch (Throwable th) {
                    httpClient.closed = true;
                    this.clients.remove(httpClient);
                    try {
                        httpClient.socket.close();
                    } catch (IOException unused3) {
                    }
                    Util.log("Http", "stream client disconnected");
                    throw th;
                }
            } catch (IOException unused4) {
            }
        }
        httpClient.closed = true;
        this.clients.remove(httpClient);
        socket = httpClient.socket;
        socket.close();
        Util.log("Http", "stream client disconnected");
    }

    @Override // com.wifiaudio.Mixer.Sink
    public void onPcm(byte[] bArr, int i) {
        if (!this.running || this.clients.isEmpty()) {
            return;
        }
        int i2 = i * 2 * this.channels;
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, 0, bArr2, 0, i2);
        push(bArr2);
    }

    private void push(byte[] bArr) {
        for (HttpClient httpClient : this.clients) {
            if (!httpClient.closed && !httpClient.queue.offer(bArr)) {
                httpClient.queue.poll();
                httpClient.queue.offer(bArr);
            }
        }
    }

    public void stop() throws IOException {
        this.running = false;
        try {
            ServerSocket serverSocket = this.serverSocket;
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException unused) {
        }
        for (HttpClient httpClient : this.clients) {
            httpClient.closed = true;
            try {
                httpClient.socket.close();
            } catch (IOException unused2) {
            }
        }
        this.clients.clear();
        Thread thread = this.acceptThread;
        if (thread != null) {
            thread.interrupt();
            this.acceptThread = null;
        }
    }

    private static byte[] buildWavHeader(int i, int i2, int i3) {
        byte[] bArr = new byte[44];
        bArr[0] = 82;
        bArr[1] = 73;
        bArr[2] = 70;
        bArr[3] = 70;
        putIntLE(bArr, 4, -1);
        bArr[8] = 87;
        bArr[9] = 65;
        bArr[10] = 86;
        bArr[11] = 69;
        bArr[12] = 102;
        bArr[13] = 109;
        bArr[14] = 116;
        bArr[15] = 32;
        putIntLE(bArr, 16, 16);
        putShortLE(bArr, 20, 1);
        putShortLE(bArr, 22, i2);
        putIntLE(bArr, 24, i);
        putIntLE(bArr, 28, ((i * i2) * i3) / 8);
        putShortLE(bArr, 32, (i2 * i3) / 8);
        putShortLE(bArr, 34, i3);
        bArr[36] = 100;
        bArr[37] = 97;
        bArr[38] = 116;
        bArr[39] = 97;
        putIntLE(bArr, 40, -1);
        return bArr;
    }

    private static void putIntLE(byte[] bArr, int i, int i2) {
        bArr[i] = (byte) (i2 & 255);
        bArr[i + 1] = (byte) ((i2 >> 8) & 255);
        bArr[i + 2] = (byte) ((i2 >> 16) & 255);
        bArr[i + 3] = (byte) ((i2 >> 24) & 255);
    }

    private static void putShortLE(byte[] bArr, int i, int i2) {
        bArr[i] = (byte) (i2 & 255);
        bArr[i + 1] = (byte) ((i2 >> 8) & 255);
    }

    private static String indexHtml(int i) {
        return "<html><head><title>WiFiAudio</title></head><body style=\"font-family:sans-serif\"><h2>WiFiAudio 音频流</h2><ul><li><a href=\"/stream\">/stream</a> — WAV PCM 流 (VLC: 打开网络串流 <code>http://ip:" + i + "/stream</code>)</li><li><a href=\"/status\">/status</a> — 状态 JSON</li></ul></body></html>";
    }
}
