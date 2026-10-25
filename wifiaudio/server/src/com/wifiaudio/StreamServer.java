package com.wifiaudio;

import com.wifiaudio.Mixer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONException;

/* loaded from: classes.dex */
public final class StreamServer implements Mixer.Sink {
    private static final int MAX_QUEUE_FRAMES = 12;
    private Thread acceptThread;
    private final int channels;
    private final int mode;
    private final int port;
    private final int sampleRate;
    private ServerSocket serverSocket;
    private final boolean useUdp;
    private volatile boolean running = false;
    private final List<Client> clients = new CopyOnWriteArrayList();

    private static final class Client {
        final OutputStream out;
        final ArrayBlockingQueue<byte[]> queue;
        final Socket socket;
        volatile Thread thread;

        Client(Socket socket, OutputStream outputStream, int i) {
            this.socket = socket;
            this.out = outputStream;
            this.queue = new ArrayBlockingQueue<>(i);
        }
    }

    public StreamServer(int i, int i2, int i3, int i4, boolean z) {
        this.port = i;
        this.sampleRate = i2;
        this.channels = i3;
        this.mode = i4;
        this.useUdp = z;
    }

    public void start() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
        this.running = true;
        Thread thread = new Thread(new Runnable() { // from class: com.wifiaudio.StreamServer.1
            @Override // java.lang.Runnable
            public void run() throws JSONException, IOException {
                StreamServer.this.acceptLoop();
            }
        }, "tcp-accept");
        this.acceptThread = thread;
        thread.start();
        Util.log("Tcp", "TCP stream server listening on port " + this.port + " (PCM16)");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acceptLoop() throws JSONException, IOException {
        while (this.running) {
            try {
                Socket socketAccept = this.serverSocket.accept();
                socketAccept.setTcpNoDelay(true);
                socketAccept.setSoTimeout(15000);
                final Client client = new Client(socketAccept, socketAccept.getOutputStream(), MAX_QUEUE_FRAMES);
                byte[] bArr = {87, 70, 65, 85, 1, 0, 0, 0, 0, 0, (byte) this.channels, 16, (byte) this.mode, this.useUdp ? (byte) 1 : (byte) 0, (byte) (iSenderLatencyMs & 255), (byte) ((iSenderLatencyMs >> 8) & 255)};
                putIntLE(bArr, 6, this.sampleRate);
                int iSenderLatencyMs = Status.senderLatencyMs();
                client.out.write(bArr);
                client.out.flush();
                this.clients.add(client);
                Status.setTcpClients(this.clients.size());
                updateClientsList();
                Thread thread = new Thread(new Runnable() { // from class: com.wifiaudio.StreamServer.2
                    @Override // java.lang.Runnable
                    public void run() throws JSONException, IOException {
                        StreamServer.this.sendLoop(client);
                    }
                }, "tcp-client");
                client.thread = thread;
                thread.start();
                Util.log("Tcp", "client connected, total=" + this.clients.size());
                Status.save();
            } catch (IOException e) {
                if (this.running) {
                    Util.log("Tcp", "accept error: " + Util.causeMsg(e));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendLoop(Client client) throws JSONException, IOException {
        while (this.running) {
            try {
                client.out.write(client.queue.take());
                client.out.flush();
                Status.bytesSent += r0.length;
            } catch (IOException | InterruptedException unused) {
            } catch (Throwable th) {
                removeClient(client);
                throw th;
            }
        }
        removeClient(client);
    }

    private void removeClient(Client client) throws JSONException, IOException {
        this.clients.remove(client);
        try {
            client.socket.close();
        } catch (IOException unused) {
        }
        Status.setTcpClients(this.clients.size());
        updateClientsList();
        Util.log("Tcp", "client disconnected, total=" + this.clients.size());
        Status.save();
    }

    private void updateClientsList() {
        StringBuilder sb = new StringBuilder();
        for (Client client : this.clients) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            InetSocketAddress inetSocketAddress = (InetSocketAddress) client.socket.getRemoteSocketAddress();
            if (inetSocketAddress != null && inetSocketAddress.getAddress() != null) {
                sb.append(inetSocketAddress.getAddress().getHostAddress());
            }
        }
        Status.clientsList = sb.toString();
    }

    @Override // com.wifiaudio.Mixer.Sink
    public void onPcm(byte[] bArr, int i) {
        if (this.running) {
            int i2 = i * 2 * this.channels;
            byte[] bArr2 = new byte[i2];
            System.arraycopy(bArr, 0, bArr2, 0, i2);
            pushToAll(bArr2);
        }
    }

    private void pushToAll(byte[] bArr) {
        int i = 0;
        for (Client client : this.clients) {
            if (!client.queue.offer(bArr)) {
                client.queue.poll();
                client.queue.offer(bArr);
            }
            int size = client.queue.size();
            if (size > i) {
                i = size;
            }
        }
        Status.tcpQueueFrames = i;
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
        for (Client client : this.clients) {
            try {
                client.socket.close();
            } catch (IOException unused2) {
            }
            if (client.thread != null) {
                client.thread.interrupt();
            }
        }
        this.clients.clear();
        Thread thread = this.acceptThread;
        if (thread != null) {
            thread.interrupt();
            this.acceptThread = null;
        }
        Status.setTcpClients(0);
    }

    private static void putIntLE(byte[] bArr, int i, int i2) {
        bArr[i] = (byte) (i2 & 255);
        bArr[i + 1] = (byte) ((i2 >> 8) & 255);
        bArr[i + 2] = (byte) ((i2 >> 16) & 255);
        bArr[i + 3] = (byte) ((i2 >> 24) & 255);
    }
}
