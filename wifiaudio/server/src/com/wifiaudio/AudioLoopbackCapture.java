package com.wifiaudio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTimestamp;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public final class AudioLoopbackCapture {
    public static final int BYTES_PER_SAMPLE = 2;
    private static final int[] CAPTURED_USAGES = {1, 14, 0, 16, 11, 12, 13, 5, 6, 10, 4, 2, 3};
    public static final int CHANNELS = 2;
    public static final int DEFAULT_SAMPLE_RATE = 48000;
    public static final int ENCODING = 2;
    public static final int MAX_READ_BYTES = 2048;
    public static final int MAX_READ_FRAMES = 512;
    private long framesRead;
    private boolean keepPlayingOnDevice;
    private long lastLatencyProbeNanos;
    private AudioRecord recorder;
    private int sampleRate = 48000;
    private volatile int captureLatencyMs = 0;
    private int recordBufferFrames = -1;

    public AudioLoopbackCapture(boolean z) {
        this.keepPlayingOnDevice = z;
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public int getCaptureLatencyMs() {
        return this.captureLatencyMs;
    }

    public int getRecordBufferFrames() {
        return this.recordBufferFrames;
    }

    public static int captureLatencyMs(long j, long j2, long j3, long j4, int i) {
        if (i <= 0) {
            return -1;
        }
        long j5 = (j - (j2 + (((j3 - j4) * 1000000000) / i))) / 1000000;
        if (j5 < 0 || j5 > 5000) {
            return -1;
        }
        return (int) j5;
    }

    private void probeCaptureLatency() {
        int iCaptureLatencyMs;
        long jNanoTime = System.nanoTime();
        if (jNanoTime - this.lastLatencyProbeNanos < 500000000) {
            return;
        }
        this.lastLatencyProbeNanos = jNanoTime;
        try {
            AudioTimestamp audioTimestamp = new AudioTimestamp();
            this.recorder.getTimestamp(audioTimestamp, 0);
            if (audioTimestamp.nanoTime <= 0 || audioTimestamp.framePosition < 0 || (iCaptureLatencyMs = captureLatencyMs(System.nanoTime(), audioTimestamp.nanoTime, this.framesRead, audioTimestamp.framePosition, this.sampleRate)) < 0) {
                return;
            }
            this.captureLatencyMs = iCaptureLatencyMs;
        } catch (Throwable unused) {
        }
    }

    public static int detectDeviceSampleRate(Context context) {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService("audio");
            if (audioManager == null) {
                return 48000;
            }
            int i = 48000;
            for (AudioDeviceInfo audioDeviceInfo : audioManager.getDevices(2)) {
                if (audioDeviceInfo != null && audioDeviceInfo.isSink()) {
                    for (int i2 : audioDeviceInfo.getSampleRates()) {
                        if ((i2 == 48000 || i2 == 96000 || i2 == 192000) && i2 > i) {
                            i = i2;
                        }
                    }
                }
            }
            return i;
        } catch (Throwable unused) {
            return 48000;
        }
    }

    public void start(Context context) throws Exception {
        start(context, 0);
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x017d  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x01f2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void start(Context context, int i) throws Exception {
        Class<?>[] clsArr;
        int i2;
        int iIntValue;
        int iDetectDeviceSampleRate = i <= 0 ? detectDeviceSampleRate(context) : i;
        if (iDetectDeviceSampleRate != 48000 && iDetectDeviceSampleRate != 96000 && iDetectDeviceSampleRate != 192000) {
            iDetectDeviceSampleRate = 48000;
        }
        Class<?> cls = Class.forName("android.media.audiopolicy.AudioMixingRule");
        Class<?> cls2 = Class.forName("android.media.audiopolicy.AudioMixingRule$Builder");
        Class<?> cls3 = Class.forName("android.media.audiopolicy.AudioMix");
        Class<?> cls4 = Class.forName("android.media.audiopolicy.AudioMix$Builder");
        Class<?> cls5 = Class.forName("android.media.audiopolicy.AudioPolicy");
        Class<?> cls6 = Class.forName("android.media.audiopolicy.AudioPolicy$Builder");
        Object objNewInstance = cls2.getConstructor(null).newInstance(null);
        cls2.getMethod("setTargetMixRole", Integer.TYPE).invoke(objNewInstance, Integer.valueOf(cls.getField("MIX_ROLE_PLAYERS").getInt(null)));
        int i3 = cls.getField("RULE_MATCH_ATTRIBUTE_USAGE").getInt(null);
        Method method = cls2.getMethod("addMixRule", Integer.TYPE, Object.class);
        for (int i4 : CAPTURED_USAGES) {
            method.invoke(objNewInstance, Integer.valueOf(i3), new AudioAttributes.Builder().setUsage(i4).build());
        }
        try {
            cls2.getMethod("voiceCommunicationCaptureAllowed", Boolean.TYPE).invoke(objNewInstance, true);
        } catch (NoSuchMethodException unused) {
        }
        Object objNewInstance2 = cls4.getConstructor(cls).newInstance(cls2.getMethod("build", null).invoke(objNewInstance, null));
        objNewInstance2.getClass().getMethod("setFormat", AudioFormat.class).invoke(objNewInstance2, createAudioFormat(iDetectDeviceSampleRate));
        try {
            clsArr = null;
            try {
                i2 = cls3.getField(this.keepPlayingOnDevice ? "ROUTE_FLAG_LOOP_BACK_RENDER" : "ROUTE_FLAG_LOOP_BACK").getInt(null);
            } catch (NoSuchFieldException unused2) {
                i2 = cls3.getField("ROUTE_FLAG_LOOP_BACK").getInt(clsArr);
                objNewInstance2.getClass().getMethod("setRouteFlags", Integer.TYPE).invoke(objNewInstance2, Integer.valueOf(i2));
                Object objInvoke = cls4.getMethod("build", clsArr).invoke(objNewInstance2, clsArr);
                Object objNewInstance3 = cls6.getConstructor(Context.class).newInstance(context);
                cls6.getMethod("addMix", cls3).invoke(objNewInstance3, objInvoke);
                Object objInvoke2 = cls6.getMethod("build", clsArr).invoke(objNewInstance3, clsArr);
                Method declaredMethod = AudioManager.class.getDeclaredMethod("registerAudioPolicyStatic", cls5);
                declaredMethod.setAccessible(true);
                iIntValue = ((Integer) declaredMethod.invoke(clsArr, objInvoke2)).intValue();
                if (iIntValue == 0) {
                }
            }
        } catch (NoSuchFieldException unused3) {
            clsArr = null;
        }
        objNewInstance2.getClass().getMethod("setRouteFlags", Integer.TYPE).invoke(objNewInstance2, Integer.valueOf(i2));
        Object objInvoke3 = cls4.getMethod("build", clsArr).invoke(objNewInstance2, clsArr);
        Object objNewInstance32 = cls6.getConstructor(Context.class).newInstance(context);
        cls6.getMethod("addMix", cls3).invoke(objNewInstance32, objInvoke3);
        Object objInvoke22 = cls6.getMethod("build", clsArr).invoke(objNewInstance32, clsArr);
        Method declaredMethod2 = AudioManager.class.getDeclaredMethod("registerAudioPolicyStatic", cls5);
        declaredMethod2.setAccessible(true);
        iIntValue = ((Integer) declaredMethod2.invoke(clsArr, objInvoke22)).intValue();
        if (iIntValue == 0) {
            throw new RuntimeException("registerAudioPolicyStatic returned " + iIntValue);
        }
        AudioRecord audioRecord = (AudioRecord) cls5.getMethod("createAudioRecordSink", cls3).invoke(objInvoke22, objInvoke3);
        this.recorder = audioRecord;
        if (audioRecord.getState() != 1) {
            throw new RuntimeException("AudioRecord sink not initialized");
        }
        this.recorder.startRecording();
        int sampleRate = this.recorder.getSampleRate();
        this.sampleRate = sampleRate;
        if (sampleRate <= 0) {
            this.sampleRate = iDetectDeviceSampleRate;
        }
        try {
            this.recordBufferFrames = this.recorder.getBufferSizeInFrames();
            Util.log("Capture", "AudioRecord 缓冲 " + this.recordBufferFrames + " 帧（约 " + ((this.recordBufferFrames * 1000) / Math.max(1, this.sampleRate)) + "ms，框架决定，代码无法设置）");
        } catch (Throwable unused4) {
            this.recordBufferFrames = -1;
        }
    }

    private static AudioFormat createAudioFormat(int i) throws IllegalArgumentException {
        AudioFormat.Builder builder = new AudioFormat.Builder();
        builder.setEncoding(2);
        builder.setSampleRate(i);
        builder.setChannelMask(12);
        return builder.build();
    }

    public int read(byte[] bArr) {
        AudioRecord audioRecord = this.recorder;
        if (audioRecord == null) {
            return -1;
        }
        int i = audioRecord.read(bArr, 0, MAX_READ_BYTES);
        if (i > 0) {
            this.framesRead += i / 4;
            probeCaptureLatency();
        }
        return i;
    }

    public void stop() {
        AudioRecord audioRecord = this.recorder;
        if (audioRecord != null) {
            try {
                audioRecord.release();
            } catch (Throwable unused) {
            }
            this.recorder = null;
        }
    }
}
