package com.wifiaudio;

/**
 * AAudio 低延迟输出 JNI（NDK）。
 */
public final class AaudioNative {
    static {
        try {
            System.loadLibrary("wifiaudio_aaudio");
        } catch (Throwable t) {
            android.util.Log.e("Aaudio", "loadLibrary failed: " + t);
        }
    }

    /** 打开播放流；失败返回 0 */
    public static native long open(int rate, int channels, int bufferMs);

    /** 写入 PCM（阻塞），返回写入帧数 */
    public static native int write(long handle, short[] pcm, int frames);

    /** 当前缓冲帧数 */
    public static native long getBufferedFrames(long handle);

    /** 关闭 */
    public static native void close(long handle);
}
