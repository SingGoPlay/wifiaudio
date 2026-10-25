package com.wifiaudio;

import android.content.Context;
import android.media.AudioRecord;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/* loaded from: classes.dex */
public final class CallCapture {
    private final Context context;
    private final Mixer mixer;
    private volatile AudioRecord recorder;
    private volatile Thread thread;
    private volatile boolean supported = false;
    private volatile boolean inCall = false;
    private volatile boolean running = false;
    private int callSampleRate = 16000;
    private int callChannels = 1;
    private final PhoneStateListener callStateListener = new PhoneStateListener() { // from class: com.wifiaudio.CallCapture.2
        @Override // android.telephony.PhoneStateListener
        public void onCallStateChanged(int i, String str) {
            boolean z = i == 2;
            if (z == CallCapture.this.inCall) {
                return;
            }
            CallCapture.this.inCall = z;
            Status.callActive = CallCapture.this.inCall;
            if (CallCapture.this.inCall) {
                Util.log("Call", "Call started, opening VOICE_CALL recorder");
                CallCapture.this.ensureRecorder();
            } else {
                Util.log("Call", "Call ended");
                Status.callCaptureActive = false;
                CallCapture.this.releaseRecorder();
            }
        }
    };

    public CallCapture(Context context, Mixer mixer) {
        this.context = context;
        this.mixer = mixer;
    }

    public boolean probe() {
        AudioRecord audioRecordTryCreate = tryCreate();
        if (audioRecordTryCreate == null) {
            Util.log("Call", "VOICE_CALL capture NOT supported on this device (HAL limitation)");
            this.supported = false;
            return false;
        }
        audioRecordTryCreate.release();
        this.supported = true;
        Util.log("Call", "VOICE_CALL capture supported, sampleRate=" + this.callSampleRate + " ch=" + this.callChannels + " (Android " + Build.VERSION.SDK_INT + ")");
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x0057, code lost:
    
        r3 = r3 + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private AudioRecord tryCreate() {
        int[] iArr = {48000, 44100, 32000, 16000, 8000};
        int i = 0;
        while (i < 5) {
            int i2 = iArr[i];
            int[] iArr2 = {16, 12};
            int i3 = 0;
            while (true) {
                int i4 = 2;
                if (i3 < 2) {
                    int i5 = iArr2[i3];
                    try {
                        int minBufferSize = AudioRecord.getMinBufferSize(i2, i5, 2);
                        if (minBufferSize > 0) {
                            AudioRecord audioRecord = new AudioRecord(4, i2, i5, 2, Math.max(minBufferSize * 2, 8192));
                            if (audioRecord.getState() == 1) {
                                this.callSampleRate = i2;
                                if (i5 != 12) {
                                    i4 = 1;
                                }
                                this.callChannels = i4;
                                return audioRecord;
                            }
                            audioRecord.release();
                        }
                    } catch (Throwable unused) {
                    }
                    i3++;
                }
            }
        }
        return null;
    }

    public void start() {
        if (!this.supported || this.running) {
            return;
        }
        this.running = true;
        this.thread = new Thread(new Runnable() { // from class: com.wifiaudio.CallCapture.1
            @Override // java.lang.Runnable
            public void run() throws InterruptedException {
                CallCapture.this.loop();
            }
        }, "call-capture");
        this.thread.start();
    }

    public void stop() {
        this.running = false;
        if (this.recorder != null) {
            try {
                this.recorder.release();
            } catch (Throwable unused) {
            }
            this.recorder = null;
        }
        if (this.thread != null) {
            try {
                this.thread.interrupt();
            } catch (Throwable unused2) {
            }
            this.thread = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loop() throws InterruptedException {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService("phone");
            if (telephonyManager == null) {
                Util.log("Call", "No telephony service, call capture disabled");
                return;
            }
            telephonyManager.listen(this.callStateListener, 32);
            int i = this.callChannels * AudioLoopbackCapture.MAX_READ_BYTES;
            byte[] bArr = new byte[i];
            while (this.running) {
                AudioRecord audioRecord = this.recorder;
                if (audioRecord != null && this.inCall) {
                    int i2 = audioRecord.read(bArr, 0, i);
                    if (i2 > 0) {
                        int i3 = this.callChannels;
                        this.mixer.mixCallPcm(bArr, i2 / (i3 * 2), i3, this.callSampleRate);
                        Status.callCaptureActive = true;
                    } else if (i2 < 0) {
                        Util.log("Call", "read error " + i2 + ", recreating recorder");
                        releaseRecorder();
                    }
                } else {
                    try {
                        Thread.sleep(50L);
                    } catch (InterruptedException unused) {
                        return;
                    }
                }
            }
        } catch (Throwable th) {
            Util.log("Call", "通话状态监听不可用（此设备可能无法自动触发通话捕获）: " + Util.causeMsg(th));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void ensureRecorder() {
        if (this.recorder != null) {
            return;
        }
        AudioRecord audioRecordTryCreate = tryCreate();
        if (audioRecordTryCreate == null) {
            Util.log("Call", "Cannot open VOICE_CALL recorder in call");
            return;
        }
        try {
            audioRecordTryCreate.startRecording();
            this.recorder = audioRecordTryCreate;
            Util.log("Call", "VOICE_CALL recorder started: " + this.callSampleRate + "Hz ch=" + this.callChannels);
        } catch (Throwable th) {
            Util.log("Call", "startRecording failed", th);
            audioRecordTryCreate.release();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void releaseRecorder() {
        if (this.recorder != null) {
            try {
                this.recorder.stop();
                this.recorder.release();
            } catch (Throwable unused) {
            }
            this.recorder = null;
        }
    }
}
