package com.wifiaudio.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.IBinder;
import android.util.Log;
import androidx.compose.animation.core.AnimationKt;
import androidx.compose.material3.ProgressIndicatorKt;
import androidx.compose.runtime.ComposerKt;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import com.wifiaudio.receiver.AudioOut;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import kotlin.UByte;

/* loaded from: classes2.dex */
public class PlayerService extends Service {
    public static final String ACTION_CONNECT = "com.wifiaudio.receiver.CONNECT";
    public static final String ACTION_STATE = "com.wifiaudio.receiver.STATE";
    public static final String ACTION_STATS = "com.wifiaudio.receiver.STATS";
    public static final String ACTION_STOP = "com.wifiaudio.receiver.STOP";
    public static final String ACTION_TEST_TONE = "com.wifiaudio.receiver.TEST_TONE";
    public static final String ACTION_VOLUME = "com.wifiaudio.receiver.SET_VOLUME";
    private static final String CHANNEL_ID = "wifiaudio_playback";
    static final int DEVICE_BUFFERED_MAX_MS = 2000;
    static final int DEVICE_LOW_MS = 12;
    public static final String EXTRA_AAUDIO = "aaudio";
    public static final String EXTRA_AUDIO_MS = "audio_buffer_ms";
    public static final String EXTRA_AUTORECONNECT = "autoreconnect";
    public static final String EXTRA_BITRATE = "bitrate_kbps";
    public static final String EXTRA_BUFFER = "buffer";
    public static final String EXTRA_CHANNELS = "channels";
    public static final String EXTRA_CODEC = "codec";
    public static final String EXTRA_CUR_TRANSPORT = "cur_transport";
    public static final String EXTRA_DEC_FRAMES = "dec_frames";
    public static final String EXTRA_DROPPED = "dropped";
    public static final String EXTRA_IP = "ip";
    public static final String EXTRA_JITTER_MS = "jitter_ms";
    public static final String EXTRA_LATENCY_MS = "latency_ms";
    public static final String EXTRA_LOG = "log_lines";
    public static final String EXTRA_MSG = "msg";
    public static final String EXTRA_OUTPUT = "output";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_RATE = "rate";
    public static final String EXTRA_RX_KB = "rx_kb";
    public static final String EXTRA_SENDER_MS = "sender_raw_ms";
    public static final String EXTRA_STATE = "state";
    public static final String EXTRA_TARGET_MS = "target_ms";
    public static final String EXTRA_TRANSPORT = "transport";
    public static final String EXTRA_UNDERRUN = "underrun";
    public static final String EXTRA_VOLUME = "volume";
    static final int SENDER_LATENCY_FALLBACK_MS = 40;
    private static final String TAG = "WiFiAudioReceiver";
    private static final int UDP_PORT_OFFSET = 1000;
    static final int WRITE_AHEAD_GIVEUP_MS = 400;
    static final int WRITE_AHEAD_MAX_MS = 60;
    static final int WRITE_AHEAD_MS = 30;
    static final int WRITE_AHEAD_STEP_MS = 10;
    static final int WRITE_SLICE_MS = 10;
    private static File logFile;
    private static volatile ArrayBlockingQueue<String> logQueue;
    private static volatile boolean logWriterStarted;
    private static volatile StateListener stateListener;
    private static volatile StatsListener statsListener;
    private AudioTrack audioTrack;
    private volatile PcmJitterBuffer currentJb;
    private volatile AudioOut currentOut;
    private volatile AudioSink currentSink;
    private Thread statsThread;
    private Thread udpHeartbeat;
    private DatagramSocket udpSocket;
    private volatile Thread worker;
    private static final byte[] MAGIC = {87, 70, 65, 85};
    private static volatile String aaudioNote = "";
    private static volatile long statMaxArrivalGapMs = 0;
    private static volatile long lastArrivalAtMs = 0;
    private static final List<String> logBuf = Collections.synchronizedList(new ArrayList());
    private static final ThreadLocal<SimpleDateFormat> LOG_SDF = new ThreadLocal<SimpleDateFormat>() { // from class: com.wifiaudio.receiver.PlayerService.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss", Locale.US);
        }
    };
    private volatile boolean running = false;
    private volatile String aaudioMode = "0";
    private volatile int volume = 100;
    private volatile boolean autoReconnect = false;
    private volatile boolean userStopped = false;
    private volatile String statTransport = "-";
    private volatile String statCodecName = "-";
    private volatile int statRate = 0;
    private volatile int statChannels = 0;
    private volatile int statJitterMs = 0;
    private volatile int statSenderRawMs = -1;
    private volatile int senderLatencyMs = 0;
    private volatile int statUdpTargetMs = 0;
    private volatile long statUdpDroppedFrames = 0;
    private volatile long statUdpStarveCount = 0;
    private volatile long statUdpUnderruns = 0;
    private volatile long statUdpArrivedBytes = 0;
    private volatile int statHdrRate = 0;
    private volatile long lastBitrateWarnAt = 0;
    private volatile boolean arrivalAdviceLogged = false;

    public interface StateListener {
        void onState(String str, String str2, String str3, int i, int i2);
    }

    public interface StatsListener {
        void onStats(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, long j, long j2, String str3, ArrayList<String> arrayList, int i10);
    }

    static long pcmBufferedFrames(long j, long j2, long j3) {
        long j4 = ((j & 4294967295L) - (j2 & 4294967295L)) & 4294967295L;
        if (j4 > j3) {
            return -1L;
        }
        return j4;
    }

    static int saneBufferedMs(int i) {
        if (i < 0 || i > DEVICE_BUFFERED_MAX_MS) {
            return 0;
        }
        return i;
    }

    static int senderLatencyForDisplay(int i) {
        if (i > 0) {
            return i;
        }
        return -1;
    }

    static boolean underrunStartsEpisode(boolean z, boolean z2) {
        return z2 && !z;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void setStatsListener(StatsListener statsListener2) {
        statsListener = statsListener2;
    }

    public static void initLogFile(Context context) {
        try {
            File externalFilesDir = context.getExternalFilesDir(null);
            if (externalFilesDir != null) {
                File file = new File(externalFilesDir, "wifiaudio.log");
                logFile = file;
                if (file.exists() && logFile.length() > 1048576) {
                    logFile.delete();
                }
            }
        } catch (Throwable unused) {
        }
        startLogWriter();
    }

    private static void startLogWriter() {
        if (logWriterStarted || logFile == null) {
            return;
        }
        synchronized (PlayerService.class) {
            if (!logWriterStarted && logFile != null) {
                final ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<>(512);
                logQueue = arrayBlockingQueue;
                Thread thread = new Thread(new Runnable() { // from class: com.wifiaudio.receiver.PlayerService.2
                    @Override // java.lang.Runnable
                    public void run() throws InterruptedException {
                        String str;
                        StringBuilder sb = new StringBuilder(4096);
                        while (true) {
                            try {
                                try {
                                    sb.setLength(0);
                                    sb.append((String) arrayBlockingQueue.take()).append('\n');
                                    for (int i = 0; i < 200 && (str = (String) arrayBlockingQueue.poll()) != null; i++) {
                                        sb.append(str).append('\n');
                                    }
                                    FileOutputStream fileOutputStream = new FileOutputStream(PlayerService.logFile, true);
                                    fileOutputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                                    fileOutputStream.close();
                                } catch (Throwable unused) {
                                    Thread.sleep(200L);
                                }
                            } catch (InterruptedException unused2) {
                                return;
                            }
                        }
                    }
                }, "log-writer");
                thread.setDaemon(true);
                thread.start();
                logWriterStarted = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void addLog(String str) {
        try {
            String str2 = "[" + LOG_SDF.get().format(new Date()) + "] " + str;
            logBuf.add(str2);
            while (true) {
                List<String> list = logBuf;
                if (list.size() > 300) {
                    list.remove(0);
                } else {
                    try {
                        break;
                    } catch (Throwable unused) {
                    }
                }
            }
            Log.d(TAG, str);
            ArrayBlockingQueue<String> arrayBlockingQueue = logQueue;
            if (arrayBlockingQueue != null) {
                arrayBlockingQueue.offer(str2);
            }
        } catch (Throwable unused2) {
        }
    }

    static int logBufferSize() {
        return logBuf.size();
    }

    public static void logStatic(String str) {
        addLog(str);
    }

    public static void setStateListener(StateListener stateListener2) {
        stateListener = stateListener2;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        createChannel();
        initLogFile(this);
        addLog("======== WiFiAudio 接收端启动 ========");
        startForeground(1, buildNotification("正在准备…"));
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) throws Throwable {
        if (intent == null) {
            return 2;
        }
        String action = intent.getAction();
        if (ACTION_STOP.equals(action)) {
            stopPlayback();
            stopSelf();
            return 2;
        }
        if (ACTION_TEST_TONE.equals(action)) {
            playTestTone();
            return 2;
        }
        if (ACTION_VOLUME.equals(action)) {
            this.volume = intent.getIntExtra(EXTRA_VOLUME, 100);
            if (this.currentOut != null) {
                try {
                    this.currentOut.setVolume(this.volume);
                } catch (Throwable unused) {
                }
            }
            if (this.currentSink != null) {
                try {
                    this.currentSink.setVolume(this.volume);
                } catch (Throwable unused2) {
                }
            }
            return 2;
        }
        if (ACTION_CONNECT.equals(action)) {
            String stringExtra = intent.getStringExtra(EXTRA_IP);
            int intExtra = intent.getIntExtra(EXTRA_PORT, 47800);
            String stringExtra2 = intent.getStringExtra("transport");
            String stringExtra3 = intent.getStringExtra(EXTRA_BUFFER);
            this.aaudioMode = intent.getStringExtra(EXTRA_AAUDIO);
            this.volume = intent.getIntExtra(EXTRA_VOLUME, 100);
            this.autoReconnect = "1".equals(intent.getStringExtra(EXTRA_AUTORECONNECT));
            startPlayback(stringExtra, intExtra, stringExtra2, stringExtra3);
        }
        return 2;
    }

    private synchronized void startPlayback(final String str, final int i, final String str2, final String str3) throws Throwable {
        try {
            try {
                stopPlayback();
                this.userStopped = false;
                this.running = true;
                this.worker = new Thread(new Runnable() { // from class: com.wifiaudio.receiver.PlayerService.3
                    @Override // java.lang.Runnable
                    public void run() throws InterruptedException {
                        PlayerService.this.connectAndPlay(str, i, str2, str3);
                    }
                }, "wifiaudio-player");
                this.worker.start();
            } catch (Throwable th) {
                th = th;
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private synchronized void stopPlayback() throws Throwable {
        try {
            try {
                this.userStopped = true;
                this.running = false;
                if (this.worker != null) {
                    this.worker.interrupt();
                    this.worker = null;
                }
                Thread thread = this.statsThread;
                if (thread != null) {
                    thread.interrupt();
                    this.statsThread = null;
                }
                DatagramSocket datagramSocket = this.udpSocket;
                if (datagramSocket != null) {
                    try {
                        datagramSocket.close();
                    } catch (Throwable unused) {
                    }
                    this.udpSocket = null;
                }
                Thread thread2 = this.udpHeartbeat;
                if (thread2 != null) {
                    thread2.interrupt();
                    this.udpHeartbeat = null;
                }
                if (this.currentJb != null) {
                    this.currentJb.close();
                    this.currentJb = null;
                }
                if (this.currentSink != null) {
                    this.currentSink.release();
                    this.currentSink = null;
                }
                if (this.currentOut != null) {
                    try {
                        this.currentOut.close();
                    } catch (Throwable unused2) {
                    }
                    this.currentOut = null;
                }
                updateNotification("已停止");
                emit("stopped", "已停止", null, 0, 0);
            } catch (Throwable th) {
                th = th;
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:126:0x02fe A[Catch: all -> 0x032d, TRY_LEAVE, TryCatch #7 {all -> 0x032d, blocks: (B:5:0x0069, B:124:0x02fa, B:126:0x02fe), top: B:140:0x0069 }] */
    /* JADX WARN: Removed duplicated region for block: B:153:0x0327 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:162:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private boolean connectOnce(String str, int i, String str2, String str3) throws Throwable {
        PlayerService playerService;
        String str4;
        Socket socket;
        InputStream inputStream;
        byte[] bArr;
        Socket socket2 = null;
        try {
            try {
                addLog("正在连接 " + str + ":" + i);
                str4 = "错误: ";
                playerService = this;
                try {
                    try {
                        playerService.emit("connecting", "正在连接 " + str + ":" + i + " …", null, 0, 0);
                        playerService.updateNotification("正在连接 " + str + "…");
                        socket = new Socket();
                        try {
                            socket.connect(new InetSocketAddress(str, i), ProgressIndicatorKt.CircularAnimationProgressDuration);
                            socket.setTcpNoDelay(true);
                            socket.setSoTimeout(10000);
                            inputStream = socket.getInputStream();
                            playerService.senderLatencyMs = 0;
                            aaudioNote = "";
                            playerService.statUdpTargetMs = 0;
                            playerService.statUdpDroppedFrames = 0L;
                            playerService.statUdpStarveCount = 0L;
                            playerService.statUdpUnderruns = 0L;
                            playerService.statUdpArrivedBytes = 0L;
                            playerService.lastBitrateWarnAt = 0L;
                            statMaxArrivalGapMs = 0L;
                            lastArrivalAtMs = 0L;
                            playerService.arrivalAdviceLogged = false;
                            bArr = new byte[16];
                        } catch (Exception e) {
                            e = e;
                        } catch (Throwable th) {
                            th = th;
                        }
                    } catch (Exception e2) {
                        e = e2;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception e3) {
                e = e3;
                playerService = this;
                str4 = "错误: ";
            }
        } catch (Throwable th3) {
            th = th3;
        }
        try {
            if (!readFully(inputStream, bArr)) {
                playerService.emit("error", "连接已断开（未收到数据）", null, 0, 0);
                try {
                    socket.close();
                } catch (IOException unused) {
                }
                return false;
            }
            if (bArr[0] == 87 && bArr[1] == 70 && bArr[2] == 65 && bArr[3] == 85) {
                int i2 = bArr[5] & UByte.MAX_VALUE;
                if (i2 != 0) {
                    emit("error", "对端发送的是压缩编码（" + (i2 == 1 ? "AAC" : i2 == 2 ? "OPUS" : String.valueOf(i2)) + "），本版本只支持未压缩 PCM —— 请把发射端模块也升级到 4.19", null, 0, 0);
                    addLog("协议不匹配: 对端 codecType=" + i2 + "（本版本仅支持 PCM）");
                    try {
                        socket.close();
                    } catch (IOException unused2) {
                    }
                    return false;
                }
                int iLeInt = (int) leInt(bArr, 6);
                int i3 = bArr[10] & UByte.MAX_VALUE;
                if (i3 <= 0) {
                    i3 = 2;
                }
                int i4 = bArr[12] & UByte.MAX_VALUE;
                int i5 = bArr[13] & UByte.MAX_VALUE;
                this.senderLatencyMs = senderLatencyFromHeader(bArr, 0);
                String strCodecName = codecName();
                addLog("协议头: codec=" + strCodecName + " rate=" + iLeInt + " ch=" + i3 + " mode=" + (i4 == 1 ? "game" : "music") + " serverTransport=" + (i5 == 1 ? "udp" : "tcp") + (this.senderLatencyMs > 0 ? " 发送端实测延迟=" + this.senderLatencyMs + "ms" : " 发送端延迟=未提供(按40ms估算)"));
                boolean z = !"tcp".equals(str2) && ("udp".equals(str2) || i5 == 1);
                emit("playing", "已连接 " + iLeInt + "Hz/" + i3 + "ch " + strCodecName + (z ? " (UDP)" : " (TCP)"), strCodecName, iLeInt, i3);
                updateNotification("正在播放 " + iLeInt + "Hz " + strCodecName + (z ? " UDP" : ""));
                this.statTransport = z ? "UDP" : "TCP";
                this.statCodecName = strCodecName;
                this.statRate = iLeInt;
                this.statHdrRate = iLeInt;
                int iDeviceNativeRate = deviceNativeRate();
                addLog("本机输出: 原生采样率 " + (iDeviceNativeRate > 0 ? (iDeviceNativeRate / 1000) + "kHz" : "未知") + "；本次「输出」= " + ("1".equals(this.aaudioMode) ? "AAudio（已请求）" : "AudioTrack"));
                this.statChannels = i3;
                this.statJitterMs = 0;
                startStatsThread();
                if (z) {
                    socket.close();
                    try {
                        playUdp(str, i, i2, iLeInt, i3, str3, i4);
                    } catch (Exception e4) {
                        e = e4;
                        playerService = this;
                        if (playerService.running) {
                            Log.e(TAG, "playback error", e);
                            playerService.emit("error", str4 + e.getMessage(), null, 0, 0);
                            playerService.updateNotification("连接失败");
                        }
                        if (socket2 != null) {
                            return true;
                        }
                        try {
                            socket2.close();
                            return true;
                        } catch (IOException unused3) {
                            return true;
                        }
                    }
                } else {
                    playTcpStream(inputStream, iLeInt, i3, str3, i4);
                    socket2 = socket;
                }
                if (socket2 != null) {
                    try {
                        socket2.close();
                    } catch (IOException unused4) {
                    }
                }
                return true;
            }
            emit("error", "协议不匹配：目标不是 WiFiAudio 发送端", null, 0, 0);
            try {
                socket.close();
                return false;
            } catch (IOException unused5) {
                return false;
            }
        } catch (Exception e5) {
            e = e5;
            playerService = this;
            socket2 = socket;
            if (playerService.running) {
            }
            if (socket2 != null) {
            }
        } catch (Throwable th4) {
            th = th4;
            socket2 = socket;
            if (socket2 != null) {
                try {
                    socket2.close();
                } catch (IOException unused6) {
                }
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectAndPlay(String str, int i, String str2, String str3) throws InterruptedException {
        while (this.running && !this.userStopped && connectOnce(str, i, str2, str3) && this.running && this.autoReconnect && !this.userStopped) {
            addLog("连接断开，3 秒后自动重连...");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException unused) {
                return;
            }
        }
    }

    private void playTcpStream(InputStream inputStream, int i, int i2, String str, int i3) throws Exception {
        playPcmStream(inputStream, i, i2, str, i3);
    }

    private void playPcmStream(InputStream inputStream, int i, int i2, String str, int i3) throws Exception {
        AudioOut.AaudioOut aaudioOut;
        int i4;
        AudioSink audioSinkCreateSink = createSink(i, i2, str, i3);
        this.currentSink = audioSinkCreateSink;
        audioSinkCreateSink.rate = i;
        audioSinkCreateSink.channels = i2;
        audioSinkCreateSink.setVolume(this.volume);
        emit("playing", "PCM 直通 " + i + "Hz/" + i2 + "ch", "PCM", i, i2);
        int iComputeBufferMs = computeBufferMs(str, i3);
        boolean z = str == null || !str.startsWith("custom:");
        if ("1".equals(this.aaudioMode)) {
            aaudioOut = new AudioOut.AaudioOut();
            if (aaudioOut.open(i, i2, iComputeBufferMs)) {
                aaudioOut.setVolume(this.volume);
                this.currentOut = aaudioOut;
                addLog("PCM 输出: AAudio（exclusive 低延迟）");
            } else {
                addLog("PCM AAudio 打开失败，回退 AudioTrack");
                aaudioNote = aaudioFallbackNote(true, i, deviceNativeRate());
                aaudioOut = null;
            }
        } else {
            aaudioOut = null;
        }
        PcmJitterBuffer pcmJitterBuffer = new PcmJitterBuffer(audioSinkCreateSink, aaudioOut, i, i2, iComputeBufferMs, z);
        this.currentJb = pcmJitterBuffer;
        addLog("PCM 自适应缓冲启动: 目标 " + iComputeBufferMs + "ms" + (z ? "（自动调节 " + pcmJitterBuffer.minMs + "-" + pcmJitterBuffer.maxMs + "ms）" : "（自定义固定）"));
        pcmJitterBuffer.start();
        byte[] bArr = new byte[i2 * 4096];
        while (this.running && (i4 = inputStream.read(bArr)) >= 0) {
            if (i4 > 0) {
                pcmJitterBuffer.write(bArr, i4);
            }
        }
        pcmJitterBuffer.close();
        if (aaudioOut != null) {
            try {
                aaudioOut.close();
            } catch (Throwable unused) {
            }
            if (this.currentOut == aaudioOut) {
                this.currentOut = null;
            }
        }
        audioSinkCreateSink.release();
        if (this.currentJb == pcmJitterBuffer) {
            this.currentJb = null;
        }
        if (this.currentSink == audioSinkCreateSink) {
            this.currentSink = null;
        }
        emit("stopped", "流已结束", null, 0, 0);
    }

    static int computeBufferMs(String str, int i) {
        if (str != null && str.startsWith("custom:")) {
            try {
                return Math.max(20, Integer.parseInt(str.substring(7)));
            } catch (NumberFormatException unused) {
            }
        }
        if ("low".equals(str)) {
            return 20;
        }
        if ("high".equals(str)) {
            return 80;
        }
        return i == 1 ? 25 : 40;
    }

    /* JADX WARN: Removed duplicated region for block: B:71:0x02b8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void playUdp(String str, int i, final int i2, final int i3, final int i4, String str2, int i5) throws Exception {
        AudioOut.AaudioOut aaudioOut;
        byte[] bArr;
        final boolean z;
        AudioSink audioSink;
        Thread thread;
        Map.Entry entryPollFirstEntry;
        PlayerService playerService = this;
        int i6 = i4;
        DatagramSocket datagramSocket = new DatagramSocket();
        playerService.udpSocket = datagramSocket;
        datagramSocket.setSoTimeout(10000);
        final InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName(str), i + 1000);
        final byte[] bArr2 = MAGIC;
        playerService.udpSocket.send(new DatagramPacket(bArr2, bArr2.length, inetSocketAddress));
        Thread thread2 = new Thread(new Runnable() { // from class: com.wifiaudio.receiver.PlayerService.4
            @Override // java.lang.Runnable
            public void run() throws InterruptedException, IOException {
                while (PlayerService.this.running && PlayerService.this.udpSocket != null && !PlayerService.this.udpSocket.isClosed()) {
                    try {
                        Thread.sleep(2000L);
                        DatagramSocket datagramSocket2 = PlayerService.this.udpSocket;
                        byte[] bArr3 = bArr2;
                        datagramSocket2.send(new DatagramPacket(bArr3, bArr3.length, inetSocketAddress));
                    } catch (Exception unused) {
                        return;
                    }
                }
            }
        }, "udp-heartbeat");
        playerService.udpHeartbeat = thread2;
        thread2.start();
        int i7 = 8192;
        byte[] bArr3 = new byte[8192];
        long jCurrentTimeMillis = System.currentTimeMillis() + 8000;
        while (playerService.running && System.currentTimeMillis() < jCurrentTimeMillis) {
            DatagramPacket datagramPacket = new DatagramPacket(bArr3, i7);
            playerService.udpSocket.receive(datagramPacket);
            if (datagramPacket.getLength() >= 16 && playerService.isMagic(bArr3, datagramPacket.getOffset())) {
                playerService.senderLatencyMs = senderLatencyFromHeader(bArr3, datagramPacket.getOffset());
                final AudioSink audioSinkCreateSink = playerService.createSink(i3, i6, str2, i5);
                audioSinkCreateSink.rate = i3;
                audioSinkCreateSink.channels = i6;
                if (audioSinkCreateSink.bufferMs > 0) {
                    playerService.emit("playing", "抖动缓冲目标 " + udpBufferMs(str2, i5) + "ms（输出缓冲 " + audioSinkCreateSink.bufferMs + "ms）——「缓冲」档位可调", codecName(), i3, i6);
                }
                if (i2 == 0 && "1".equals(playerService.aaudioMode)) {
                    aaudioOut = new AudioOut.AaudioOut();
                    if (aaudioOut.open(i3, i6, udpBufferMs(str2, i5))) {
                        aaudioOut.setVolume(playerService.volume);
                        playerService.currentOut = aaudioOut;
                        addLog("UDP PCM 输出: AAudio（exclusive 低延迟）");
                    } else {
                        addLog("UDP PCM AAudio 打开失败，回退 AudioTrack");
                        aaudioNote = aaudioFallbackNote(true, i3, playerService.deviceNativeRate());
                        aaudioOut = null;
                    }
                } else {
                    aaudioOut = null;
                }
                final AudioOut[] audioOutArr = {aaudioOut};
                final TreeMap treeMap = new TreeMap();
                final boolean z2 = i2 == 0;
                final long j = i6 * 2;
                final int iMax = Math.max(8, i3 / 500);
                final short[] sArr = {0, 0};
                final boolean[] zArr = {true};
                byte[] bArr4 = bArr3;
                final long[] jArr = {0};
                final int iUdpBufferMs = udpBufferMs(str2, i5);
                if (str2 == null || !str2.startsWith("custom:")) {
                    bArr = bArr4;
                    z = true;
                } else {
                    bArr = bArr4;
                    z = false;
                }
                final int[] iArr = {iUdpBufferMs};
                final long[] jArr2 = {-1};
                final int[] iArr2 = {0};
                final long[] jArr3 = {0};
                final boolean[] zArr2 = {false};
                byte[] bArr5 = bArr;
                final WriteAheadGovernor writeAheadGovernor = new WriteAheadGovernor();
                final long[] jArr4 = {0};
                byte[] bArr6 = bArr5;
                Thread thread3 = new Thread(new Runnable() { // from class: com.wifiaudio.receiver.PlayerService.5
                    @Override // java.lang.Runnable
                    public void run() throws InterruptedException {
                        AudioOut audioOut;
                        byte[] bArr7;
                        long j2;
                        boolean z3;
                        int[] iArr3;
                        int i8;
                        boolean z4 = false;
                        boolean z5 = false;
                        while (PlayerService.this.running) {
                            if (!zArr2[0]) {
                                AudioOut audioOut2 = audioOutArr[0];
                                if ((audioOut2 != null ? audioOut2.getBufferMs() : audioSinkCreateSink.getBufferMs()) > writeAheadGovernor.ms) {
                                    long jNanoTime = System.nanoTime();
                                    long[] jArr5 = jArr3;
                                    long j3 = jArr5[0];
                                    if (j3 == 0) {
                                        jArr5[0] = jNanoTime;
                                    } else if ((jNanoTime - j3) / AnimationKt.MillisToNanos > 400) {
                                        zArr2[0] = true;
                                        PlayerService.logStatic("UDP 写前量限制连续推不动（设备缓冲计数可能不可信），暂时放弃限制以保证出声");
                                    }
                                    try {
                                        Thread.sleep(2L);
                                    } catch (InterruptedException unused) {
                                        return;
                                    }
                                } else {
                                    jArr3[0] = 0;
                                }
                            }
                            synchronized (treeMap) {
                                audioOut = null;
                                if (jArr[0] >= iArr[0] || (z4 && !treeMap.isEmpty())) {
                                    Map.Entry entryFirstEntry = treeMap.firstEntry();
                                    bArr7 = (byte[]) entryFirstEntry.getValue();
                                    treeMap.remove(entryFirstEntry.getKey());
                                    long[] jArr6 = jArr;
                                    j2 = 2;
                                    z3 = true;
                                    jArr6[0] = jArr6[0] - PlayerService.pktMs(bArr7.length, z2, j, i3);
                                    long jLongValue = ((Long) entryFirstEntry.getKey()).longValue();
                                    long j4 = jArr2[0];
                                    if (j4 >= 0 && jLongValue != j4 + 1) {
                                        zArr[0] = true;
                                    }
                                    if (z) {
                                        if (j4 >= 0 && jLongValue == j4 + 1) {
                                            int[] iArr4 = iArr2;
                                            int i9 = iArr4[0] + 1;
                                            iArr4[0] = i9;
                                            if (i9 >= 40 && (i8 = (iArr3 = iArr)[0]) > 20) {
                                                iArr3[0] = Math.max(20, i8 - 5);
                                                iArr2[0] = 0;
                                            }
                                        } else {
                                            iArr2[0] = 0;
                                            int[] iArr5 = iArr;
                                            int i10 = iArr5[0];
                                            int i11 = iUdpBufferMs;
                                            if (i10 < i11) {
                                                iArr5[0] = Math.min(i11, i10 + 20);
                                            }
                                        }
                                    }
                                    jArr2[0] = jLongValue;
                                    z4 = true;
                                } else {
                                    j2 = 2;
                                    z3 = true;
                                    bArr7 = null;
                                }
                                PlayerService.this.statUdpTargetMs = iArr[0];
                            }
                            if (bArr7 == null) {
                                long[] jArr7 = jArr4;
                                long j5 = jArr7[0] + 1;
                                jArr7[0] = j5;
                                PlayerService.this.statUdpStarveCount = j5;
                                AudioOut audioOut3 = audioOutArr[0];
                                if (PlayerService.underrunStartsEpisode(z5, (audioOut3 != null ? audioOut3.getBufferMs() : audioSinkCreateSink.getBufferMs()) <= 12 ? z3 : false)) {
                                    PlayerService.this.statUdpUnderruns++;
                                    z5 = z3;
                                }
                                Thread.sleep(j2);
                            } else {
                                writeAheadGovernor.observe(audioOutArr[0] == null ? audioSinkCreateSink : null, jArr4[0]);
                                try {
                                    if (i2 == 0) {
                                        AudioOut audioOut4 = audioOutArr[0];
                                        if (audioOut4 == null || audioOut4.healthy()) {
                                            audioOut = audioOut4;
                                        } else {
                                            PlayerService.logStatic("AAudio 输出失效，切换回 AudioTrack 继续播放(UDP)");
                                            PlayerService.aaudioNote = PlayerService.aaudioFallbackNote(false, i3, PlayerService.this.deviceNativeRate());
                                            try {
                                                audioOut4.close();
                                            } catch (Throwable unused2) {
                                            }
                                            audioOutArr[0] = null;
                                            if (PlayerService.this.currentOut == audioOut4) {
                                                PlayerService.this.currentOut = null;
                                            }
                                        }
                                        byte[] bArr8 = bArr7;
                                        PlayerService.fadeAndTrack(bArr8, bArr7.length, (int) j, i4, iMax, zArr[0], sArr);
                                        zArr[0] = false;
                                        if (audioOut != null) {
                                            audioOut.writePcmBytes(bArr8, bArr8.length);
                                            audioSinkCreateSink.receivedBytes += bArr8.length;
                                            audioSinkCreateSink.writtenFrames += bArr8.length / (i4 * 2);
                                        } else {
                                            audioSinkCreateSink.writePcm(bArr8, bArr8.length);
                                        }
                                    }
                                } catch (Throwable unused3) {
                                }
                                z5 = false;
                            }
                        }
                    }
                }, "udp-play");
                thread3.start();
                while (playerService.running) {
                    byte[] bArr7 = bArr6;
                    DatagramPacket datagramPacket2 = new DatagramPacket(bArr7, 8192);
                    playerService.udpSocket.receive(datagramPacket2);
                    if (datagramPacket2.getLength() >= 16 && playerService.isMagic(bArr7, datagramPacket2.getOffset())) {
                        playerService.senderLatencyMs = senderLatencyFromHeader(bArr7, datagramPacket2.getOffset());
                    } else if (datagramPacket2.getLength() > 4) {
                        int offset = datagramPacket2.getOffset();
                        boolean[] zArr3 = zArr;
                        long[] jArr5 = jArr;
                        int[] iArr3 = iArr;
                        long j2 = ((bArr7[offset + 1] & 255) << 16) | ((bArr7[offset] & 255) << 24) | ((bArr7[offset + 2] & 255) << 8) | (bArr7[offset + 3] & 255);
                        int length = datagramPacket2.getLength() - 4;
                        byte[] bArr8 = new byte[length];
                        System.arraycopy(bArr7, offset + 4, bArr8, 0, length);
                        playerService.statUdpArrivedBytes += length;
                        noteArrival();
                        synchronized (treeMap) {
                            if (treeMap.containsKey(Long.valueOf(j2))) {
                                thread = thread3;
                            } else {
                                treeMap.put(Long.valueOf(j2), bArr8);
                                long jPktMs = jArr5[0] + pktMs(length, z2, j, i3);
                                jArr5[0] = jPktMs;
                                playerService.statJitterMs = (int) jPktMs;
                                boolean z3 = true;
                                long jMax = Math.max(iArr3[0] + 12, (long) Math.ceil(pktMs(Math.max(length, 1), z2, j, i3)));
                                while (jArr5[0] > jMax && (entryPollFirstEntry = treeMap.pollFirstEntry()) != null) {
                                    jArr5[0] = jArr5[0] - pktMs(((byte[]) entryPollFirstEntry.getValue()).length, z2, j, i3);
                                    boolean z4 = z3;
                                    playerService.statUdpDroppedFrames += ((byte[]) entryPollFirstEntry.getValue()).length / j;
                                    zArr3[0] = z4;
                                    thread3 = thread3;
                                    z3 = z4;
                                }
                                thread = thread3;
                            }
                        }
                        zArr = zArr3;
                        jArr = jArr5;
                        iArr = iArr3;
                        bArr6 = bArr7;
                        thread3 = thread;
                    }
                    bArr6 = bArr7;
                }
                Thread thread4 = thread3;
                thread4.interrupt();
                try {
                    thread4.join(1000L);
                } catch (InterruptedException unused) {
                }
                AudioOut audioOut = audioOutArr[0];
                if (audioOut != null) {
                    try {
                        audioOut.close();
                    } catch (Throwable unused2) {
                    }
                    if (playerService.currentOut == audioOut) {
                        audioSink = null;
                        playerService.currentOut = null;
                    } else {
                        audioSink = null;
                    }
                }
                audioSinkCreateSink.release();
                if (playerService.currentSink == audioSinkCreateSink) {
                    playerService.currentSink = audioSink;
                }
                playerService.emit("stopped", "流已结束", null, 0, 0);
                return;
            }
            i7 = 8192;
            playerService = this;
            i6 = i4;
            bArr3 = bArr3;
        }
        emit("error", "UDP 等待格式头超时", null, 0, 0);
    }

    static int udpBufferMs(String str, int i) {
        if (str != null && str.startsWith("custom:")) {
            try {
                return Math.max(20, Integer.parseInt(str.substring(7)));
            } catch (NumberFormatException unused) {
            }
        }
        if ("low".equals(str)) {
            return 20;
        }
        if ("high".equals(str)) {
            return 80;
        }
        return i == 1 ? 20 : 40;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long pktMs(int i, boolean z, long j, int i2) {
        if (z) {
            return (i * 1000) / (j * i2);
        }
        return 20L;
    }

    private boolean isMagic(byte[] bArr, int i) {
        return bArr[i] == 87 && bArr[i + 1] == 70 && bArr[i + 2] == 65 && bArr[i + 3] == 85;
    }

    private static final class AudioSink {
        int bufferMs;
        int channels;
        volatile long outFrames;
        int rate;
        volatile long receivedBytes;
        AudioTrack track;
        volatile long writtenFrames;

        private AudioSink() {
            this.bufferMs = -1;
            this.writtenFrames = 0L;
            this.receivedBytes = 0L;
            this.outFrames = 0L;
            this.rate = 48000;
            this.channels = 2;
        }

        void setVolume(int i) {
            AudioTrack audioTrack = this.track;
            if (audioTrack != null) {
                try {
                    audioTrack.setVolume(i / 100.0f);
                } catch (Throwable unused) {
                }
            }
        }

        void writePcm(byte[] bArr, int i) {
            int iWrite;
            AudioTrack audioTrack = this.track;
            if (audioTrack != null && (iWrite = audioTrack.write(bArr, 0, i)) > 0) {
                this.outFrames += iWrite / (this.channels * 2);
            }
            this.writtenFrames += i / (this.channels * 2);
            this.receivedBytes += i;
        }

        int getBufferMs() {
            return (int) ((bufferedFrames() * 1000) / this.rate);
        }

        long bufferedFrames() {
            if (this.track == null) {
                return 0L;
            }
            long playbackHeadPosition = r0.getPlaybackHeadPosition() & 4294967295L;
            long jPcmBufferedFrames = PlayerService.pcmBufferedFrames(this.outFrames, playbackHeadPosition, this.rate * 2);
            if (jPcmBufferedFrames >= 0) {
                return jPcmBufferedFrames;
            }
            this.outFrames = playbackHeadPosition;
            return 0L;
        }

        void release() {
            AudioTrack audioTrack = this.track;
            if (audioTrack != null) {
                try {
                    audioTrack.stop();
                } catch (Throwable unused) {
                }
                try {
                    this.track.release();
                } catch (Throwable unused2) {
                }
                this.track = null;
            }
        }
    }

    static void fadeAndTrack(byte[] bArr, int i, int i2, int i3, int i4, boolean z, short[] sArr) {
        byte[] bArr2;
        int i5;
        int i6;
        int i7;
        int i8;
        if (z) {
            bArr2 = bArr;
            i5 = i;
            i6 = i2;
            i7 = i3;
            applyFadeToChunk(bArr2, i5, i6, i7, i4, sArr[0], sArr[1]);
        } else {
            bArr2 = bArr;
            i5 = i;
            i6 = i2;
            i7 = i3;
        }
        int i9 = i5 / i6;
        if (i9 > 0) {
            int i10 = (i9 - 1) * i6;
            sArr[0] = (short) ((bArr2[i10] & UByte.MAX_VALUE) | (bArr2[i10 + 1] << 8));
            if (i7 < 2 || (i8 = i10 + 3) >= i5) {
                return;
            }
            sArr[1] = (short) ((bArr2[i10 + 2] & UByte.MAX_VALUE) | (bArr2[i8] << 8));
        }
    }

    static void applyFadeToChunk(byte[] bArr, int i, int i2, int i3, int i4, short s, short s2) {
        int iMin = Math.min(i4, i / i2);
        if (iMin <= 0) {
            return;
        }
        int i5 = 0;
        while (i5 < iMin) {
            int i6 = i5 * i2;
            i5++;
            double d = i5 / iMin;
            double d2 = 1.0d - d;
            int iRound = (int) Math.round((s * d2) + (((short) ((bArr[i6] & UByte.MAX_VALUE) | (bArr[r5] << 8))) * d));
            bArr[i6] = (byte) (iRound & 255);
            bArr[i6 + 1] = (byte) ((iRound >> 8) & 255);
            if (i3 >= 2) {
                int i7 = i6 + 2;
                int iRound2 = (int) Math.round((s2 * d2) + (((short) ((bArr[i7] & UByte.MAX_VALUE) | (bArr[r1] << 8))) * d));
                bArr[i7] = (byte) (iRound2 & 255);
                bArr[i6 + 3] = (byte) ((iRound2 >> 8) & 255);
            }
        }
    }

    static int writeBudgetFrames(int i, int i2, int i3) {
        if (i <= 0 || i3 <= 0) {
            return 0;
        }
        if (i2 < 0) {
            i2 = 0;
        }
        long j = i;
        int i4 = (int) (((i3 - i2) * j) / 1000);
        if (i4 <= 0) {
            return 0;
        }
        int i5 = (int) ((j * 10) / 1000);
        if (i5 < 1) {
            i5 = 1;
        }
        return Math.min(i4, i5);
    }

    static int adaptWriteAheadMs(int i, long j, boolean z) {
        if (j <= 0 || z) {
            return i;
        }
        int i2 = ((j > 2 ? 2 : 1) * 10) + i;
        if (i2 > 60) {
            i2 = 60;
        }
        return Math.max(i, i2);
    }

    private static final class WriteAheadGovernor {
        private long lastCheckMs;
        private long lastQueueUnderruns;
        private int lastUnderruns;
        volatile int ms;

        private WriteAheadGovernor() {
            this.ms = 30;
            this.lastUnderruns = -1;
        }

        void observe(AudioSink audioSink, long j) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (jCurrentTimeMillis - this.lastCheckMs < 500) {
                return;
            }
            this.lastCheckMs = jCurrentTimeMillis;
            if (audioSink == null || audioSink.track == null) {
                return;
            }
            try {
                int underrunCount = audioSink.track.getUnderrunCount();
                int i = this.lastUnderruns;
                if (i >= 0 && underrunCount > i) {
                    long j2 = underrunCount - i;
                    boolean z = j > this.lastQueueUnderruns;
                    int i2 = this.ms;
                    this.ms = PlayerService.adaptWriteAheadMs(this.ms, j2, z);
                    if (this.ms != i2) {
                        PlayerService.logStatic("输出设备欠载 +" + j2 + "（累计 " + underrunCount + "）→ 写前量自动放宽 " + i2 + "→" + this.ms + "ms（延迟会相应增加，这是为了先保证不断音）");
                    } else if (!z) {
                        PlayerService.logStatic("输出设备欠载 +" + j2 + "（累计 " + underrunCount + "）但写前量已是上限 " + this.ms + "ms，建议把「缓冲」档位调高一档");
                    }
                }
                this.lastUnderruns = underrunCount;
                this.lastQueueUnderruns = j;
            } catch (Throwable unused) {
            }
        }
    }

    static int adaptTargetMs(int i, int i2, int i3, boolean z) {
        if (z) {
            return Math.min(i + 10, i3);
        }
        return Math.max(i - 10, i2);
    }

    static boolean pcmBitrateSane(int i, int i2, int i3) {
        if (i2 <= 0 || i3 <= 0) {
            return true;
        }
        long j = ((i2 * 16) * i3) / 1000;
        return j <= 0 || Math.abs(((long) i) - j) <= Math.max(64L, j / 5);
    }

    static String aaudioFallbackNote(boolean z, int i, int i2) {
        StringBuilder sb = new StringBuilder(z ? "AAudio 打开失败" : "AAudio 已失效回退");
        if (i2 > 0 && i > 0 && i2 != i) {
            sb.append("：本机原生 ").append(i2 / 1000).append("kHz 与流的 ").append(i / 1000).append("kHz 不符（AAudio 不做重采样，独占被拒时会降级到本机原生采样率）");
        } else if (i > 48000) {
            sb.append("；流 ").append(i / 1000).append("kHz，建议把发射端采样率设为 48kHz");
        }
        return sb.toString();
    }

    private static void noteArrival() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j = lastArrivalAtMs != 0 ? jCurrentTimeMillis - lastArrivalAtMs : 0L;
        lastArrivalAtMs = jCurrentTimeMillis;
        if (j > statMaxArrivalGapMs) {
            statMaxArrivalGapMs = j;
        }
    }

    static String jitterBufferAdvice(int i, int i2) {
        if (i <= 0 || i2 <= 0 || i <= i2 + 12) {
            return null;
        }
        return "链路到达间隔最大 " + i + "ms，超过当前缓冲档位 " + i2 + "ms —— 该档位会周期性被吃空（断续）。建议把「缓冲」调到 ≥" + (((i + 9) / 10) * 10) + "ms，或改用 5G / 缩短距离";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int deviceNativeRate() {
        String property;
        try {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            if (audioManager == null || (property = audioManager.getProperty("android.media.property.OUTPUT_SAMPLE_RATE")) == null) {
                return 0;
            }
            return Integer.parseInt(property.trim());
        } catch (Throwable unused) {
            return 0;
        }
    }

    final class PcmJitterBuffer {
        static final int QUEUE_MARGIN_MS = 12;
        final boolean adaptive;
        volatile long arrivedBytes;
        double avgGapMs;
        final int channels;
        volatile boolean closed;
        volatile long droppedFrames;
        final int fadeFrames;
        final int frameBytes;
        double jitterEwma;
        volatile int jitterMs;
        long lastAdaptNanos;
        long lastArrivalNanos;
        long lastJitterWarnTime;
        short lastL;
        short lastR;
        volatile long lastUnderrunTime;
        final int maxMs;
        final int minMs;
        volatile AudioOut out;
        int overTargetStreak;
        long queuedFrames;
        final int rate;
        final AudioSink sink;
        volatile int targetMs;
        boolean throttleBypass;
        long throttleSinceNanos;
        volatile long underruns;
        final ArrayDeque<byte[]> queue = new ArrayDeque<>();
        volatile boolean needsFade = true;
        final Thread playThread = new Thread(new Runnable() { // from class: com.wifiaudio.receiver.PlayerService.PcmJitterBuffer.1
            @Override // java.lang.Runnable
            public void run() throws InterruptedException {
                PcmJitterBuffer.this.playLoop();
            }
        }, "pcm-jitter");
        final WriteAheadGovernor ahead = new WriteAheadGovernor();

        PcmJitterBuffer(AudioSink audioSink, AudioOut audioOut, int i, int i2, int i3, boolean z) {
            this.sink = audioSink;
            this.out = audioOut;
            this.rate = i;
            this.channels = i2;
            this.frameBytes = i2 * 2;
            this.adaptive = z;
            if (!z) {
                int iMax = Math.max(20, i3);
                this.maxMs = iMax;
                this.minMs = iMax;
            } else {
                int iMax2 = Math.max(20, (i3 * 3) / 5);
                this.minMs = iMax2;
                this.maxMs = Math.max(iMax2 + 40, i3 * 2);
            }
            this.targetMs = Math.max(20, i3);
            this.fadeFrames = Math.max(8, i / 500);
        }

        void start() {
            this.playThread.start();
        }

        void close() throws InterruptedException {
            this.closed = true;
            this.playThread.interrupt();
            try {
                this.playThread.join(500L);
            } catch (InterruptedException unused) {
            }
        }

        synchronized void write(byte[] bArr, int i) {
            byte[] bArrPollFirst;
            if (!this.closed && i > 0) {
                this.arrivedBytes += i;
                long jNanoTime = System.nanoTime();
                if (this.lastArrivalNanos != 0) {
                    double d = (jNanoTime - r2) / 1000000.0d;
                    if (d > 0.1d && d < 2000.0d) {
                        double d2 = this.avgGapMs;
                        double d3 = d2 == 0.0d ? d : (d2 * 0.95d) + (0.05d * d);
                        this.avgGapMs = d3;
                        double dAbs = Math.abs(d - d3);
                        double d4 = this.jitterEwma;
                        if (d4 != 0.0d) {
                            dAbs = (dAbs * 0.1d) + (d4 * 0.9d);
                        }
                        this.jitterEwma = dAbs;
                    }
                }
                this.lastArrivalNanos = jNanoTime;
                int i2 = this.frameBytes;
                int iMax = Math.max(i2, ((int) ((this.rate * 10) / 1000)) * i2);
                for (int i3 = 0; i3 < i; i3 += iMax) {
                    int iMin = Math.min(iMax, i - i3);
                    byte[] bArr2 = new byte[iMin];
                    System.arraycopy(bArr, i3, bArr2, 0, iMin);
                    this.queue.addLast(bArr2);
                    this.queuedFrames += iMin / this.frameBytes;
                }
                long j = ((this.targetMs + 12) * this.rate) / 1000;
                while (this.queuedFrames > j && (bArrPollFirst = this.queue.pollFirst()) != null) {
                    this.queuedFrames -= bArrPollFirst.length / this.frameBytes;
                    this.droppedFrames += bArrPollFirst.length / this.frameBytes;
                    this.needsFade = true;
                }
            }
        }

        private synchronized byte[] pollChunk() {
            if (this.queue.isEmpty()) {
                return null;
            }
            byte[] bArrPollFirst = this.queue.pollFirst();
            this.queuedFrames -= bArrPollFirst.length / this.frameBytes;
            return bArrPollFirst;
        }

        int queueMs() {
            long j;
            synchronized (this) {
                j = this.queuedFrames;
            }
            return (int) ((j * 1000) / this.rate);
        }

        private synchronized void dropSamples(long j) {
            if (j > 0) {
                try {
                    this.needsFade = true;
                } catch (Throwable th) {
                    throw th;
                }
            }
            while (j > 0) {
                if (this.queue.isEmpty()) {
                    break;
                }
                byte[] bArrPollFirst = this.queue.pollFirst();
                long length = bArrPollFirst.length / this.frameBytes;
                long jMin = Math.min(length, j);
                if (jMin < length) {
                    int i = this.frameBytes;
                    int i2 = (int) ((length - jMin) * i);
                    byte[] bArr = new byte[i2];
                    System.arraycopy(bArrPollFirst, (int) (i * jMin), bArr, 0, i2);
                    this.queue.addFirst(bArr);
                    this.queuedFrames -= jMin;
                    this.droppedFrames += jMin;
                    j = 0;
                } else {
                    this.queuedFrames -= length;
                    this.droppedFrames += length;
                    j -= length;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void playLoop() throws InterruptedException {
            long j;
            long jNanoTime = System.nanoTime();
            while (!this.closed) {
                synchronized (this) {
                    j = this.queuedFrames;
                }
                if ((j * 1000) / this.rate >= this.targetMs || (System.nanoTime() - jNanoTime) / 1000000.0d > 2500.0d) {
                    break;
                }
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException unused) {
                    return;
                }
            }
            while (true) {
                boolean z = false;
                while (!this.closed) {
                    AudioOut audioOut = this.out;
                    if (audioOut != null && !audioOut.healthy()) {
                        PlayerService.logStatic("AAudio 输出失效，切换回 AudioTrack 继续播放");
                        PlayerService.aaudioNote = PlayerService.aaudioFallbackNote(false, this.rate, PlayerService.this.deviceNativeRate());
                        try {
                            audioOut.close();
                        } catch (Throwable unused2) {
                        }
                        this.out = null;
                        if (PlayerService.this.currentOut == audioOut) {
                            PlayerService.this.currentOut = null;
                        }
                        audioOut = null;
                    }
                    if (!this.throttleBypass) {
                        if (PlayerService.writeBudgetFrames(this.rate, audioOut != null ? audioOut.getBufferMs() : this.sink.getBufferMs(), this.ahead.ms) <= 0) {
                            long jNanoTime2 = System.nanoTime();
                            long j2 = this.throttleSinceNanos;
                            if (j2 == 0) {
                                this.throttleSinceNanos = jNanoTime2;
                            } else if ((jNanoTime2 - j2) / AnimationKt.MillisToNanos > 400) {
                                this.throttleBypass = true;
                                PlayerService.logStatic("写前量限制连续 400ms 推不动（设备缓冲计数可能不可信），暂时放弃限制以保证出声");
                            }
                            try {
                                Thread.sleep(2L);
                            } catch (InterruptedException unused3) {
                                return;
                            }
                        } else {
                            this.throttleSinceNanos = 0L;
                        }
                    }
                    byte[] bArrPollChunk = pollChunk();
                    if (bArrPollChunk == null) {
                        if (PlayerService.underrunStartsEpisode(z, (audioOut != null ? audioOut.getBufferMs() : this.sink.getBufferMs()) <= 12)) {
                            this.underruns++;
                            this.lastUnderrunTime = System.currentTimeMillis();
                            z = true;
                        }
                        Thread.sleep(10L);
                    } else {
                        if (this.needsFade) {
                            applyFade(bArrPollChunk, bArrPollChunk.length);
                            this.needsFade = false;
                        }
                        if (audioOut != null) {
                            try {
                                audioOut.writePcmBytes(bArrPollChunk, bArrPollChunk.length);
                            } catch (Throwable unused4) {
                            }
                        } else {
                            this.sink.writePcm(bArrPollChunk, bArrPollChunk.length);
                        }
                        rememberTail(bArrPollChunk, bArrPollChunk.length);
                        maybeAdapt();
                    }
                }
                return;
            }
        }

        private void applyFade(byte[] bArr, int i) {
            PlayerService.applyFadeToChunk(bArr, i, this.frameBytes, this.channels, this.fadeFrames, this.lastL, this.lastR);
        }

        private void rememberTail(byte[] bArr, int i) {
            int i2 = this.frameBytes;
            int i3 = (i / i2) * i2;
            if (i3 < i2) {
                return;
            }
            int i4 = i3 - i2;
            this.lastL = (short) ((bArr[i4] & UByte.MAX_VALUE) | (bArr[i4 + 1] << 8));
            if (this.channels >= 2) {
                this.lastR = (short) ((bArr[i4 + 3] << 8) | (bArr[i4 + 2] & UByte.MAX_VALUE));
            }
        }

        private void maybeAdapt() {
            int i;
            long jNanoTime = System.nanoTime();
            if (this.lastAdaptNanos == 0 || (jNanoTime - r2) / 1000000.0d >= 500.0d) {
                this.lastAdaptNanos = jNanoTime;
                this.jitterMs = (int) Math.round(this.jitterEwma);
                if (this.adaptive && this.jitterMs > this.targetMs && this.targetMs < this.maxMs && System.currentTimeMillis() - this.lastJitterWarnTime > 10000) {
                    this.lastJitterWarnTime = System.currentTimeMillis();
                    PlayerService.logStatic("网络到达抖动约 " + this.jitterMs + "ms，已超过当前缓冲水位 " + this.targetMs + "ms，可能出现断续；建议把「缓冲」档位调高一档");
                }
                synchronized (this) {
                    i = (int) ((this.queuedFrames * 1000) / this.rate);
                }
                if (this.adaptive) {
                    this.targetMs = PlayerService.adaptTargetMs(this.targetMs, this.minMs, this.maxMs, System.currentTimeMillis() - this.lastUnderrunTime < 3000);
                }
                if (i > this.targetMs + 40) {
                    int i2 = this.overTargetStreak + 1;
                    this.overTargetStreak = i2;
                    if (i2 >= 4) {
                        dropSamples(this.rate / ComposerKt.invocationKey);
                        this.overTargetStreak = 0;
                    }
                } else {
                    this.overTargetStreak = 0;
                }
                this.ahead.observe(this.out == null ? this.sink : null, this.underruns);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0066  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static int computeBufferBytes(int i, int i2, String str, int i3, boolean z) throws NumberFormatException {
        int i4;
        int i5 = 4;
        int minBufferSize = AudioTrack.getMinBufferSize(i, i2 >= 2 ? 12 : 4, 2);
        if (z) {
            long j = i * (i2 >= 2 ? 2 : 1) * 2;
            return Math.max((int) (j / 100), Math.min(minBufferSize, (int) ((j * 30) / 1000)));
        }
        if (str == null || !str.startsWith("custom:")) {
            i4 = -1;
        } else {
            try {
                i4 = Integer.parseInt(str.substring(7));
            } catch (NumberFormatException unused) {
            }
        }
        if (i4 > 0) {
            return Math.max(minBufferSize, (int) Math.round((((i * 2.0d) * (i2 < 2 ? 1 : 2)) * i4) / 1000.0d));
        }
        if (!"low".equals(str)) {
            if ("high".equals(str)) {
                i5 = 6;
            } else if (i3 == 1) {
                i5 = 2;
            }
        }
        return Math.max(minBufferSize * i5, 8192);
    }

    private AudioSink createSink(int i, int i2, String str, int i3) throws Exception {
        AudioSink audioSink = new AudioSink();
        this.currentSink = audioSink;
        int i4 = i2 >= 2 ? 12 : 4;
        AudioTrack.getMinBufferSize(i, i4, 2);
        AudioTrack.Builder bufferSizeInBytes = new AudioTrack.Builder().setAudioAttributes(new AudioAttributes.Builder().setUsage(1).setContentType(2).build()).setAudioFormat(new AudioFormat.Builder().setEncoding(2).setSampleRate(i).setChannelMask(i4).build()).setTransferMode(1).setBufferSizeInBytes(computeBufferBytes(i, i2, str, i3, true));
        try {
            bufferSizeInBytes.setPerformanceMode(1);
        } catch (Throwable unused) {
        }
        audioSink.track = bufferSizeInBytes.build();
        audioSink.track.play();
        audioSink.bufferMs = (int) Math.round((audioSink.track.getBufferSizeInFrames() * 1000.0d) / i);
        return audioSink;
    }

    private static String codecName() {
        return "PCM";
    }

    private static boolean readFully(InputStream inputStream, byte[] bArr) throws IOException {
        int i = 0;
        while (i < bArr.length) {
            int i2 = inputStream.read(bArr, i, bArr.length - i);
            if (i2 > 0) {
                noteArrival();
            }
            if (i2 < 0) {
                return false;
            }
            i += i2;
        }
        return true;
    }

    static int senderLatencyFromHeader(byte[] bArr, int i) {
        if (bArr != null && i >= 0 && bArr.length >= i + 16) {
            int i2 = ((bArr[i + 15] & UByte.MAX_VALUE) << 8) | (bArr[i + 14] & UByte.MAX_VALUE);
            if (i2 > 0 && i2 <= 5000) {
                return i2;
            }
        }
        return 0;
    }

    private static long leInt(byte[] bArr, int i) {
        return ((bArr[i + 3] & 255) << 24) | (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << 16);
    }

    private void emit(String str, String str2, String str3, int i, int i2) {
        if (stateListener != null) {
            try {
                stateListener.onState(str, str2, str3, i, i2);
            } catch (Throwable unused) {
            }
        }
    }

    private void startStatsThread() {
        if (this.statsThread != null) {
            return;
        }
        Thread thread = new Thread(new Runnable() { // from class: com.wifiaudio.receiver.PlayerService.6
            @Override // java.lang.Runnable
            public void run() throws InterruptedException {
                long j;
                int bufferMs;
                String strName;
                String strJitterBufferAdvice;
                int iRound;
                long jCurrentTimeMillis = System.currentTimeMillis();
                long j2 = 0;
                long j3 = 0;
                while (PlayerService.this.running && !Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(1000L);
                        AudioSink audioSink = PlayerService.this.currentSink;
                        AudioOut audioOut = PlayerService.this.currentOut;
                        PcmJitterBuffer pcmJitterBuffer = PlayerService.this.currentJb;
                        if (audioSink != null || pcmJitterBuffer != null) {
                            long jCurrentTimeMillis2 = System.currentTimeMillis();
                            if (pcmJitterBuffer != null) {
                                j = pcmJitterBuffer.arrivedBytes;
                            } else if (PlayerService.this.statUdpArrivedBytes > j2) {
                                j = PlayerService.this.statUdpArrivedBytes;
                            } else {
                                j = audioSink != null ? audioSink.receivedBytes : j2;
                            }
                            int iMax = (int) (((j - j3) * 8) / Math.max(1L, jCurrentTimeMillis2 - jCurrentTimeMillis));
                            if (audioOut != null) {
                                bufferMs = audioOut.getBufferMs();
                            } else {
                                bufferMs = audioSink != null ? audioSink.getBufferMs() : 0;
                            }
                            int i = bufferMs >= 0 ? bufferMs : 0;
                            int iQueueMs = pcmJitterBuffer != null ? pcmJitterBuffer.queueMs() : PlayerService.this.statJitterMs;
                            int i2 = iQueueMs + i;
                            if (pcmJitterBuffer != null) {
                                PlayerService.this.statJitterMs = iQueueMs;
                            }
                            if (PlayerService.this.statCodecName != null && PlayerService.this.statCodecName.equals("PCM") && PlayerService.this.statHdrRate > 0 && PlayerService.this.statChannels > 0 && !PlayerService.pcmBitrateSane(iMax, PlayerService.this.statHdrRate, PlayerService.this.statChannels) && jCurrentTimeMillis2 - PlayerService.this.lastBitrateWarnAt > 10000) {
                                PlayerService.this.lastBitrateWarnAt = jCurrentTimeMillis2;
                                PlayerService.addLog("到达码率 " + iMax + " kbps 与 PCM 标称 " + (((PlayerService.this.statHdrRate * 16) * PlayerService.this.statChannels) / 1000) + " kbps 不符（协议头 " + (PlayerService.this.statHdrRate / 1000) + "kHz/" + PlayerService.this.statChannels + "ch）—— 到达量与应有值不一致，是队列堆积/被吃空的可疑方向，请把这条日志一并反馈");
                            }
                            if (PlayerService.this.statCodecName != null && PlayerService.this.statCodecName.equals("PCM") && PlayerService.this.statChannels > 0 && (iRound = (int) Math.round(((iMax * 1000.0d) / 8.0d) / (PlayerService.this.statChannels * 2))) >= 8000 && iRound <= 384000) {
                                PlayerService.this.statRate = iRound;
                            }
                            PlayerService playerService = PlayerService.this;
                            playerService.statSenderRawMs = PlayerService.senderLatencyForDisplay(playerService.senderLatencyMs);
                            int i3 = (PlayerService.this.statSenderRawMs > 0 ? PlayerService.this.statSenderRawMs : 40) + i2 + 5;
                            int i4 = pcmJitterBuffer != null ? pcmJitterBuffer.targetMs : PlayerService.this.statUdpTargetMs;
                            long j4 = pcmJitterBuffer != null ? pcmJitterBuffer.droppedFrames : PlayerService.this.statUdpDroppedFrames;
                            long j5 = pcmJitterBuffer != null ? pcmJitterBuffer.underruns : PlayerService.this.statUdpUnderruns;
                            if (!PlayerService.this.arrivalAdviceLogged && (strJitterBufferAdvice = PlayerService.jitterBufferAdvice((int) PlayerService.statMaxArrivalGapMs, i4)) != null) {
                                PlayerService.this.arrivalAdviceLogged = true;
                                PlayerService.addLog(strJitterBufferAdvice);
                            }
                            if (audioOut != null) {
                                strName = audioOut.name();
                            } else {
                                strName = audioSink != null ? "AudioTrack" : "";
                            }
                            if (audioOut == null && audioSink != null && PlayerService.aaudioNote != null && !PlayerService.aaudioNote.isEmpty()) {
                                strName = strName + "（" + PlayerService.aaudioNote + "）";
                            }
                            PlayerService.this.emitStats(iMax, i2, i3, i4, j4, j5, strName);
                            jCurrentTimeMillis = jCurrentTimeMillis2;
                            j3 = j;
                            j2 = 0;
                        }
                    } catch (InterruptedException unused) {
                        return;
                    }
                }
            }
        }, "stats");
        this.statsThread = thread;
        thread.start();
    }

    private void playTestTone() {
        addLog("测试音: 1kHz 正弦波 1 秒");
        new Thread(new Runnable() { // from class: com.wifiaudio.receiver.PlayerService.7
            @Override // java.lang.Runnable
            public void run() {
                AudioTrack audioTrackBuild = null;
                try {
                    try {
                        int minBufferSize = AudioTrack.getMinBufferSize(48000, 4, 2);
                        audioTrackBuild = new AudioTrack.Builder().setAudioAttributes(new AudioAttributes.Builder().setUsage(1).build()).setAudioFormat(new AudioFormat.Builder().setEncoding(2).setSampleRate(48000).setChannelMask(4).build()).setBufferSizeInBytes(minBufferSize * 2).setTransferMode(1).build();
                        audioTrackBuild.play();
                        int i = minBufferSize / 2;
                        short[] sArr = new short[i];
                        for (int i2 = 0; i2 < i; i2++) {
                            sArr[i2] = (short) (Math.sin((i2 * 6283.185307179586d) / 48000.0d) * 8000.0d);
                        }
                        long jCurrentTimeMillis = System.currentTimeMillis();
                        while (System.currentTimeMillis() - jCurrentTimeMillis < 1000) {
                            audioTrackBuild.write(sArr, 0, i);
                        }
                        PlayerService.addLog("测试音播放完成（若听到 1kHz 声音说明扬声器正常）");
                        if (audioTrackBuild != null) {
                            audioTrackBuild.stop();
                            audioTrackBuild.release();
                        }
                    } catch (Throwable th) {
                        try {
                            PlayerService.addLog("测试音失败: " + th.getMessage());
                            if (audioTrackBuild != null) {
                                audioTrackBuild.stop();
                                audioTrackBuild.release();
                            }
                        } catch (Throwable th2) {
                            if (audioTrackBuild != null) {
                                try {
                                    audioTrackBuild.stop();
                                    audioTrackBuild.release();
                                } catch (Throwable unused) {
                                }
                            }
                            throw th2;
                        }
                    }
                } catch (Throwable unused2) {
                }
            }
        }, "test-tone").start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void emitStats(int i, int i2, int i3, int i4, long j, long j2, String str) {
        ArrayList<String> arrayList;
        StatsListener statsListener2 = statsListener;
        if (statsListener2 != null) {
            try {
                List<String> list = logBuf;
                synchronized (list) {
                    arrayList = new ArrayList<>(list.subList(Math.max(0, list.size() - 30), list.size()));
                }
                statsListener2.onStats(this.statTransport, this.statCodecName, this.statRate, this.statChannels, i, i2, this.statJitterMs, i3, (int) ((this.currentSink != null ? this.currentSink.receivedBytes : 0L) / 1024), (int) (this.currentSink != null ? this.currentSink.writtenFrames : 0L), i4, j, j2, str, arrayList, this.statSenderRawMs);
            } catch (Throwable unused) {
            }
        }
        try {
            Intent intent = new Intent(ACTION_STATS);
            intent.putExtra(EXTRA_CUR_TRANSPORT, this.statTransport);
            intent.putExtra(EXTRA_CODEC, this.statCodecName);
            intent.putExtra(EXTRA_RATE, this.statRate);
            intent.putExtra(EXTRA_CHANNELS, this.statChannels);
            intent.putExtra(EXTRA_BITRATE, i);
            intent.putExtra(EXTRA_AUDIO_MS, i2);
            intent.putExtra(EXTRA_JITTER_MS, this.statJitterMs);
            intent.putExtra(EXTRA_LATENCY_MS, i3);
            intent.putExtra(EXTRA_RX_KB, (int) ((this.currentSink != null ? this.currentSink.receivedBytes : 0L) / 1024));
            intent.putExtra(EXTRA_DEC_FRAMES, (int) (this.currentSink != null ? this.currentSink.writtenFrames : 0L));
            intent.putExtra(EXTRA_TARGET_MS, i4);
            intent.putExtra(EXTRA_DROPPED, j);
            intent.putExtra(EXTRA_UNDERRUN, j2);
            intent.putExtra(EXTRA_OUTPUT, str == null ? "" : str);
            intent.putExtra(EXTRA_SENDER_MS, this.statSenderRawMs);
            List<String> list2 = logBuf;
            synchronized (list2) {
                intent.putStringArrayListExtra(EXTRA_LOG, new ArrayList<>(list2.subList(Math.max(0, list2.size() - 30), list2.size())));
            }
            sendBroadcast(intent);
        } catch (Throwable unused2) {
        }
    }

    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "音频播放", 2);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private Notification buildNotification(String str) {
        return new Notification.Builder(this, CHANNEL_ID).setContentTitle("WiFiAudio 接收端").setContentText(str).setSmallIcon(android.R.drawable.stat_sys_headset).setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, (Class<?>) MainActivity.class), AccessibilityEventCompat.TYPE_VIEW_TARGETED_BY_SCROLL)).setOngoing(true).build();
    }

    private void updateNotification(String str) {
        try {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.notify(1, buildNotification(str));
            }
        } catch (Throwable unused) {
        }
    }

    @Override // android.app.Service
    public void onDestroy() throws Throwable {
        addLog("======== 接收端服务停止 ========");
        ArrayBlockingQueue<String> arrayBlockingQueue = logQueue;
        for (int i = 0; i < 30 && arrayBlockingQueue != null && !arrayBlockingQueue.isEmpty(); i++) {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException unused) {
            }
        }
        stopPlayback();
        super.onDestroy();
    }
}
