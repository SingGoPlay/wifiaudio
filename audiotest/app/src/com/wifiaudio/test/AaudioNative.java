package com.wifiaudio.test;

/**
 * AAudio 输出 JNI（NDK 原生低延迟音频路径）。
 */
public final class AaudioNative {
    static {
        try {
            System.loadLibrary("wifiaudio_aaudio");
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /** 打开 AAudio 播放流；失败返回 0 */
    public static native long open(int rate, int channels, int bufferMs);

    /** 写入 PCM（阻塞），返回实际写入帧数 */
    public static native int write(long handle, short[] pcm, int frames);

    /** 查询当前缓冲帧数（写入-已读），Java 端换算为 ms */
    public static native long getBufferedFrames(long handle);

    /** 关闭 */
    public static native void close(long handle);
}
