package com.wifiaudio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/* loaded from: classes.dex */
public final class DiscoveryServer {
    public static final int DISCOVERY_PORT = 47802;
    private volatile boolean running = false;
    private final int tcpPort;
    private Thread thread;

    public DiscoveryServer(int i) {
        this.tcpPort = i;
    }

    public void start() {
        this.running = true;
        Thread thread = new Thread(new Runnable() { // from class: com.wifiaudio.DiscoveryServer.1
            @Override // java.lang.Runnable
            public void run() {
                DiscoveryServer.this.loop();
            }
        }, "discovery");
        this.thread = thread;
        thread.start();
        Util.log("Disc", "发现广播已启动 (UDP 47802)");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loop() {
        DatagramSocket datagramSocket;
        Throwable th;
        try {
            datagramSocket = new DatagramSocket();
        } catch (Throwable th2) {
            datagramSocket = null;
            th = th2;
        }
        try {
            try {
                datagramSocket.setBroadcast(true);
                byte[] bArrBuildPayload = buildPayload();
                DatagramPacket datagramPacket = new DatagramPacket(bArrBuildPayload, bArrBuildPayload.length, InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT);
                while (this.running) {
                    try {
                        datagramSocket.send(datagramPacket);
                    } catch (Throwable unused) {
                    }
                    Thread.sleep(3000L);
                }
            } catch (Throwable th3) {
                th = th3;
                try {
                    Util.log("Disc", "discovery error: " + Util.causeMsg(th));
                    if (datagramSocket == null) {
                        return;
                    }
                    datagramSocket.close();
                } catch (Throwable th4) {
                    if (datagramSocket != null) {
                        try {
                            datagramSocket.close();
                        } catch (Throwable unused2) {
                        }
                    }
                    throw th4;
                }
            }
            datagramSocket.close();
        } catch (Throwable unused3) {
        }
    }

    private byte[] buildPayload() {
        return ("WFAU-DISCOVER|" + Util.getLocalIp() + "|" + this.tcpPort).getBytes();
    }

    public void stop() {
        this.running = false;
        Thread thread = this.thread;
        if (thread != null) {
            thread.interrupt();
            this.thread = null;
        }
    }
}
