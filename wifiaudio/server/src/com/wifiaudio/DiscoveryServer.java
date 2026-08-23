package com.wifiaudio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 局域网发现：周期性向 255.255.255.255:47802 广播自身信息，
 * 接收端 App 监听该端口即可自动发现发送端。
 */
public final class DiscoveryServer {

    public static final int DISCOVERY_PORT = 47802;

    private final int tcpPort;
    private volatile boolean running = false;
    private Thread thread;

    public DiscoveryServer(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public void start() {
        running = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loop();
            }
        }, "discovery");
        thread.start();
        Util.log("Disc", "发现广播已启动 (UDP " + DISCOVERY_PORT + ")");
    }

    private void loop() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] payload = buildPayload();
            InetAddress bc = InetAddress.getByName("255.255.255.255");
            DatagramPacket pkt = new DatagramPacket(payload, payload.length, bc, DISCOVERY_PORT);
            while (running) {
                try {
                    socket.send(pkt);
                } catch (Throwable ignored) {
                }
                Thread.sleep(3000);
            }
        } catch (Throwable t) {
            Util.log("Disc", "discovery error: " + Util.causeMsg(t));
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private byte[] buildPayload() {
        // 简单文本: WFAU-DISCOVER|<ip>|<tcpPort>
        String msg = "WFAU-DISCOVER|" + Util.getLocalIp() + "|" + tcpPort;
        return msg.getBytes();
    }

    public void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }
}
