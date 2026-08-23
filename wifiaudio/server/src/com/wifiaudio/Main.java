package com.wifiaudio;

import android.content.Context;
import android.os.Process;

/**
 * WiFiAudio 捕获服务入口。
 * 由 KSU/Magisk 模块以 root 身份通过 app_process 启动。
 *
 * 管线：
 *   AudioLoopbackCapture (系统音频回环捕获) ─┐
 *   CallCapture (蜂窝通话, 可选) ────────────┼→ Mixer → StreamServer(TCP) / UdpServer(UDP)
 *                                            └→ OpusEncoder / AacEncoder(可选) → 同上
 *
 * 编码器回退链：配置 OPUS → 系统无 OPUS 编码器则回退 AAC → 再无则 PCM。
 */
public final class Main {

    private static final int CODEC_PCM = 0;
    private static final int CODEC_AAC = 1;
    private static final int CODEC_OPUS = 2;

    public static void main(String[] args) {
        // 关键：ActivityThread 构造内部会创建 Handler，需要当前线程有 Looper。
        // app_process 的 main 线程默认没有 Looper，必须先 prepare。
        try {
            if (android.os.Looper.myLooper() == null) {
                android.os.Looper.prepareMainLooper();
            }
        } catch (Throwable t) {
            System.err.println("Looper prepare failed: " + t);
        }

        String configPath = "/data/adb/modules/wifiaudio/config.conf";
        for (int i = 0; i < args.length; i++) {
            if ("--config".equals(args[i]) && i + 1 < args.length) {
                configPath = args[i + 1];
            }
        }

        Config config = new Config(configPath);
        String dir = config.getDir();
        // 日志写到用户可访问目录（/storage/emulated/0/WiFiAudio/capture.log）
        Util.setLogFile("/storage/emulated/0/WiFiAudio/capture.log");

        // 写 PID 供控制脚本使用
        Util.writeFileAtomic(dir + "/capture.pid", String.valueOf(Process.myPid()));
        Status.setStatusFile(dir + "/status.json");
        Status.pid = Process.myPid();
        Status.save();

        Util.log("Main", "WiFiAudio capture service starting (pid=" + Process.myPid()
                + ", config=" + configPath + ")");

        // ===== step 1: 初始化 Android framework 上下文（主线程！）=====
        Util.log("Main", "step1/5: FakeContext.apply() ...");
        try {
            FakeContext.apply();
            Util.log("Main", "step1 done, context usable=" + FakeContext.get().usable());
        } catch (Throwable t) {
            Util.log("Main", "ERROR: FakeContext.apply()", t);
        }

        final Context context = FakeContext.get();
        if (!((FakeContext) context).usable()) {
            Status.lastError = "无法获取系统 Context，服务无法启动（详见 capture.log）";
            Util.log("Main", "FATAL: FakeContext base is null", FakeContext.getInitError());
            Status.save();
            System.exit(1);
            return;
        }
        final Mixer mixer = new Mixer();

        final boolean wantOpus = config.useOpus();
        final boolean wantAac = config.useAac();
        final boolean useUdp = config.useUdp(); // 传输协议仅由 transport 配置决定（模式不再强制 UDP）
        final int mode = config.gameMode() ? 1 : 0;

        // ===== step 2: 系统音频回环捕获 =====
        // PCM 模式支持高采样率（auto=跟随设备能力 / 48k / 96k / 192k）；OPUS/AAC 编码器仅支持 48k，强制 48k
        final AudioLoopbackCapture loopback = new AudioLoopbackCapture(config.localRender());
        final int loopbackTargetRate = (wantOpus || wantAac) ? 48000 : config.pcmRate();
        Util.log("Main", "step2/5: AudioPolicy loopback capture start (targetRate="
                + (loopbackTargetRate > 0 ? loopbackTargetRate + "Hz" : "auto/设备能力") + ") ...");
        final Throwable[] captureErr = new Throwable[1];
        if (!runWithTimeout("loopback.start", 20000, new Runnable() {
            @Override
            public void run() {
                try {
                    loopback.start(context, loopbackTargetRate);
                } catch (Throwable t) {
                    captureErr[0] = t;
                }
            }
        })) {
            captureErr[0] = new RuntimeException("loopback.start timeout (>20s)");
        }
        if (captureErr[0] != null && loopbackTargetRate != 48000) {
            // 高采样率请求失败（老设备/特殊 ROM 对 loopback 格式限制严格）→ 回退 48k 重试
            Util.log("Main", "loopback @" + loopbackTargetRate + " 失败: "
                    + Util.causeMsg(captureErr[0]) + " → 回退 48kHz 重试");
            captureErr[0] = null;
            if (!runWithTimeout("loopback.start.retry48", 20000, new Runnable() {
                @Override
                public void run() {
                    try {
                        loopback.start(context, 48000);
                    } catch (Throwable t) {
                        captureErr[0] = t;
                    }
                }
            })) {
                captureErr[0] = new RuntimeException("loopback.start retry timeout (>20s)");
            }
        }
        if (captureErr[0] != null) {
            Status.lastError = "Loopback capture failed: " + Util.causeMsg(captureErr[0]);
            Util.log("Main", "FATAL: loopback capture failed", captureErr[0]);
            Status.save();
            System.exit(1);
            return;
        }
        Status.running = true;
        Status.startTime = System.currentTimeMillis();
        Status.sampleRate = loopback.getSampleRate();
        Status.channels = AudioLoopbackCapture.CHANNELS;
        Util.log("Main", "step2 done: loopback capture started " + Status.sampleRate + "Hz "
                + Status.channels + "ch PCM16 (requested="
                + (loopbackTargetRate > 0 ? loopbackTargetRate : "auto") + "), localRender=" + config.localRender());

        // ===== step 2.5: 编码器初始化（回退链：OPUS → AAC → PCM）=====
        // 服务器引用（编码器回调需要，但服务器在 step3 才创建）
        final StreamServer[] tcpRef = new StreamServer[1];
        final HttpServer[] httpRef = new HttpServer[1];
        final UdpServer[] udpRef = new UdpServer[1];

        int codecType = CODEC_PCM;
        final OpusEncoder[] opusRef = new OpusEncoder[1];
        final AacEncoder[] aacRef = new AacEncoder[1];

        if (wantOpus) {
            Util.log("Main", "step2.5: OPUS encoder init (system MediaCodec) ...");
            opusRef[0] = new OpusEncoder(config.opusBitrate(), new OpusEncoder.Listener() {
                @Override
                public void onOpusFrame(byte[] frame, int len) {
                    Status.encoderFrames++;
                    Status.encoderActive = true;
                    if (tcpRef[0] != null) tcpRef[0].onOpus(frame, len);
                    if (httpRef[0] != null) httpRef[0].onOpus(frame, len);
                    if (udpRef[0] != null) udpRef[0].onOpus(frame, len);
                }
            });
            if (opusRef[0].ok()) {
                codecType = CODEC_OPUS;
                Util.log("Main", "OPUS encoder active (" + config.opusBitrate() + "kbps)");
            } else {
                Util.log("Main", "OPUS encoder unavailable, falling back");
                opusRef[0] = null;
            }
        }
        if (codecType == CODEC_PCM && wantAac) {
            Util.log("Main", "step2.5: AAC encoder init ...");
            aacRef[0] = new AacEncoder(config.aacBitrate(), new AacEncoder.Listener() {
                @Override
                public void onAacFrame(byte[] adts, int len) {
                    Status.encoderFrames++;
                    Status.encoderActive = true;
                    if (tcpRef[0] != null) tcpRef[0].onAac(adts, len);
                    if (httpRef[0] != null) httpRef[0].onAac(adts, len);
                    if (udpRef[0] != null) udpRef[0].onAac(adts, len);
                }
            });
            if (aacRef[0].ok()) {
                codecType = CODEC_AAC;
                Util.log("Main", "AAC encoder active (" + config.aacBitrate() + "kbps)");
            } else {
                Util.log("Main", "AAC encoder unavailable, falling back to PCM");
                aacRef[0] = null;
            }
        }
        if (codecType == CODEC_PCM && (wantOpus || wantAac)) {
            Util.log("Main", "WARNING: 配置了压缩编码但不可用，已回退 PCM 无损直通");
            Status.lastError = "编码器不可用，已回退 PCM";
        }
        Status.codec = codecType == CODEC_OPUS ? "opus" : (codecType == CODEC_AAC ? "aac" : "pcm");
        // 实时比特率：PCM=采样率×16bit×声道；OPUS/AAC=配置码率
        if (codecType == CODEC_OPUS) {
            Status.bitrate = config.opusBitrate() * 1000L;
        } else if (codecType == CODEC_AAC) {
            Status.bitrate = config.aacBitrate() * 1000L;
        } else {
            Status.bitrate = (long) Status.sampleRate * 16 * Status.channels;
        }

        // ===== step 3: 网络服务 =====
        final StreamServer tcpServer = new StreamServer(config.tcpPort(), codecType,
                Status.sampleRate, Status.channels, mode, useUdp);
        final HttpServer httpServer = new HttpServer(config.httpPort(), codecType,
                Status.sampleRate, Status.channels);
        final UdpServer udpServer = new UdpServer(config.tcpPort(), codecType,
                Status.sampleRate, Status.channels, mode);
        tcpRef[0] = tcpServer;
        httpRef[0] = httpServer;
        udpRef[0] = udpServer;
        // OPUS 模式：把 OpusHead 提供给 TCP 服务器（连接时下发）
        if (opusRef[0] != null && opusRef[0].ok()) {
            tcpServer.setOpusHead(opusRef[0].getOpusHead());
        }

        Util.log("Main", "step3/5: network servers start (tcp=" + config.tcpPort()
                + " http=" + config.httpPort() + " udp=" + udpServer.getPort() + ") ...");
        try {
            tcpServer.start();
            httpServer.start();
            // UDP 服务器始终启动（接收端可随时切换 TCP/UDP，无需重配发送端）
            udpServer.start();
            Status.tcpPort = config.tcpPort();
            Status.httpPort = config.httpPort();
            Status.udpPort = config.tcpPort() + UdpServer.UDP_PORT_OFFSET;
            Status.mode = mode == 1 ? "game" : "music";
            Status.transport = useUdp ? "udp" : "tcp";
        } catch (Throwable t) {
            Status.lastError = "Network server failed: " + Util.causeMsg(t);
            Util.log("Main", "FATAL: network server failed", t);
            Status.save();
            System.exit(1);
            return;
        }
        Util.log("Main", "step3 done");

        // 局域网发现广播
        final DiscoveryServer discovery = new DiscoveryServer(config.tcpPort());
        discovery.start();

        // step 4: 混音分发（PCM 直通 + 编码器）
        Util.log("Main", "step4/5: mixer setup (codec=" + Status.codec + ") ...");
        mixer.addSink(tcpServer);
        mixer.addSink(httpServer);
        mixer.addSink(udpServer);
        if (opusRef[0] != null && opusRef[0].ok()) {
            mixer.addSink(opusRef[0]);
        }
        if (aacRef[0] != null && aacRef[0].ok()) {
            mixer.addSink(aacRef[0]);
        }
        Util.log("Main", "step4 done");

        // step 5: 蜂窝通话捕获（可选，尽力而为）
        Util.log("Main", "step5/5: call capture init (enabled=" + config.captureCall() + ") ...");
        final CallCapture callCapture;
        if (config.captureCall()) {
            callCapture = new CallCapture(context, mixer);
            Status.callCaptureSupported = callCapture.probe();
            if (Status.callCaptureSupported) {
                callCapture.start();
            }
        } else {
            callCapture = null;
        }
        Util.log("Main", "step5 done");

        // 定时自动关闭（省电）
        final int autoOffMin = config.autoOffMinutes();
        if (autoOffMin > 0) {
            final Thread offThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(autoOffMin * 60000L);
                    } catch (InterruptedException e) {
                        return;
                    }
                    Util.log("Main", "定时关闭触发（" + autoOffMin + " 分钟）");
                    System.exit(0);
                }
            }, "auto-off");
            offThread.setDaemon(true);
            offThread.start();
            Util.log("Main", "自动关闭已设置: " + autoOffMin + " 分钟后停止");
        }

        // 捕获读取线程
        final byte[] buf = new byte[AudioLoopbackCapture.MAX_READ_BYTES];
        Thread captureThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Status.running) {
                    int n = loopback.read(buf);
                    if (n < 0) {
                        Util.log("Main", "loopback read ended");
                        break;
                    }
                    if (n > 0) {
                        mixer.mixLoopback(buf, n);
                        Status.bytesSent += n;
                    }
                }
            }
        }, "loopback-read");
        captureThread.start();

        // 状态定期刷新线程
        Thread statusThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Status.running) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        break;
                    }
                    Status.save();
                }
            }
        }, "status-writer");
        statusThread.start();

        // 优雅退出
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                Util.log("Main", "shutting down...");
                Status.running = false;
                if (callCapture != null) callCapture.stop();
                discovery.stop();
                if (opusRef[0] != null) opusRef[0].stop();
                if (aacRef[0] != null) aacRef[0].stop();
                udpServer.stop();
                tcpServer.stop();
                httpServer.stop();
                loopback.stop();
                Status.running = false;
                Status.save();
            }
        }));

        Util.log("Main", "service ready. TCP=" + Status.tcpPort + " UDP=" + udpServer.getPort()
                + " HTTP=" + Status.httpPort + " codec=" + Status.codec
                + " mode=" + (mode == 1 ? "game" : "music")
                + " transport=" + (useUdp ? "udp" : "tcp")
                + " ip=" + Util.getLocalIp());
        Status.save();

        // 保持主线程存活
        try {
            while (Status.running) {
                Thread.sleep(60000);
            }
        } catch (InterruptedException ignored) {
        }
        Util.log("Main", "service exited");
    }

    /**
     * 在子线程执行任务并等待；超时返回 false（防止阻塞主流程导致"卡死"）。
     */
    private static boolean runWithTimeout(final String name, long timeoutMs, final Runnable task) {
        final Throwable[] err = new Throwable[1];
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } catch (Throwable e) {
                    err[0] = e;
                }
            }
        }, "init-" + name);
        t.start();
        try {
            t.join(timeoutMs);
        } catch (InterruptedException e) {
            return false;
        }
        if (t.isAlive()) {
            Util.log("Main", "TIMEOUT: " + name + " blocked >" + timeoutMs + "ms");
            return false;
        }
        if (err[0] != null) {
            Util.log("Main", "ERROR in " + name, err[0]);
            return false;
        }
        return true;
    }
}
