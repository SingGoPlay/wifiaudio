package com.wifiaudio;

/**
 * libopus 软解 JNI 封装（bionic 兼容，静态包含 libopus）。
 * 绕开部分 ROM 损坏的 MediaCodec OPUS 解码器。
 */
public final class OpusDecoder {

    static {
        try {
            System.loadLibrary("wifiaudio_opus_decode");
        } catch (Throwable t) {
            android.util.Log.e("OpusSoft", "loadLibrary failed: " + t);
            throw new RuntimeException(t);
        }
    }

    /** 初始化解码器（48kHz/立体声），返回 handle；失败返回 0 */
    public static native long init(int sampleRate, int channels);

    /** 解码一帧，返回每声道采样数；失败返回负值 */
    public static native int decode(long handle, byte[] in, int inLen, short[] out);

    /** 解码（带 FEC 丢包隐藏） */
    public static native int decodeFec(long handle, byte[] in, int inLen, short[] out);

    /** 销毁 */
    public static native void destroy(long handle);
}
