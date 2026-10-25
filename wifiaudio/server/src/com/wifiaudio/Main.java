package com.wifiaudio;

import android.os.Looper;
import android.os.Process;
import java.io.IOException;
import org.json.JSONException;

/* loaded from: classes.dex */
public final class Main {
    public static void main(String[] strArr) throws JSONException, InterruptedException, IOException {
        final CallCapture callCapture;
        int i;
        try {
            if (Looper.myLooper() == null) {
                Looper.prepareMainLooper();
            }
        } catch (Throwable th) {
            System.err.println("Looper prepare failed: " + th);
        }
        String str = "/data/adb/modules/wifiaudio/config.conf";
        for (int i2 = 0; i2 < strArr.length; i2++) {
            if ("--config".equals(strArr[i2]) && (i = i2 + 1) < strArr.length) {
                str = strArr[i];
            }
        }
        Config config = new Config(str);
        String dir = config.getDir();
        Util.setLogFile("/storage/emulated/0/WiFiAudio/capture.log");
        Util.writeFileAtomic(dir + "/capture.pid", String.valueOf(Process.myPid()));
        Status.setStatusFile(dir + "/status.json");
        Status.pid = Process.myPid();
        Status.save();
        Util.log("Main", "WiFiAudio capture service starting (pid=" + Process.myPid() + ", config=" + str + ")");
        Util.log("Main", "step1/5: FakeContext.apply() ...");
        try {
            FakeContext.apply();
            Util.log("Main", "step1 done, context usable=" + FakeContext.get().usable());
        } catch (Throwable th2) {
            Util.log("Main", "ERROR: FakeContext.apply()", th2);
        }
        final FakeContext fakeContext = FakeContext.get();
        if (!fakeContext.usable()) {
            Status.lastError = "无法获取系统 Context，服务无法启动（详见 capture.log）";
            Util.log("Main", "FATAL: FakeContext base is null", FakeContext.getInitError());
            Status.save();
            System.exit(1);
            return;
        }
        final Mixer mixer = new Mixer();
        boolean zUseUdp = config.useUdp();
        boolean zGameMode = config.gameMode();
        final AudioLoopbackCapture audioLoopbackCapture = new AudioLoopbackCapture(config.localRender());
        final int iPcmRate = config.pcmRate();
        StringBuilder sb = new StringBuilder("step2/5: AudioPolicy loopback capture start (targetRate=");
        sb.append(iPcmRate > 0 ? iPcmRate + "Hz" : "auto/设备能力");
        sb.append(") ...");
        Util.log("Main", sb.toString());
        final Throwable[] thArr = new Throwable[1];
        if (!runWithTimeout("loopback.start", 20000L, new Runnable() { // from class: com.wifiaudio.Main.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    audioLoopbackCapture.start(fakeContext, iPcmRate);
                } catch (Throwable th3) {
                    thArr[0] = th3;
                }
            }
        })) {
            thArr[0] = new RuntimeException("loopback.start timeout (>20s)");
        }
        if (thArr[0] != null && iPcmRate != 48000) {
            Util.log("Main", "loopback @" + iPcmRate + " 失败: " + Util.causeMsg(thArr[0]) + " → 回退 48kHz 重试");
            thArr[0] = null;
            if (!runWithTimeout("loopback.start.retry48", 20000L, new Runnable() { // from class: com.wifiaudio.Main.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        audioLoopbackCapture.start(fakeContext, 48000);
                    } catch (Throwable th3) {
                        thArr[0] = th3;
                    }
                }
            })) {
                thArr[0] = new RuntimeException("loopback.start retry timeout (>20s)");
            }
        }
        if (thArr[0] != null) {
            Status.lastError = "Loopback capture failed: " + Util.causeMsg(thArr[0]);
            Util.log("Main", "FATAL: loopback capture failed", thArr[0]);
            Status.save();
            System.exit(1);
            return;
        }
        Status.running = true;
        Status.startTime = System.currentTimeMillis();
        Status.sampleRate = audioLoopbackCapture.getSampleRate();
        Status.channels = 2;
        StringBuilder sb2 = new StringBuilder("step2 done: loopback capture started ");
        sb2.append(Status.sampleRate);
        sb2.append("Hz ");
        sb2.append(Status.channels);
        sb2.append("ch PCM16 (requested=");
        sb2.append(iPcmRate > 0 ? Integer.valueOf(iPcmRate) : "auto");
        sb2.append("), localRender=");
        sb2.append(config.localRender());
        Util.log("Main", sb2.toString());
        Status.codec = "pcm";
        Status.bitrate = Status.sampleRate * 16 * Status.channels;
        final StreamServer streamServer = new StreamServer(config.tcpPort(), Status.sampleRate, Status.channels, zGameMode ? 1 : 0, zUseUdp);
        final HttpServer httpServer = new HttpServer(config.httpPort(), Status.sampleRate, Status.channels);
        final UdpServer udpServer = new UdpServer(config.tcpPort(), Status.sampleRate, Status.channels, zGameMode ? 1 : 0);
        Util.log("Main", "step3/5: network servers start (tcp=" + config.tcpPort() + " http=" + config.httpPort() + " udp=" + udpServer.getPort() + ") ...");
        try {
            streamServer.start();
            httpServer.start();
            udpServer.start();
            Status.tcpPort = config.tcpPort();
            Status.httpPort = config.httpPort();
            Status.udpPort = config.tcpPort() + UdpServer.UDP_PORT_OFFSET;
            Status.mode = zGameMode ? "game" : "music";
            Status.transport = zUseUdp ? "udp" : "tcp";
            Util.log("Main", "step3 done");
            final DiscoveryServer discoveryServer = new DiscoveryServer(config.tcpPort());
            discoveryServer.start();
            Util.log("Main", "step4/5: mixer setup (codec=" + Status.codec + ") ...");
            mixer.addSink(streamServer);
            mixer.addSink(httpServer);
            mixer.addSink(udpServer);
            Util.log("Main", "step4 done");
            Util.log("Main", "step5/5: call capture init (enabled=" + config.captureCall() + ") ...");
            if (config.captureCall()) {
                CallCapture callCapture2 = new CallCapture(fakeContext, mixer);
                Status.callCaptureSupported = callCapture2.probe();
                if (Status.callCaptureSupported) {
                    callCapture2.start();
                }
                callCapture = callCapture2;
            } else {
                callCapture = null;
            }
            Util.log("Main", "step5 done");
            final int iAutoOffMinutes = config.autoOffMinutes();
            if (iAutoOffMinutes > 0) {
                Thread thread = new Thread(new Runnable() { // from class: com.wifiaudio.Main.3
                    @Override // java.lang.Runnable
                    public void run() throws InterruptedException {
                        try {
                            Thread.sleep(iAutoOffMinutes * 60000);
                            Util.log("Main", "定时关闭触发（" + iAutoOffMinutes + " 分钟）");
                            System.exit(0);
                        } catch (InterruptedException unused) {
                        }
                    }
                }, "auto-off");
                thread.setDaemon(true);
                thread.start();
                Util.log("Main", "自动关闭已设置: " + iAutoOffMinutes + " 分钟后停止");
            }
            final byte[] bArr = new byte[AudioLoopbackCapture.MAX_READ_BYTES];
            new Thread(new Runnable() { // from class: com.wifiaudio.Main.4
                @Override // java.lang.Runnable
                public void run() {
                    while (Status.running) {
                        int i3 = audioLoopbackCapture.read(bArr);
                        if (i3 < 0) {
                            Util.log("Main", "loopback read ended");
                            return;
                        } else if (i3 > 0) {
                            mixer.mixLoopback(bArr, i3);
                            Status.bytesSent += i3;
                            Status.captureLatencyMs = audioLoopbackCapture.getCaptureLatencyMs();
                            Status.recordBufferFrames = audioLoopbackCapture.getRecordBufferFrames();
                        }
                    }
                }
            }, "loopback-read").start();
            new Thread(new Runnable() { // from class: com.wifiaudio.Main.5
                @Override // java.lang.Runnable
                public void run() throws JSONException, InterruptedException, IOException {
                    while (Status.running) {
                        try {
                            Thread.sleep(2000L);
                            Status.save();
                        } catch (InterruptedException unused) {
                            return;
                        }
                    }
                }
            }, "status-writer").start();
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() { // from class: com.wifiaudio.Main.6
                @Override // java.lang.Runnable
                public void run() throws JSONException, IOException {
                    Util.log("Main", "shutting down...");
                    Status.running = false;
                    CallCapture callCapture3 = callCapture;
                    if (callCapture3 != null) {
                        callCapture3.stop();
                    }
                    discoveryServer.stop();
                    udpServer.stop();
                    streamServer.stop();
                    httpServer.stop();
                    mixer.stop();
                    audioLoopbackCapture.stop();
                    Status.running = false;
                    Status.save();
                }
            }));
            StringBuilder sb3 = new StringBuilder("service ready. TCP=");
            sb3.append(Status.tcpPort);
            sb3.append(" UDP=");
            sb3.append(udpServer.getPort());
            sb3.append(" HTTP=");
            sb3.append(Status.httpPort);
            sb3.append(" codec=");
            sb3.append(Status.codec);
            sb3.append(" mode=");
            sb3.append(zGameMode ? "game" : "music");
            sb3.append(" transport=");
            sb3.append(zUseUdp ? "udp" : "tcp");
            sb3.append(" ip=");
            sb3.append(Util.getLocalIp());
            Util.log("Main", sb3.toString());
            Status.save();
            while (Status.running) {
                try {
                    Thread.sleep(60000L);
                } catch (InterruptedException unused) {
                }
            }
            Util.log("Main", "service exited");
        } catch (Throwable th3) {
            Status.lastError = "Network server failed: " + Util.causeMsg(th3);
            Util.log("Main", "FATAL: network server failed", th3);
            Status.save();
            System.exit(1);
        }
    }

    private static boolean runWithTimeout(String str, long j, final Runnable runnable) throws InterruptedException {
        final Throwable[] thArr = new Throwable[1];
        Thread thread = new Thread(new Runnable() { // from class: com.wifiaudio.Main.7
            @Override // java.lang.Runnable
            public void run() {
                try {
                    runnable.run();
                } catch (Throwable th) {
                    thArr[0] = th;
                }
            }
        }, "init-" + str);
        thread.start();
        try {
            thread.join(j);
            if (thread.isAlive()) {
                Util.log("Main", "TIMEOUT: " + str + " blocked >" + j + "ms");
                return false;
            }
            if (thArr[0] == null) {
                return true;
            }
            Util.log("Main", "ERROR in " + str, thArr[0]);
            return false;
        } catch (InterruptedException unused) {
            return false;
        }
    }
}
