package com.wifiaudio;

import android.util.Log;

/* loaded from: classes2.dex */
public final class AaudioNative {
    public static native void close(long j);

    public static native long getBufferedFrames(long j);

    public static native long open(int i, int i2, int i3);

    public static native int write(long j, short[] sArr, int i);

    static {
        try {
            System.loadLibrary("wifiaudio_aaudio");
        } catch (Throwable th) {
            Log.e("Aaudio", "loadLibrary failed: " + th);
        }
    }
}
