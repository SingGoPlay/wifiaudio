package com.wifiaudio;

/**
 * fdk-aac 软解 JNI 封装（bionic 兼容，静态包含 fdk-aac）。
 * 绕开部分 ROM 损坏的 MediaCodec AAC 解码器。
 */
public final class AacDecoder {

    static {
        try {
            System.loadLibrary("wifiaudio_aac_decode");
        } catch (Throwable t) {
            android.util.Log.e("AacSoft", "loadLibrary failed: " + t);
            throw new RuntimeException(t);
        }
    }

    /** 初始化解码器（需 AudioSpecificConfig）；失败返回 0 */
    public static native long init(int sampleRate, int channels, byte[] asc);

    /** 解码一帧 ADTS，返回每声道采样数；失败返回负值 */
    public static native int decode(long handle, byte[] adts, int len, short[] out);

    /** 销毁 */
    public static native void destroy(long handle);
}
