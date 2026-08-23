package com.wifiaudio.receiver;

import com.wifiaudio.OpusDecoder;
import com.wifiaudio.AacDecoder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;

/**
 * 前台服务：连接发送端，接收音频流并播放。
 * 协议：
 *   TCP: 16字节头 + 连续音频数据
 *   UDP: 发送端 TCP端口+1000，发送"WFAU"请求/心跳，收 16字节格式头 + 4字节seq+音频包
 *   codec: 0=PCM 1=AAC 2=OPUS
 */
public class PlayerService extends Service {

    private static final String TAG = "WiFiAudioReceiver";
    private static final String CHANNEL_ID = "wifiaudio_playback";
    private static final int UDP_PORT_OFFSET = 1000;
    private static final byte[] MAGIC = {'W', 'F', 'A', 'U'};

    public static final String ACTION_CONNECT = "com.wifiaudio.receiver.CONNECT";
    public static final String ACTION_STOP = "com.wifiaudio.receiver.STOP";
    public static final String ACTION_TEST_TONE = "com.wifiaudio.receiver.TEST_TONE";
    public static final String ACTION_VOLUME = "com.wifiaudio.receiver.SET_VOLUME";
    public static final String EXTRA_IP = "ip";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_TRANSPORT = "transport"; // auto/tcp/udp
    public static final String EXTRA_BUFFER = "buffer";       // low/medium/high
    public static final String EXTRA_AAUDIO = "aaudio";       // 1=AAudio 输出
    public static final String EXTRA_DECODE_MODE = "decode_mode"; // auto/hard/soft
    public static final String EXTRA_DECODER_NAME = "decoder_name"; // null=自动
    public static final String EXTRA_VOLUME = "volume";          // 0-100
    public static final String EXTRA_AUTORECONNECT = "autoreconnect"; // 1=断线重连

    public static final String ACTION_STATE = "com.wifiaudio.receiver.STATE";
    public static final String EXTRA_STATE = "state";
    public static final String EXTRA_MSG = "msg";
    public static final String EXTRA_CODEC = "codec";
    public static final String EXTRA_RATE = "rate";
    public static final String EXTRA_CHANNELS = "channels";

    /** 实时统计广播（每秒一次） */
    public static final String ACTION_STATS = "com.wifiaudio.receiver.STATS";
    public static final String EXTRA_CUR_TRANSPORT = "cur_transport";
    public static final String EXTRA_BITRATE = "bitrate_kbps";
    public static final String EXTRA_AUDIO_MS = "audio_buffer_ms";
    public static final String EXTRA_JITTER_MS = "jitter_ms";
    public static final String EXTRA_LATENCY_MS = "latency_ms";
    public static final String EXTRA_RX_KB = "rx_kb";
    public static final String EXTRA_DEC_FRAMES = "dec_frames";
    public static final String EXTRA_TARGET_MS = "target_ms";   // PCM 自适应缓冲目标
    public static final String EXTRA_DROPPED = "dropped";       // 累计跳帧（帧）
    public static final String EXTRA_UNDERRUN = "underrun";     // 累计下溢次数
    public static final String EXTRA_OUTPUT = "output";         // AudioTrack / AAudio
    public static final String EXTRA_LOG = "log_lines";

    private static volatile StateListener stateListener;
    public interface StateListener {
        void onState(String state, String msg, String codec, int rate, int channels);
    }

    private volatile Thread worker;
    private volatile boolean running = false;
    private AudioTrack audioTrack;
    private MediaCodec codec;
    private DatagramSocket udpSocket;
    private Thread udpHeartbeat;
    private volatile AudioSink currentSink;
    private volatile DecoderEngine currentDecoder;
    private volatile AudioOut currentOut;
    private volatile PcmJitterBuffer currentJb;   // PCM 自适应抖动缓冲
    private volatile String aaudioMode = "0";
    private volatile String decodeMode = "auto";   // auto/hard/soft
    private volatile String decoderName = "";      // 指定解码器（空=自动）
    private volatile int volume = 100;
    private volatile boolean autoReconnect = false;
    private volatile boolean userStopped = false;
    // 统计
    private volatile String statTransport = "-";
    private volatile String statCodecName = "-";
    private volatile int statRate = 0;
    private volatile int statChannels = 0;
    private volatile int statJitterMs = 0;
    private Thread statsThread;
    // 日志缓冲（App 内显示 + 写文件）
    private static final java.util.List<String> logBuf = java.util.Collections.synchronizedList(new java.util.ArrayList<String>());
    private static File logFile;

    /** 初始化日志文件（App 专属外部目录，免权限） */
    public static void initLogFile(Context ctx) {
        try {
            File dir = ctx.getExternalFilesDir(null);
            if (dir != null) {
                logFile = new File(dir, "wifiaudio.log");
                // 轮转：超过 1MB 重写
                if (logFile.exists() && logFile.length() > 1024 * 1024) {
                    logFile.delete();
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static void addLog(String msg) {
        long ms = System.currentTimeMillis();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US);
        String line = "[" + sdf.format(new java.util.Date(ms)) + "] " + msg;
        logBuf.add(line);
        while (logBuf.size() > 300) logBuf.remove(0);
        Log.d(TAG, msg);
        if (logFile != null) {
            try {
                java.io.FileOutputStream fos = new java.io.FileOutputStream(logFile, true);
                fos.write((line + "\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
                fos.close();
            } catch (Throwable ignored) {
            }
        }
    }

    /** 供外部类（输出/解码引擎）写日志 */
    public static void logStatic(String msg) {
        addLog(msg);
    }

    public static void setStateListener(StateListener l) {
        stateListener = l;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();
        initLogFile(this);
        addLog("======== WiFiAudio 接收端启动 ========");
        startForeground(1, buildNotification("正在准备…"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;
        String action = intent.getAction();
        if (ACTION_STOP.equals(action)) {
            stopPlayback();
            stopSelf();
            return START_NOT_STICKY;
        }
        if (ACTION_TEST_TONE.equals(action)) {
            playTestTone();
            return START_NOT_STICKY;
        }
        if (ACTION_VOLUME.equals(action)) {
            volume = intent.getIntExtra(EXTRA_VOLUME, 100);
            // 实时应用到当前输出
            if (currentOut != null) {
                try {
                    currentOut.setVolume(volume);
                } catch (Throwable ignored) {
                }
            }
            if (currentSink != null) {
                try {
                    currentSink.setVolume(volume);
                } catch (Throwable ignored) {
                }
            }
            return START_NOT_STICKY;
        }
        if (ACTION_CONNECT.equals(action)) {
            final String ip = intent.getStringExtra(EXTRA_IP);
            final int port = intent.getIntExtra(EXTRA_PORT, 47800);
            final String transport = intent.getStringExtra(EXTRA_TRANSPORT);
            final String buffer = intent.getStringExtra(EXTRA_BUFFER);
            aaudioMode = intent.getStringExtra(EXTRA_AAUDIO);
            decodeMode = intent.getStringExtra(EXTRA_DECODE_MODE);
            if (decodeMode == null) decodeMode = "auto";
            decoderName = intent.getStringExtra(EXTRA_DECODER_NAME);
            if (decoderName == null) decoderName = "";
            volume = intent.getIntExtra(EXTRA_VOLUME, 100);
            autoReconnect = "1".equals(intent.getStringExtra(EXTRA_AUTORECONNECT));
            startPlayback(ip, port, transport, buffer);
        }
        return START_NOT_STICKY;
    }

    private synchronized void startPlayback(final String ip, final int port,
                                            final String transport, final String buffer) {
        stopPlayback();
        userStopped = false;
        running = true;
        worker = new Thread(new Runnable() {
            @Override
            public void run() {
                connectAndPlay(ip, port, transport, buffer);
            }
        }, "wifiaudio-player");
        worker.start();
    }

    private synchronized void stopPlayback() {
        userStopped = true;
        running = false;
        if (worker != null) {
            worker.interrupt();
            worker = null;
        }
        if (statsThread != null) {
            statsThread.interrupt();
            statsThread = null;
        }
        if (udpSocket != null) {
            try { udpSocket.close(); } catch (Throwable ignored) {}
            udpSocket = null;
        }
        if (udpHeartbeat != null) {
            udpHeartbeat.interrupt();
            udpHeartbeat = null;
        }
        if (currentJb != null) {
            currentJb.close();
            currentJb = null;
        }
        if (currentSink != null) {
            currentSink.release();
            currentSink = null;
        }
        if (currentDecoder != null) {
            try {
                currentDecoder.release();
            } catch (Throwable ignored) {
            }
            currentDecoder = null;
        }
        if (currentOut != null) {
            try {
                currentOut.close();
            } catch (Throwable ignored) {
            }
            currentOut = null;
        }
        updateNotification("已停止");
        emit("stopped", "已停止", null, 0, 0);
    }

    private void connectAndPlay(String ip, int port, String transport, String buffer) {
        Socket socket = null;
        try {
            addLog("正在连接 " + ip + ":" + port);
            emit("connecting", "正在连接 " + ip + ":" + port + " …", null, 0, 0);
            updateNotification("正在连接 " + ip + "…");
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 6000);
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(10000);
            InputStream in = socket.getInputStream();

            byte[] header = new byte[16];
            if (!readFully(in, header)) {
                emit("error", "连接已断开（未收到数据）", null, 0, 0);
                return;
            }
            if (header[0] != 'W' || header[1] != 'F' || header[2] != 'A' || header[3] != 'U') {
                emit("error", "协议不匹配：目标不是 WiFiAudio 发送端", null, 0, 0);
                return;
            }
            int codecType = header[5] & 0xFF;
            int rate = (int) leInt(header, 6);
            int channels = header[10] & 0xFF;
            if (channels <= 0) channels = 2;
            int mode = header[12] & 0xFF;         // 0=music 1=game
            int serverTransport = header[13] & 0xFF; // 0=tcp 1=udp
            String codecName = codecName(codecType);
            addLog("协议头: codec=" + codecName + " rate=" + rate + " ch=" + channels
                    + " mode=" + (mode == 1 ? "game" : "music") + " serverTransport=" + (serverTransport == 1 ? "udp" : "tcp"));

            // 决定传输方式
            boolean useUdp;
            if ("tcp".equals(transport)) useUdp = false;
            else if ("udp".equals(transport)) useUdp = true;
            else useUdp = (serverTransport == 1); // auto 跟随发送端

            emit("playing", "已连接 " + rate + "Hz/" + channels + "ch " + codecName
                    + (useUdp ? " (UDP)" : " (TCP)"), codecName, rate, channels);
            updateNotification("正在播放 " + rate + "Hz " + codecName + (useUdp ? " UDP" : ""));

            // 设置统计信息并启动统计线程
            statTransport = useUdp ? "UDP" : "TCP";
            statCodecName = codecName;
            statRate = rate;
            statChannels = channels;
            statJitterMs = 0;
            startStatsThread();

            if (useUdp) {
                socket.close();
                socket = null;
                playUdp(ip, port, codecType, rate, channels, buffer, mode);
            } else {
                playTcpStream(in, codecType, rate, channels, buffer, mode);
            }
        } catch (Exception e) {
            if (running) {
                Log.e(TAG, "playback error", e);
                emit("error", "错误: " + e.getMessage(), null, 0, 0);
                updateNotification("连接失败");
            }
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException ignored) {
            }
        }
        // 断线自动重连（用户未主动断开时）
        if (running && autoReconnect && !userStopped) {
            addLog("连接断开，3 秒后自动重连...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            if (running && !userStopped) {
                connectAndPlay(ip, port, transport, buffer);
            }
        }
    }

    // ============ TCP 流播放 ============

    private void playTcpStream(InputStream in, int codecType, int rate, int channels,
                               String buffer, int mode) throws Exception {
        if (codecType == 0) {
            playPcmStream(in, rate, channels, buffer, mode);
        } else {
            playEncodedStream(in, codecType, rate, channels, buffer, mode);
        }
    }

    /** PCM 无损直通（自适应抖动缓冲：预缓冲 + 动态水位 + 过载丢帧 + 漂移伺服） */
    private void playPcmStream(InputStream in, int rate, int channels, String buffer, int mode) throws Exception {
        AudioSink sink = createSink(0, rate, channels, buffer, mode);
        currentSink = sink;
        sink.rate = rate;
        sink.channels = channels;
        sink.setVolume(volume);
        emit("playing", "PCM 直通 " + rate + "Hz/" + channels + "ch", "PCM", rate, channels);
        int baseMs = computeBufferMs(buffer, mode);
        boolean adaptive = buffer == null || !buffer.startsWith("custom:");
        PcmJitterBuffer jb = new PcmJitterBuffer(sink, rate, channels, baseMs, adaptive);
        currentJb = jb;
        addLog("PCM 自适应缓冲启动: 目标 " + baseMs + "ms"
                + (adaptive ? "（自动调节 " + jb.minMs + "-" + jb.maxMs + "ms）" : "（自定义固定）"));
        jb.start();
        byte[] buf = new byte[4096 * channels];
        while (running) {
            int n = in.read(buf);
            if (n < 0) break;
            if (n > 0) jb.write(buf, n);
        }
        jb.close();
        sink.release();
        if (currentJb == jb) currentJb = null;
        if (currentSink == sink) currentSink = null;
        emit("stopped", "流已结束", null, 0, 0);
    }

    /** 压缩编码流：异步硬解优先，失败自动切换软解；输出 AudioTrack/AAudio 可切换 */
    private void playEncodedStream(InputStream in, int codecType, int rate, int channels,
                                   String buffer, int mode) throws Exception {
        boolean isAac = codecType == 1;
        byte[] first = new byte[8192];
        int firstLen = -1;
        boolean prefixMode = true;
        byte[] opusHead = null;

        // 读首帧
        int hi = in.read();
        int lo = in.read();
        if (hi < 0 || lo < 0) {
            emit("stopped", "流已结束", null, 0, 0);
            return;
        }
        if (isAac) {
            // AAC 双模式：前缀（新）或 ADTS（旧）
            prefixMode = (hi & 0xFF) <= 0x20;
            if (prefixMode) {
                firstLen = (hi << 8) | lo;
            } else {
                byte[] head = new byte[7];
                head[0] = (byte) hi;
                head[1] = (byte) lo;
                int hoff = 2;
                while (hoff < 7) {
                    int n = in.read(head, hoff, 7 - hoff);
                    if (n < 0) break;
                    hoff += n;
                }
                if (hoff < 7) { emit("stopped", "流已结束", null, 0, 0); return; }
                firstLen = frameLenFromAdts(head);
                addLog("检测到旧版服务端（纯 ADTS 流）");
            }
        } else {
            // OPUS：前缀模式
            firstLen = (hi << 8) | lo;
        }
        if (firstLen <= 7 || firstLen > first.length) {
            emit("error", "首帧无效 (len=" + firstLen + ")", null, 0, 0);
            return;
        }
        int off = 0;
        while (off < firstLen) {
            int n = in.read(first, off, firstLen - off);
            if (n < 0) break;
            off += n;
        }
        if (off < firstLen) { emit("stopped", "流已结束", null, 0, 0); return; }

        // csd：AAC 从 ADTS 提取；OPUS 首包即 OpusHead
        byte[] csd;
        if (isAac) {
            csd = buildCsdFromAdts(first);
        } else {
            if (firstLen >= 8 && first[0] == 'O' && first[1] == 'p') {
                csd = new byte[firstLen];
                System.arraycopy(first, 0, csd, 0, firstLen);
            } else {
                csd = null; // 无 OpusHead（旧服务端），硬解可能失败，软解不需要
            }
        }

        // 输出（AudioTrack / AAudio）
        AudioOut out = createAudioOut(rate, channels, buffer, mode);
        // 解码器（硬解优先）
        DecoderEngine decoder = createDecoder(isAac, rate, channels, csd, out);
        currentDecoder = decoder;
        currentOut = out;
        emit("playing", "缓冲中… " + out.name() + " / " + decoder.name(), isAac ? "AAC" : "OPUS", rate, channels);
        addLog("解码: " + decoder.name() + " 输出: " + out.name());

        // 首帧（AAC 硬解剥离 ADTS 头）
        feedFirst(decoder, isAac, first, firstLen);

        // 主循环
        while (running) {
            if (decoder.failed()) {
                if ("hard".equals(decodeMode)) {
                    addLog("硬解失败（强制硬解，不切换）: " + decoder.name());
                    break; // 强制硬解失败，停止
                }
                addLog("解码器失败，切换软解: " + decoder.name());
                decoder.release();
                decoder = new SoftDecoderEngine(isAac ? SoftDecoderEngine.TYPE_AAC : SoftDecoderEngine.TYPE_OPUS,
                        rate, channels, csd, out);
                decoder.init();
                currentDecoder = decoder;
            }
            int h2 = in.read();
            int l2 = in.read();
            if (h2 < 0 || l2 < 0) break;
            int frameLen = (h2 << 8) | l2;
            if (frameLen <= 0 || frameLen > first.length) continue;
            int foff = 0;
            while (foff < frameLen) {
                int n = in.read(first, foff, frameLen - foff);
                if (n < 0) break;
                foff += n;
            }
            if (foff < frameLen) break;
            if (isAac && decoder instanceof AsyncHardDecoder) {
                decoder.feed(first, 7, frameLen - 7); // 剥离 ADTS 头
            } else {
                decoder.feed(first, 0, frameLen);
            }
        }
        decoder.eos();
        decoder.release();
        out.close();
        currentDecoder = null;
        currentOut = null;
        emit("stopped", "流已结束", null, 0, 0);
    }

    private void feedFirst(DecoderEngine decoder, boolean isAac, byte[] frame, int len) {
        if (isAac && decoder instanceof AsyncHardDecoder) {
            decoder.feed(frame, 7, len - 7);
        } else {
            decoder.feed(frame, 0, len);
        }
    }

    /** 创建输出（AAudio 优先若启用，失败回退 AudioTrack） */
    private AudioOut createAudioOut(int rate, int channels, String buffer, int mode) {
        int bufferMs = computeBufferMs(buffer, mode);
        boolean wantAaudio = "1".equals(aaudioMode);
        AudioOut out;
        if (wantAaudio) {
            out = new AudioOut.AaudioOut();
            if (out.open(rate, channels, bufferMs)) return out;
            addLog("AAudio 打开失败，回退 AudioTrack");
            out = new AudioOut.AudioTrackOut();
        } else {
            out = new AudioOut.AudioTrackOut();
        }
        out.open(rate, channels, bufferMs);
        out.setVolume(volume);
        return out;
    }

    /** 缓冲 ms（档位/自定义） */
    private int computeBufferMs(String buffer, int mode) {
        if (buffer != null && buffer.startsWith("custom:")) {
            try {
                return Integer.parseInt(buffer.substring(7));
            } catch (NumberFormatException ignored) {
            }
        }
        if ("low".equals(buffer)) return 40;
        if ("high".equals(buffer)) return 160;
        return mode == 1 ? 40 : 80;
    }

    /** 创建解码器：按用户选择（auto 硬解优先+软解兜底 / hard 强制硬解 / soft 软解），可指定解码器 */
    private DecoderEngine createDecoder(boolean isAac, int rate, int channels, byte[] csd, AudioOut out) {
        String mime = isAac ? "audio/mp4a-latm" : "audio/opus";
        int softType = isAac ? SoftDecoderEngine.TYPE_AAC : SoftDecoderEngine.TYPE_OPUS;
        String name = decoderName == null || decoderName.isEmpty() ? null : decoderName;

        if ("soft".equals(decodeMode)) {
            DecoderEngine soft = new SoftDecoderEngine(softType, rate, channels, csd, out);
            soft.init();
            return soft;
        }

        // 校验指定解码器是否支持当前音频类型（防选错）
        if (name != null) {
            boolean supports = false;
            try {
                android.media.MediaCodecList mcl = new android.media.MediaCodecList(android.media.MediaCodecList.ALL_CODECS);
                for (android.media.MediaCodecInfo info : mcl.getCodecInfos()) {
                    if (name.equals(info.getName())) {
                        for (String t : info.getSupportedTypes()) {
                            if (mime.equals(t)) { supports = true; break; }
                        }
                        break;
                    }
                }
            } catch (Throwable ignored) {
            }
            if (!supports) {
                addLog("解码器 " + name + " 不支持 " + mime + "，回退自动选择");
                name = null;
            }
        }

        DecoderEngine hard = new AsyncHardDecoder(mime, rate, channels, csd, out, name);
        if (hard.init()) {
            if ("hard".equals(decodeMode)) addLog("强制硬解模式（用户指定）");
            return hard;
        }
        if ("hard".equals(decodeMode)) {
            addLog("硬解不可用（强制硬解模式）");
            return hard; // 返回失败的硬解，主循环会提示
        }
        addLog("硬解不可用，切换软解");
        hard.release();
        DecoderEngine soft = new SoftDecoderEngine(softType, rate, channels, csd, out);
        soft.init();
        return soft;
    }



    // ============ UDP 播放 ============

    private void playUdp(String ip, int tcpPort, int codecType, int rate, int channels,
                         String buffer, int mode) throws Exception {
        int udpPort = tcpPort + UDP_PORT_OFFSET;
        udpSocket = new DatagramSocket();
        udpSocket.setSoTimeout(10000);
        InetAddress addr = InetAddress.getByName(ip);
        InetSocketAddress server = new InetSocketAddress(addr, udpPort);
        byte[] magic = MAGIC;
        udpSocket.send(new DatagramPacket(magic, magic.length, server));

        // 心跳线程
        udpHeartbeat = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running && udpSocket != null && !udpSocket.isClosed()) {
                    try {
                        Thread.sleep(2000);
                        udpSocket.send(new DatagramPacket(magic, magic.length, server));
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        }, "udp-heartbeat");
        udpHeartbeat.start();

        // 等待格式头
        byte[] buf = new byte[8192];
        boolean gotHeader = false;
        long deadline = System.currentTimeMillis() + 8000;
        while (running && System.currentTimeMillis() < deadline) {
            DatagramPacket pkt = new DatagramPacket(buf, buf.length);
            udpSocket.receive(pkt);
            if (pkt.getLength() >= 16 && isMagic(buf, pkt.getOffset())) {
                gotHeader = true;
                break;
            }
        }
        if (!gotHeader) {
            emit("error", "UDP 等待格式头超时", null, 0, 0);
            return;
        }

        AudioSink sink = createSink(codecType, rate, channels, buffer, mode);
        sink.rate = rate;
        sink.channels = channels;
        if (sink.bufferMs > 0) {
            emit("playing", "缓冲 " + sink.bufferMs + "ms（App 内可调，减小可降延迟）", codecName(codecType), rate, channels);
        }

        // 抖动缓冲：TreeMap<seq, payload>，按"毫秒"管理水位（兼容任意包粒度；下限 20ms ≈ 1 帧）
        final TreeMap<Long, byte[]> jitter = new TreeMap<Long, byte[]>();
        final boolean isPcm = codecType == 0;
        final long frameBytes = (long) channels * 2; // PCM16 每帧字节
        final long[] queuedMs = {0};
        final int baseMs = udpBufferMs(buffer, mode);
        final int[] targetMs = {baseMs}; // 自适应：稳定后减小到 20ms
        final long[] lastSeq = {-1};
        final int[] stableCount = {0};

        // 播放线程
        Thread playThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean started = false;
                while (running) {
                    byte[] frame = null;
                    synchronized (jitter) {
                        if (queuedMs[0] >= targetMs[0] || started && !jitter.isEmpty()) {
                            Map.Entry<Long, byte[]> e = jitter.firstEntry();
                            frame = e.getValue();
                            jitter.remove(e.getKey());
                            queuedMs[0] -= pktMs(frame.length, isPcm, frameBytes, rate);
                            started = true;
                            // 稳定检测：连续 200 包无丢包则减小抖动缓冲（延迟最低）
                            long seq = e.getKey();
                            if (lastSeq[0] >= 0 && seq == lastSeq[0] + 1) {
                                stableCount[0]++;
                                if (stableCount[0] >= 40 && targetMs[0] > 20) {
                                    targetMs[0] = Math.max(20, targetMs[0] - 5); // 平滑收缩（每 40 包 -5ms）
                                    stableCount[0] = 0;
                                }
                            } else {
                                stableCount[0] = 0;
                                if (targetMs[0] < baseMs) {
                                    targetMs[0] = Math.min(baseMs, targetMs[0] + 20); // 丢包逐步恢复
                                }
                            }
                            lastSeq[0] = seq;
                        }
                    }
                    if (frame == null) {
                        try { Thread.sleep(2); } catch (InterruptedException e) { break; }
                        continue;
                    }
                    try {
                        if (codecType == 0) {
                            sink.writePcm(frame, frame.length);
                        } else {
                            sink.writeEncoded(frame, frame.length);
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
        }, "udp-play");
        playThread.start();

        // 接收线程
        while (running) {
            DatagramPacket pkt = new DatagramPacket(buf, buf.length);
            udpSocket.receive(pkt);
            if (pkt.getLength() >= 16 && isMagic(buf, pkt.getOffset())) {
                continue; // 格式头，忽略
            }
            if (pkt.getLength() <= 4) continue;
            int o = pkt.getOffset();
            long seq = ((buf[o] & 0xFFL) << 24) | ((buf[o + 1] & 0xFFL) << 16)
                     | ((buf[o + 2] & 0xFFL) << 8) | (buf[o + 3] & 0xFFL);
            int payloadLen = pkt.getLength() - 4;
            byte[] payload = new byte[payloadLen];
            System.arraycopy(buf, o + 4, payload, 0, payloadLen);
            synchronized (jitter) {
                if (!jitter.containsKey(seq)) {
                    jitter.put(seq, payload);
                    queuedMs[0] += pktMs(payloadLen, isPcm, frameBytes, rate);
                    statJitterMs = (int) queuedMs[0];
                    // 防膨胀：超过 3 倍目标缓冲时丢弃最旧的
                    while (queuedMs[0] > baseMs * 3L) {
                        Map.Entry<Long, byte[]> old = jitter.pollFirstEntry();
                        if (old == null) break;
                        queuedMs[0] -= pktMs(old.getValue().length, isPcm, frameBytes, rate);
                    }
                }
            }
        }
        playThread.interrupt();
        sink.release();
        if (currentSink == sink) currentSink = null;
        emit("stopped", "流已结束", null, 0, 0);
    }

    /** UDP 抖动缓冲目标（毫秒）：low/游戏 20ms（≈1 帧），music 80ms，high 160ms，custom 自定义 */
    private int udpBufferMs(String buffer, int mode) {
        if (buffer != null && buffer.startsWith("custom:")) {
            try {
                return Math.max(20, Integer.parseInt(buffer.substring(7)));
            } catch (NumberFormatException ignored) {
            }
        }
        if ("low".equals(buffer)) return 20;   // 1 帧@48k≈21.3ms
        if ("high".equals(buffer)) return 160;
        return mode == 1 ? 20 : 80;           // auto: game 20ms / music 80ms
    }

    /** 包时长估算：PCM 按字节数精确换算；编码流按 20ms/帧 */
    private static long pktMs(int len, boolean isPcm, long frameBytes, int rate) {
        return isPcm ? (long) len * 1000 / (frameBytes * rate) : 20;
    }

    private boolean isMagic(byte[] d, int o) {
        return d[o] == 'W' && d[o + 1] == 'F' && d[o + 2] == 'A' && d[o + 3] == 'U';
    }

    // ============ 音频播放（AudioTrack + 可选解码） ============

    private static final class AudioSink {
        AudioTrack track;
        MediaCodec dec;
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        int bufferMs = -1;
        long opusHandle = 0;  // libopus 软解 handle
        long aacHandle = 0;   // fdk-aac 软解 handle
        private String lastErr = "";
        private int lastErrCount = 0;
        // 统计
        volatile long writtenFrames = 0;   // 已写入 AudioTrack 的累计帧数
        volatile long receivedBytes = 0;   // 累计接收字节
        volatile long decodedFrames = 0;   // 解码输出帧数（编码模式）
        int rate = 48000;
        int channels = 2;

        /** 提交 OpusHead / AudioSpecificConfig 作为 CODEC_CONFIG */
        void setOpusHead(byte[] data, int len) {
            if (dec == null) return;
            addLog("提交 csd-0 给解码器 (" + len + "B)");
            try {
                int inIdx = dec.dequeueInputBuffer(20000);
                if (inIdx < 0) {
                    addLog("警告: dequeueInputBuffer 失败 " + inIdx);
                    return;
                }
                ByteBuffer ib = dec.getInputBuffer(inIdx);  // 每次获取最新 buffer（标准用法）
                ib.clear();
                ib.put(data, 0, len);
                dec.queueInputBuffer(inIdx, 0, len, 0, MediaCodec.BUFFER_FLAG_CODEC_CONFIG);
                addLog("csd-0 已提交");
            } catch (Throwable t) {
                addLog("setOpusHead 错误: " + t.getMessage());
                Log.e(TAG, "setOpusHead error", t);
            }
        }

        /** fdk-aac 软解初始化 */
        void startAacSoft(int rate, int channels, byte[] csd0) {
            aacHandle = AacDecoder.init(rate, channels, csd0);
            if (aacHandle != 0) {
                addLog("fdk-aac 软解就绪 (rate=" + rate + " ch=" + channels + ", handle=" + aacHandle + ")");
            } else {
                addLog("fdk-aac 初始化失败（详见 logcat AacSoft）");
                aacHandle = 0; // 防野指针
            }
        }

        /** fdk-aac 软解一帧 ADTS */
        void writeAac(byte[] data, int len) {
            if (aacHandle == 0) return;
            receivedBytes += len;
            try {
                short[] pcm = new short[1024 * 2];
                int samples = AacDecoder.decode(aacHandle, data, len, pcm);
                if (samples > 0) {
                    byte[] out = new byte[samples * channels * 2];
                    for (int i = 0; i < samples * channels; i++) {
                        short v = pcm[i];
                        out[i * 2] = (byte) (v & 0xFF);
                        out[i * 2 + 1] = (byte) (v >> 8);
                    }
                    if (track != null) track.write(out, 0, out.length);
                    writtenFrames += samples;
                    decodedFrames++;
                } else if (samples < 0) {
                    addLog("aac 解码错误码: " + samples);
                }
            } catch (Throwable t) {
                addLog("fdk-aac 解码错误: " + t.getMessage());
            }
        }

        /** libopus 软解初始化 */
        void startOpusSoft(int rate, int channels) {
            opusHandle = OpusDecoder.init(rate, channels);
            if (opusHandle != 0) {
                addLog("libopus 软解就绪 (rate=" + rate + " ch=" + channels + ")");
            } else {
                addLog("libopus 软解初始化失败!");
            }
        }

        /** libopus 软解一帧 */
        void writeOpus(byte[] data, int len) {
            if (opusHandle == 0) return;
            receivedBytes += len;
            try {
                short[] pcm = new short[960 * 2]; // 最多 960 采样/声道
                int samples = OpusDecoder.decode(opusHandle, data, len, pcm);
                if (samples > 0) {
                    byte[] out = new byte[samples * channels * 2];
                    for (int i = 0; i < samples * channels; i++) {
                        short v = pcm[i];
                        out[i * 2] = (byte) (v & 0xFF);
                        out[i * 2 + 1] = (byte) (v >> 8);
                    }
                    if (track != null) track.write(out, 0, out.length);
                    writtenFrames += samples;
                    decodedFrames++;
                } else if (samples < 0) {
                    addLog("opus 解码错误码: " + samples);
                }
            } catch (Throwable t) {
                addLog("libopus 解码错误: " + t.getMessage());
            }
        }

        void setVolume(int volume) {
            if (track != null) {
                try {
                    track.setVolume(volume / 100f);
                } catch (Throwable ignored) {
                }
            }
        }

        void writePcm(byte[] data, int len) {
            if (track != null) track.write(data, 0, len);
            writtenFrames += len / (channels * 2);
            receivedBytes += len;
        }

        void writeEncoded(byte[] data, int len) {
            if (dec == null) return;
            receivedBytes += len;
            try {
                int inIdx = dec.dequeueInputBuffer(20000);
                if (inIdx < 0) return;
                ByteBuffer ib = dec.getInputBuffer(inIdx);  // 每次获取最新 buffer（标准用法）
                ib.clear();
                ib.put(data, 0, len);
                dec.queueInputBuffer(inIdx, 0, len, System.nanoTime() / 1000, 0);
                while (true) {
                    int outIdx = dec.dequeueOutputBuffer(info, 0);
                    if (outIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        // ignore
                    } else if (outIdx >= 0) {
                        ByteBuffer ob = dec.getOutputBuffer(outIdx);  // 每次获取最新 buffer
                        ob.position(info.offset);
                        ob.limit(info.offset + info.size);
                        byte[] pcm = new byte[info.size];
                        ob.get(pcm);
                        if (track != null) track.write(pcm, 0, pcm.length);
                        writtenFrames += info.size / (channels * 2);
                        decodedFrames++;
                        dec.releaseOutputBuffer(outIdx, false);
                    } else {
                        break;
                    }
                }
            } catch (Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : t.toString();
                // 详细日志：异常 + 前 3 行堆栈
                String stack = "";
                StackTraceElement[] st = t.getStackTrace();
                for (int i = 0; i < Math.min(3, st.length); i++) stack += "  at " + st[i] + "\n";
                if (!msg.equals(lastErr)) {
                    lastErr = msg;
                    lastErrCount = 1;
                    addLog("解码错误: " + msg + "\n" + stack.trim());
                } else {
                    lastErrCount++;
                    if (lastErrCount == 10 || lastErrCount % 100 == 0) {
                        addLog("解码错误 (重复 " + lastErrCount + " 次): " + msg);
                    }
                }
                Log.e(TAG, "decode error", t);
            }
        }

        /** 当前 AudioTrack 缓冲中未播放的毫秒数（实时） */
        int getBufferMs() {
            if (track == null) return 0;
            long head = track.getPlaybackHeadPosition();
            long buffered = writtenFrames - head;
            if (buffered < 0) buffered = 0;
            return (int) (buffered * 1000 / rate);
        }

        void release() {
            if (track != null) {
                try { track.stop(); } catch (Throwable ignored) {}
                try { track.release(); } catch (Throwable ignored) {}
                track = null;
            }
            if (dec != null) {
                try { dec.stop(); dec.release(); } catch (Throwable ignored) {}
                dec = null;
            }
            if (opusHandle != 0) {
                try { OpusDecoder.destroy(opusHandle); } catch (Throwable ignored) {}
                opusHandle = 0;
            }
            if (aacHandle != 0) {
                try { AacDecoder.destroy(aacHandle); } catch (Throwable ignored) {}
                aacHandle = 0;
            }
        }
    }

    /**
     * PCM 自适应抖动缓冲。
     * 网络线程只负责入队（write），独立播放线程按 AudioTrack 消费节奏出队写音频。
     * 特性：
     *  - 预缓冲：连接后攒够目标水位再开始出声，避免开场卡顿
     *  - 自适应水位：按数据到达抖动(EWMA)动态调深浅 —— 网络稳则自动收缩延迟，抖则自动加深防卡
     *  - 过载丢帧：水位超限时丢最旧数据（快进追赶），防止缓冲无限膨胀导致延迟飙升
     *  - 漂移伺服：发送/接收端时钟漂移导致水位持续偏高时，小步丢帧拉回
     */
    final class PcmJitterBuffer {
        final AudioSink sink;
        final int rate, channels, frameBytes;   // frameBytes = 每帧字节数(PCM16)
        final int minMs, maxMs;                 // 自适应范围
        final boolean adaptive;                 // custom 档位固定目标，不自动调节
        final java.util.ArrayDeque<byte[]> queue = new java.util.ArrayDeque<byte[]>();
        long queuedFrames;                      // 队列中帧数
        volatile int targetMs;                  // 当前目标水位
        // 到达间隔抖动 EWMA
        long lastArrivalNanos;
        double jitterEwma;                      // |gap-avgGap| 的 EWMA
        double avgGapMs;                        // 平均到达间隔
        // 统计
        volatile long arrivedBytes;             // 数据到达字节数（用于采样率/码率计算）
        volatile int jitterMs;                  // 当前抖动估计
        volatile int currentMs;                 // 当前总缓冲（队列 + AudioTrack 内部）
        volatile long droppedFrames;            // 累计跳帧（帧）
        volatile long underruns;                // 累计欠载次数
        // 漂移伺服
        long lastAdaptNanos;
        int overTargetStreak;

        final Thread playThread = new Thread(new Runnable() {
            @Override
            public void run() { playLoop(); }
        }, "pcm-jitter");
        volatile boolean closed;

        PcmJitterBuffer(AudioSink sink, int rate, int channels, int baseMs, boolean adaptive) {
            this.sink = sink;
            this.rate = rate;
            this.channels = channels;
            this.frameBytes = channels * 2; // PCM16
            this.adaptive = adaptive;
            if (!adaptive) {
                this.minMs = this.maxMs = Math.max(20, baseMs);
            } else if (baseMs <= 40) {
                this.minMs = 25; this.maxMs = 100;    // 游戏：低延迟优先
            } else if (baseMs <= 80) {
                this.minMs = 40; this.maxMs = 180;    // 默认
            } else {
                this.minMs = 80; this.maxMs = 260;    // 高音质
            }
            this.targetMs = Math.max(20, baseMs);
        }

        void start() { playThread.start(); }

        void close() {
            closed = true;
            playThread.interrupt();
            try { playThread.join(500); } catch (InterruptedException ignored) {}
        }

        /** 网络线程：PCM 数据入队（不直接写 AudioTrack） */
        synchronized void write(byte[] data, int len) {
            if (closed || len <= 0) return;
            arrivedBytes += len;
            // 到达间隔抖动统计（稳定网络时 jitterEwma 小，抖动时大）
            long now = System.nanoTime();
            if (lastArrivalNanos != 0) {
                double gapMs = (now - lastArrivalNanos) / 1e6;
                if (gapMs > 0.1 && gapMs < 2000) {
                    avgGapMs = (avgGapMs == 0) ? gapMs : avgGapMs * 0.95 + gapMs * 0.05;
                    double dev = Math.abs(gapMs - avgGapMs);
                    jitterEwma = (jitterEwma == 0) ? dev : jitterEwma * 0.9 + dev * 0.1;
                }
            }
            lastArrivalNanos = now;
            byte[] copy = new byte[len];
            System.arraycopy(data, 0, copy, 0, len);
            queue.addLast(copy);
            queuedFrames += len / frameBytes;
            // 过载丢帧：队列水位 > 目标+40ms 时丢最旧（快进追赶）
            long maxQueued = (targetMs + 40) * (long) rate / 1000;
            while (queuedFrames > maxQueued) {
                byte[] old = queue.pollFirst();
                if (old == null) break;
                queuedFrames -= old.length / frameBytes;
                droppedFrames += old.length / frameBytes;
            }
        }

        /** 播放线程：取队首一块 */
        private synchronized byte[] pollChunk() {
            if (queue.isEmpty()) return null;
            byte[] head = queue.pollFirst();
            queuedFrames -= head.length / frameBytes;
            return head;
        }

        /** 播放线程：小步丢帧（漂移伺服，最多丢 10ms） */
        private synchronized void dropSamples(long frames) {
            long need = frames;
            while (need > 0 && !queue.isEmpty()) {
                byte[] head = queue.pollFirst();
                long hf = head.length / frameBytes;
                long take = Math.min(hf, need);
                if (take < hf) {
                    int keepBytes = (int) ((hf - take) * frameBytes);
                    byte[] rest = new byte[keepBytes];
                    System.arraycopy(head, (int) (take * frameBytes), rest, 0, keepBytes);
                    queue.addFirst(rest);
                    queuedFrames -= take;
                    droppedFrames += take;
                    need = 0;
                } else {
                    queuedFrames -= hf;
                    droppedFrames += hf;
                    need -= hf;
                }
            }
        }

        private void playLoop() {
            // 预缓冲：攒够目标水位再出声（最长等 2.5s，避免弱网长时间无声）
            long waitStart = System.nanoTime();
            while (!closed) {
                long q;
                synchronized (this) { q = queuedFrames; }
                if (q * 1000 / rate >= targetMs) break;
                if ((System.nanoTime() - waitStart) / 1e6 > 2500) break;
                try { Thread.sleep(10); } catch (InterruptedException e) { return; }
            }
            while (!closed) {
                byte[] chunk = pollChunk();
                if (chunk == null) {
                    underruns++;
                    try { Thread.sleep(10); } catch (InterruptedException e) { break; }
                    continue;
                }
                try {
                    sink.writePcm(chunk, chunk.length); // 阻塞式，AudioTrack 自然节流
                } catch (Throwable ignored) {
                }
                maybeAdapt();
            }
        }

        /** 周期性（≥500ms）：自适应水位 + 漂移伺服 + 统计刷新 */
        private void maybeAdapt() {
            long now = System.nanoTime();
            if (lastAdaptNanos != 0 && (now - lastAdaptNanos) / 1e6 < 500) return;
            lastAdaptNanos = now;
            jitterMs = (int) Math.round(jitterEwma);
            int queuedMs;
            synchronized (this) { queuedMs = (int) (queuedFrames * 1000 / rate); }
            if (adaptive) {
                // 抖动大 → 加深；稳定且无欠载 → 收缩
                if (jitterEwma > 15 || underruns > 0) {
                    if (jitterEwma > 15) targetMs = Math.min(targetMs + 10, maxMs);
                    if (underruns > 0 && targetMs < maxMs) targetMs = Math.min(targetMs + 10, maxMs);
                } else if (jitterEwma < 6 && targetMs > minMs) {
                    targetMs = Math.max(targetMs - 10, minMs);
                }
            }
            // 漂移伺服：队列水位持续 > 目标+40ms 超过 2 秒 → 小步丢 5ms
            if (queuedMs > targetMs + 40) {
                overTargetStreak++;
                if (overTargetStreak >= 4) {
                    dropSamples(rate / 200); // 5ms
                    overTargetStreak = 0;
                }
            } else {
                overTargetStreak = 0;
            }
            // 当前总缓冲（队列 + AudioTrack 内部未播放）
            currentMs = queuedMs + sink.getBufferMs();
        }
    }

    /** 从 ADTS 头提取 AudioSpecificConfig (csd-0) —— 标准格式，audioObjectType 直接存值 */
    private static byte[] buildCsdFromAdts(byte[] f) {
        int b2 = f[2] & 0xFF;
        int b3 = f[3] & 0xFF;
        int profile = (b2 >> 6) & 0x03;      // 0=Main 1=LC 2=SSR 3=LTP
        int sfIdx = (b2 >> 2) & 0x0F;
        int chanCfg = ((b2 & 0x03) << 2) | ((b3 >> 6) & 0x03);
        int objectType = profile + 1;        // AAC-LC = 2
        // 标准 AudioSpecificConfig（5 字节，AAC-LC 的 GASpecificConfig 为 0）
        byte[] csd = new byte[5];
        csd[0] = (byte) ((objectType << 3) | (sfIdx >> 1));
        csd[1] = (byte) (((sfIdx & 1) << 7) | (chanCfg << 3));
        csd[2] = 0;
        csd[3] = 0;
        csd[4] = 0;
        return csd;
    }

    /** 计算 AudioTrack 缓冲字节数（档位或自定义毫秒） */
    private static int computeBufferBytes(int rate, int channels, String buffer, int mode, boolean isPcm) {
        int chMask = channels >= 2 ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO;
        int minBuf = AudioTrack.getMinBufferSize(rate, chMask, AudioFormat.ENCODING_PCM_16BIT);
        if (isPcm) {
            // PCM 路径：抖动由 PcmJitterBuffer / UDP 队列负责，AudioTrack 内部缓冲压到最小（仅满足播放）
            return Math.max(minBuf, 4096);
        }
        int customMs = -1;
        if (buffer != null && buffer.startsWith("custom:")) {
            try {
                customMs = Integer.parseInt(buffer.substring(7));
            } catch (NumberFormatException ignored) {
            }
        }
        if (customMs > 0) {
            return Math.max(minBuf, (int) Math.round(rate * 2.0 * (channels >= 2 ? 2 : 1) * customMs / 1000.0));
        }
        int mult = "low".equals(buffer) ? 2 : ("high".equals(buffer) ? 6 : (mode == 1 ? 2 : 4));
        return Math.max(minBuf * mult, 8192);
    }

    /** 创建带 csd-0 的 AAC 解码播放器 */
    private AudioSink createSinkAac(int rate, int channels, String buffer, int mode, byte[] csd0) throws Exception {
        AudioSink sink = new AudioSink();
        currentSink = sink;
        addLog("创建 AAC 解码器 (MediaFormat csd-0=" + csd0.length + "B, rate=" + rate + ", ch=" + channels + ")");
        int chMask = channels >= 2 ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO;
        AudioTrack.Builder tb = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(rate)
                        .setChannelMask(chMask)
                        .build())
                .setBufferSizeInBytes(computeBufferBytes(rate, channels, buffer, mode, false))
                .setTransferMode(AudioTrack.MODE_STREAM);
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                tb.setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY);
            } catch (Throwable ignored) {
            }
        }
        sink.track = tb.build();
        sink.track.play();
        sink.bufferMs = (int) Math.round(sink.track.getBufferSizeInFrames() * 1000.0 / rate);

        MediaFormat fmt = MediaFormat.createAudioFormat("audio/mp4a-latm", rate, channels);
        fmt.setByteBuffer("csd-0", ByteBuffer.wrap(csd0));
        // 硬解可能需要显式参数
        try {
            fmt.setInteger(MediaFormat.KEY_CHANNEL_MASK,
                    channels >= 2 ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO);
        } catch (Throwable ignored) {
        }
        try {
            fmt.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        } catch (Throwable ignored) {
        }
        try {
            sink.dec = MediaCodec.createDecoderByType("audio/mp4a-latm");
            addLog("AAC createDecoderByType OK");
        } catch (Throwable t) {
            addLog("AAC createDecoderByType 失败: " + t);
            throw t;
        }
        try {
            sink.dec.configure(fmt, null, null, 0);
            addLog("AAC configure OK");
            sink.dec.start();
            addLog("AAC start OK");
        } catch (Throwable t) {
            addLog("AAC configure/start 失败: " + t);
            throw t;
        }
        return sink;
    }

    /** 创建带 csd-0（OpusHead）的 OPUS 解码播放器 */
    private AudioSink createSinkOpus(int rate, int channels, String buffer, int mode, byte[] opusHead) throws Exception {
        AudioSink sink = new AudioSink();
        currentSink = sink;
        int chMask = channels >= 2 ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO;
        AudioTrack.Builder tb = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(rate)
                        .setChannelMask(chMask)
                        .build())
                .setBufferSizeInBytes(computeBufferBytes(rate, channels, buffer, mode, false))
                .setTransferMode(AudioTrack.MODE_STREAM);
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                tb.setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY);
            } catch (Throwable ignored) {
            }
        }
        sink.track = tb.build();
        sink.track.play();
        sink.bufferMs = (int) Math.round(sink.track.getBufferSizeInFrames() * 1000.0 / rate);

        addLog("创建 OPUS 解码器 (MediaFormat csd-0=" + opusHead.length + "B)");
        MediaFormat fmt = MediaFormat.createAudioFormat("audio/opus", rate, channels);
        fmt.setByteBuffer("csd-0", ByteBuffer.wrap(opusHead));
        sink.dec = MediaCodec.createDecoderByType("audio/opus");
        sink.dec.configure(fmt, null, null, 0);
        sink.dec.start();
        addLog("OPUS 解码器已启动");
        return sink;
    }

    private AudioSink createSink(int codecType, int rate, int channels, String buffer, int mode) throws Exception {
        AudioSink sink = new AudioSink();
        currentSink = sink;
        int chMask = channels >= 2 ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO;
        int minBuf = AudioTrack.getMinBufferSize(rate, chMask, AudioFormat.ENCODING_PCM_16BIT);

        AudioTrack.Builder tb = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(rate)
                        .setChannelMask(chMask)
                        .build())
                .setTransferMode(AudioTrack.MODE_STREAM)
                .setBufferSizeInBytes(computeBufferBytes(rate, channels, buffer, mode, codecType == 0));
        // 低延迟模式（Android 8+ fast mixer 路径）
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                tb.setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY);
            } catch (Throwable ignored) {
            }
        }
        sink.track = tb.build();
        sink.track.play();

        // 记录实际缓冲（毫秒），供状态显示
        sink.bufferMs = (int) Math.round(sink.track.getBufferSizeInFrames() * 1000.0 / rate);

        if (codecType != 0) {
            String mime = codecType == 2 ? "audio/opus" : "audio/mp4a-latm";
            addLog("创建 " + mime + " 解码器");
            MediaFormat fmt = MediaFormat.createAudioFormat(mime, rate, channels);
            sink.dec = MediaCodec.createDecoderByType(mime);
            sink.dec.configure(fmt, null, null, 0);
            sink.dec.start();
            addLog(mime + " 解码器已启动");
        }
        return sink;
    }

    private void UtilLog(String msg) {
        android.util.Log.d(TAG, msg);
    }

    // ============ 工具 ============

    private static String codecName(int c) {
        return c == 1 ? "AAC" : (c == 2 ? "OPUS" : "PCM");
    }

    private static boolean readFully(InputStream in, byte[] buf) throws IOException {
        int off = 0;
        while (off < buf.length) {
            int n = in.read(buf, off, buf.length - off);
            if (n < 0) return false;
            off += n;
        }
        return true;
    }

    private static long leInt(byte[] b, int off) {
        return (b[off] & 0xFFL) | ((b[off + 1] & 0xFFL) << 8)
                | ((b[off + 2] & 0xFFL) << 16) | ((b[off + 3] & 0xFFL) << 24);
    }

    /** 从流中读取一个完整 ADTS 帧 */
    private boolean readAdtsFrame(InputStream in, byte[] frame) throws IOException {
        int b = in.read();
        if (b < 0) return false;
        while (b != 0xFF) {
            b = in.read();
            if (b < 0) return false;
        }
        int b2 = in.read();
        if (b2 < 0) return false;
        while ((b2 & 0xF6) != 0xF0) {
            b = b2;
            b2 = in.read();
            if (b2 < 0) return false;
            if (b != 0xFF) {
                b2 = in.read();
                if (b2 < 0) return false;
            }
        }
        int b3 = in.read(), b4 = in.read();
        if (b3 < 0 || b4 < 0) return false;
        int b5 = in.read(), b6 = in.read(), b7 = in.read();
        if (b5 < 0 || b6 < 0 || b7 < 0) return false;
        int frameLen = ((b3 & 0x03) << 11) | (b4 << 3) | ((b5 >> 5) & 0x07);
        if (frameLen < 7 || frameLen > frame.length) {
            return readAdtsFrame(in, frame);
        }
        frame[0] = (byte) 0xFF; frame[1] = (byte) b2; frame[2] = (byte) b3;
        frame[3] = (byte) b4; frame[4] = (byte) b5; frame[5] = (byte) b6; frame[6] = (byte) b7;
        int need = frameLen - 7, off = 7;
        while (need > 0) {
            int n = in.read(frame, off, need);
            if (n < 0) return false;
            off += n;
            need -= n;
        }
        return true;
    }

    private static int frameLenFromAdts(byte[] f) {
        return ((f[3] & 0x03) << 11) | ((f[4] & 0xFF) << 3) | ((f[5] >> 5) & 0x07);
    }

    private void emit(String state, String msg, String codec, int rate, int channels) {
        if (stateListener != null) {
            try {
                stateListener.onState(state, msg, codec, rate, channels);
            } catch (Throwable ignored) {
            }
        }
    }

    // ============ 实时统计 ============

    private void startStatsThread() {
        if (statsThread != null) return;
        statsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long lastBytes = 0;
                long lastTime = System.currentTimeMillis();
                while (running && !Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                    AudioSink s = currentSink;
                    DecoderEngine dec = currentDecoder;
                    AudioOut out = currentOut;
                    PcmJitterBuffer jb = currentJb;
                    if (s == null && dec == null) continue;
                    long now = System.currentTimeMillis();
                    // PCM 模式用"数据到达字节数"（反映发射端真实发送速率），编码模式用已写入字节
                    long bytes = jb != null ? jb.arrivedBytes : (s != null ? s.receivedBytes : 0);
                    int kbps = (int) ((bytes - lastBytes) * 8 / Math.max(1, now - lastTime));
                    lastBytes = bytes;
                    lastTime = now;
                    int audioMs = out != null ? out.getBufferMs()
                            : (jb != null ? jb.currentMs : (s != null ? s.getBufferMs() : 0));
                    if (jb != null) statJitterMs = jb.jitterMs; // PCM TCP：显示真实抖动
                    // PCM 模式：由数据到达速率反推采样率（每帧 channels*2 字节，PCM16）
                    if (statCodecName != null && statCodecName.equals("PCM") && statChannels > 0) {
                        int calcRate = (int) Math.round(kbps * 1000.0 / 8 / (statChannels * 2));
                        if (calcRate >= 8000 && calcRate <= 384000) {
                            statRate = calcRate;
                        }
                    }
                    // 端到端估算：发送端捕获≈40ms + 网络≈5ms + 接收端缓冲 + UDP抖动
                    int latency = 40 + audioMs + statJitterMs + 5;
                    int targetMs = jb != null ? jb.targetMs : 0;
                    long dropped = jb != null ? jb.droppedFrames : 0;
                    long underruns = jb != null ? jb.underruns : 0;
                    String outName = out != null ? out.name() : (s != null ? "AudioTrack" : "");
                    emitStats(kbps, audioMs, latency, targetMs, dropped, underruns, outName);
                }
            }
        }, "stats");
        statsThread.start();
    }

    /** 播放 1kHz 测试音 1 秒（验证 AudioTrack 输出链路） */
    private void playTestTone() {
        addLog("测试音: 1kHz 正弦波 1 秒");
        final int rate = 48000;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                AudioTrack tt = null;
                try {
                    int minBuf = AudioTrack.getMinBufferSize(rate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
                    tt = new AudioTrack.Builder()
                            .setAudioAttributes(new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build())
                            .setAudioFormat(new AudioFormat.Builder()
                                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                    .setSampleRate(rate)
                                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                    .build())
                            .setBufferSizeInBytes(minBuf * 2)
                            .setTransferMode(AudioTrack.MODE_STREAM)
                            .build();
                    tt.play();
                    short[] buf = new short[minBuf / 2];
                    for (int i = 0; i < buf.length; i++) {
                        buf[i] = (short) (Math.sin(2 * Math.PI * 1000 * i / rate) * 8000);
                    }
                    long start = System.currentTimeMillis();
                    while (System.currentTimeMillis() - start < 1000) {
                        tt.write(buf, 0, buf.length);
                    }
                    addLog("测试音播放完成（若听到 1kHz 声音说明扬声器正常）");
                } catch (Throwable e) {
                    addLog("测试音失败: " + e.getMessage());
                } finally {
                    if (tt != null) {
                        try {
                            tt.stop();
                            tt.release();
                        } catch (Throwable ignored) {
                        }
                    }
                }
            }
        }, "test-tone");
        t.start();
    }

    private void emitStats(int kbps, int audioMs, int latencyMs,
                           int targetMs, long dropped, long underruns, String output) {
        try {
            Intent i = new Intent(ACTION_STATS);
            i.putExtra(EXTRA_CUR_TRANSPORT, statTransport);
            i.putExtra(EXTRA_CODEC, statCodecName);
            i.putExtra(EXTRA_RATE, statRate);
            i.putExtra(EXTRA_CHANNELS, statChannels);
            i.putExtra(EXTRA_BITRATE, kbps);
            i.putExtra(EXTRA_AUDIO_MS, audioMs);
            i.putExtra(EXTRA_JITTER_MS, statJitterMs);
            i.putExtra(EXTRA_LATENCY_MS, latencyMs);
            i.putExtra(EXTRA_RX_KB, (int) ((currentSink != null ? currentSink.receivedBytes : 0) / 1024));
            i.putExtra(EXTRA_DEC_FRAMES, currentDecoder != null ? currentDecoder.outputFrames()
                    : (currentSink != null ? currentSink.decodedFrames : 0));
            i.putExtra(EXTRA_TARGET_MS, targetMs);
            i.putExtra(EXTRA_DROPPED, dropped);
            i.putExtra(EXTRA_UNDERRUN, underruns);
            i.putExtra(EXTRA_OUTPUT, output == null ? "" : output);
            // 附带最近日志（30 行）
            synchronized (logBuf) {
                int from = Math.max(0, logBuf.size() - 30);
                i.putStringArrayListExtra(EXTRA_LOG, new java.util.ArrayList<String>(logBuf.subList(from, logBuf.size())));
            }
            sendBroadcast(i);
        } catch (Throwable ignored) {
        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "音频播放",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }

    private Notification buildNotification(String text) {
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("WiFiAudio 接收端")
                .setContentText(text)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .setContentIntent(pi)
                .setOngoing(true);
        return b.build();
    }

    private void updateNotification(String text) {
        try {
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.notify(1, buildNotification(text));
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void onDestroy() {
        addLog("======== 接收端服务停止 ========");
        stopPlayback();
        super.onDestroy();
    }
}
