package com.wifiaudio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * 蜂窝通话（VoLTE/CS 电话）音频捕获 —— 尽力而为。
 * 使用 AudioRecord VOICE_CALL 源（root 可绕过 CAPTURE_AUDIO_OUTPUT 限制），
 * 但能否真正拿到数据取决于设备 HAL 支持（高通平台一般支持）。
 * 自动检测：创建/读取失败则标记不支持，静默降级，不影响主功能。
 */
public final class CallCapture {

    private final Context context;
    private final Mixer mixer;
    private volatile boolean supported = false;
    private volatile boolean inCall = false;
    private volatile AudioRecord recorder;
    private volatile Thread thread;
    private volatile boolean running = false;

    private int callSampleRate = 16000; // 实际协商的采样率
    private int callChannels = 1;

    public CallCapture(Context context, Mixer mixer) {
        this.context = context;
        this.mixer = mixer;
    }

    /** 检测设备是否支持通话捕获（一次性探测） */
    public boolean probe() {
        AudioRecord r = tryCreate();
        if (r == null) {
            Util.log("Call", "VOICE_CALL capture NOT supported on this device (HAL limitation)");
            supported = false;
            return false;
        }
        r.release();
        supported = true;
        Util.log("Call", "VOICE_CALL capture supported, sampleRate=" + callSampleRate
                + " ch=" + callChannels + " (Android " + Build.VERSION.SDK_INT + ")");
        return true;
    }

    private AudioRecord tryCreate() {
        int[] rates = {48000, 44100, 32000, 16000, 8000};
        for (int rate : rates) {
            for (int ch : new int[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
                try {
                    int minBuf = AudioRecord.getMinBufferSize(rate, ch, AudioFormat.ENCODING_PCM_16BIT);
                    if (minBuf <= 0) continue;
                    AudioRecord r = new AudioRecord(MediaRecorder.AudioSource.VOICE_CALL, rate, ch,
                            AudioFormat.ENCODING_PCM_16BIT, Math.max(minBuf * 2, 8192));
                    if (r.getState() == AudioRecord.STATE_INITIALIZED) {
                        callSampleRate = rate;
                        callChannels = (ch == AudioFormat.CHANNEL_IN_STEREO) ? 2 : 1;
                        return r;
                    }
                    r.release();
                } catch (Throwable ignored) {
                    // 该组合不可用，继续尝试
                }
            }
        }
        return null;
    }

    public void start() {
        if (!supported || running) return;
        running = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loop();
            }
        }, "call-capture");
        thread.start();
    }

    public void stop() {
        running = false;
        if (recorder != null) {
            try {
                recorder.release();
            } catch (Throwable ignored) {
            }
            recorder = null;
        }
        if (thread != null) {
            try {
                thread.interrupt();
            } catch (Throwable ignored) {
            }
            thread = null;
        }
    }

    private void loop() {
        // 监听通话状态
        TelephonyManager tm;
        try {
            tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) {
                Util.log("Call", "No telephony service, call capture disabled");
                return;
            }
            tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Throwable t) {
            // 部分 ROM 的 TelephonyManager 服务异常（如 LG/LineageOS），通话状态监听不可用
            // 通话捕获降级：无法自动在通话时启动，但不影响主功能
            Util.log("Call", "通话状态监听不可用（此设备可能无法自动触发通话捕获）: " + Util.causeMsg(t));
            return;
        }

        byte[] buf = new byte[callChannels * 2 * 1024];
        while (running) {
            AudioRecord r = recorder;
            if (r != null && inCall) {
                int n = r.read(buf, 0, buf.length);
                if (n > 0) {
                    int frames = n / (2 * callChannels);
                    mixer.mixCallPcm(buf, frames, callChannels, callSampleRate);
                    Status.callCaptureActive = true;
                } else if (n < 0) {
                    // 读取失败，尝试重建
                    Util.log("Call", "read error " + n + ", recreating recorder");
                    releaseRecorder();
                }
            } else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private final PhoneStateListener callStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            boolean nowInCall = (state == TelephonyManager.CALL_STATE_OFFHOOK);
            if (nowInCall == inCall) return;
            inCall = nowInCall;
            Status.callActive = inCall;
            if (inCall) {
                Util.log("Call", "Call started, opening VOICE_CALL recorder");
                ensureRecorder();
            } else {
                Util.log("Call", "Call ended");
                Status.callCaptureActive = false;
                releaseRecorder();
            }
        }
    };

    private synchronized void ensureRecorder() {
        if (recorder != null) return;
        AudioRecord r = tryCreate();
        if (r == null) {
            Util.log("Call", "Cannot open VOICE_CALL recorder in call");
            return;
        }
        try {
            r.startRecording();
            recorder = r;
            Util.log("Call", "VOICE_CALL recorder started: " + callSampleRate + "Hz ch=" + callChannels);
        } catch (Throwable t) {
            Util.log("Call", "startRecording failed", t);
            r.release();
        }
    }

    private synchronized void releaseRecorder() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
            } catch (Throwable ignored) {
            }
            recorder = null;
        }
    }
}
