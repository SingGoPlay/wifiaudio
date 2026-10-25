package com.wifiaudio;

import com.wifiaudio.Mixer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONException;

/* loaded from: classes.dex */
public final class UdpServer implements Mixer.Sink {
    private static final long HEARTBEAT_TIMEOUT_MS = 6000;
    private static final byte[] MAGIC = {87, 70, 65, 85};
    private static final int MAX_PAYLOAD_BYTES = 1200;
    private static final int SEND_QUEUE_MAX = 4;
    public static final int UDP_PORT_OFFSET = 1000;
    private final int channels;
    private Thread hbThread;
    private long lastDropLogAt;
    private long loggedDrops;
    private final int mode;
    private final int port;
    private Thread recvThread;
    private final int sampleRate;
    private volatile long sendDrops;
    private Thread sendThread;
    private DatagramSocket socket;
    private volatile boolean running = false;
    private final ArrayDeque<DatagramPacket> sendQ = new ArrayDeque<>();
    private final Map<InetSocketAddress, Long> targets = new ConcurrentHashMap();
    private final Map<InetSocketAddress, Integer> seqs = new ConcurrentHashMap();

    public UdpServer(int i, int i2, int i3, int i4) {
        this.port = i + UDP_PORT_OFFSET;
        this.sampleRate = i2;
        this.channels = i3;
        this.mode = i4;
    }

    public int getPort() {
        return this.port;
    }

    public void start() throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket(this.port);
        this.socket = datagramSocket;
        try {
            datagramSocket.setSendBufferSize(32768);
        } catch (Throwable unused) {
        }
        this.running = true;
        Thread thread = new Thread(new Runnable() { // from class: com.wifiaudio.UdpServer.1
            @Override // java.lang.Runnable
            public void run() {
                UdpServer.this.sendLoop();
            }
        }, "udp-send");
        this.sendThread = thread;
        thread.start();
        Thread thread2 = new Thread(new Runnable() { // from class: com.wifiaudio.UdpServer.2
            @Override // java.lang.Runnable
            public void run() throws JSONException, IOException {
                UdpServer.this.recvLoop();
            }
        }, "udp-recv");
        this.recvThread = thread2;
        thread2.start();
        Thread thread3 = new Thread(new Runnable() { // from class: com.wifiaudio.UdpServer.3
            @Override // java.lang.Runnable
            public void run() throws InterruptedException {
                UdpServer.this.heartbeatLoop();
            }
        }, "udp-hb");
        this.hbThread = thread3;
        thread3.start();
        Util.log("Udp", "UDP server listening on port " + this.port + " (PCM16)");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendLoop() {
        DatagramPacket datagramPacketPollFirst;
        while (this.running) {
            synchronized (this.sendQ) {
                while (this.running && this.sendQ.isEmpty()) {
                    try {
                        this.sendQ.wait(200L);
                    } catch (InterruptedException unused) {
                        return;
                    }
                }
                if (!this.running) {
                    return;
                } else {
                    datagramPacketPollFirst = this.sendQ.pollFirst();
                }
            }
            if (datagramPacketPollFirst != null) {
                try {
                    this.socket.send(datagramPacketPollFirst);
                } catch (Throwable unused2) {
                }
                long j = this.sendDrops;
                if (j > this.loggedDrops) {
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    if (jCurrentTimeMillis - this.lastDropLogAt > 5000) {
                        this.lastDropLogAt = jCurrentTimeMillis;
                        Util.log("Udp", "发送队列满，丢弃最旧的包（链路发送不过来），累计 " + j + " 个 —— 只在发送侧丢，不会阻塞 Mixer 分发线程");
                    }
                    this.loggedDrops = j;
                }
            }
        }
    }

    private void enqueue(DatagramPacket datagramPacket) {
        synchronized (this.sendQ) {
            while (this.sendQ.size() >= SEND_QUEUE_MAX) {
                this.sendQ.pollFirst();
                this.sendDrops++;
            }
            this.sendQ.addLast(datagramPacket);
            this.sendQ.notifyAll();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recvLoop() throws JSONException, IOException {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[64], 64);
        while (this.running) {
            try {
                this.socket.receive(datagramPacket);
                InetSocketAddress inetSocketAddress = new InetSocketAddress(datagramPacket.getAddress(), datagramPacket.getPort());
                if (isMagic(datagramPacket)) {
                    boolean z = this.targets.put(inetSocketAddress, Long.valueOf(System.currentTimeMillis())) == null;
                    if (z) {
                        Status.setUdpClients(this.targets.size());
                        Util.log("Udp", "client joined: " + inetSocketAddress.getAddress().getHostAddress() + " (total=" + this.targets.size() + ")");
                        Status.save();
                    }
                    if (z) {
                        Util.log("Udp", "receiver joined: " + inetSocketAddress);
                    }
                    sendFormat(inetSocketAddress);
                }
            } catch (IOException e) {
                if (this.running) {
                    Util.log("Udp", "recv error: " + Util.causeMsg(e));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void heartbeatLoop() throws InterruptedException {
        while (this.running) {
            try {
                Thread.sleep(1000L);
                Iterator<InetSocketAddress> it = this.targets.keySet().iterator();
                while (it.hasNext()) {
                    sendFormat(it.next());
                }
                long jCurrentTimeMillis = System.currentTimeMillis();
                for (Map.Entry<InetSocketAddress, Long> entry : this.targets.entrySet()) {
                    if (jCurrentTimeMillis - entry.getValue().longValue() > HEARTBEAT_TIMEOUT_MS) {
                        this.targets.remove(entry.getKey());
                        this.seqs.remove(entry.getKey());
                        Util.log("Udp", "receiver timeout, removed: " + entry.getKey());
                    }
                }
            } catch (InterruptedException unused) {
                return;
            }
        }
    }

    private boolean isMagic(DatagramPacket datagramPacket) {
        if (datagramPacket.getLength() < SEND_QUEUE_MAX) {
            return false;
        }
        byte[] data = datagramPacket.getData();
        int offset = datagramPacket.getOffset();
        return data[offset] == 87 && data[offset + 1] == 70 && data[offset + 2] == 65 && data[offset + 3] == 85;
    }

    private void sendFormat(InetSocketAddress inetSocketAddress) {
        try {
            byte[] bArr = {87, 70, 65, 85, 1, 0, 0, 0, 0, 0, (byte) this.channels, 16, (byte) this.mode, 1, (byte) (iSenderLatencyMs & 255), (byte) ((iSenderLatencyMs >> 8) & 255)};
            putIntLE(bArr, 6, this.sampleRate);
            int iSenderLatencyMs = Status.senderLatencyMs();
            sendTo(inetSocketAddress, bArr);
        } catch (Throwable unused) {
        }
    }

    @Override // com.wifiaudio.Mixer.Sink
    public void onPcm(byte[] bArr, int i) {
        if (!this.running || this.targets.isEmpty()) {
            return;
        }
        broadcast(bArr, i * 2 * this.channels);
    }

    private void broadcast(byte[] bArr, int i) {
        if (i <= MAX_PAYLOAD_BYTES) {
            broadcastChunk(bArr, 0, i);
            return;
        }
        int i2 = this.channels * 2;
        int iMax = Math.max(i2, (MAX_PAYLOAD_BYTES / i2) * i2);
        for (int i3 = 0; i3 < i; i3 += iMax) {
            broadcastChunk(bArr, i3, Math.min(iMax, i - i3));
        }
    }

    private void broadcastChunk(byte[] bArr, int i, int i2) {
        for (InetSocketAddress inetSocketAddress : this.targets.keySet()) {
            try {
                int iNextSeq = nextSeq(inetSocketAddress);
                int i3 = i2 + SEND_QUEUE_MAX;
                byte[] bArr2 = new byte[i3];
                bArr2[0] = (byte) (iNextSeq >> 24);
                bArr2[1] = (byte) (iNextSeq >> 16);
                bArr2[2] = (byte) (iNextSeq >> 8);
                bArr2[3] = (byte) iNextSeq;
                System.arraycopy(bArr, i, bArr2, SEND_QUEUE_MAX, i2);
                enqueue(new DatagramPacket(bArr2, i3, inetSocketAddress.getAddress(), inetSocketAddress.getPort()));
            } catch (Throwable unused) {
            }
        }
    }

    private synchronized int nextSeq(InetSocketAddress inetSocketAddress) {
        int iIntValue;
        Integer num = this.seqs.get(inetSocketAddress);
        iIntValue = num == null ? 0 : num.intValue() + 1;
        this.seqs.put(inetSocketAddress, Integer.valueOf(iIntValue));
        return iIntValue;
    }

    private void sendTo(InetSocketAddress inetSocketAddress, byte[] bArr) throws IOException {
        this.socket.send(new DatagramPacket(bArr, bArr.length, inetSocketAddress.getAddress(), inetSocketAddress.getPort()));
    }

    public void stop() {
        this.running = false;
        DatagramSocket datagramSocket = this.socket;
        if (datagramSocket != null) {
            datagramSocket.close();
        }
        Thread thread = this.recvThread;
        if (thread != null) {
            thread.interrupt();
        }
        Thread thread2 = this.hbThread;
        if (thread2 != null) {
            thread2.interrupt();
        }
        Thread thread3 = this.sendThread;
        if (thread3 != null) {
            thread3.interrupt();
        }
        synchronized (this.sendQ) {
            this.sendQ.clear();
            this.sendQ.notifyAll();
        }
        this.targets.clear();
        Status.setUdpClients(0);
        this.seqs.clear();
    }

    private static void putIntLE(byte[] bArr, int i, int i2) {
        bArr[i] = (byte) (i2 & 255);
        bArr[i + 1] = (byte) ((i2 >> 8) & 255);
        bArr[i + 2] = (byte) ((i2 >> 16) & 255);
        bArr[i + 3] = (byte) ((i2 >> 24) & 255);
    }
}
