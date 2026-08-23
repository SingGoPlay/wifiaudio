package com.wifiaudio.receiver;

import com.wifiaudio.AacDecoder;
import com.wifiaudio.OpusDecoder;

/**
 * 软解引擎（libopus / fdk-aac，与 WiFiAudio 既有方案一致）。
 * 同步解码（读取线程直接调用）。
 */
public final class SoftDecoderEngine implements DecoderEngine {
    public static final int TYPE_OPUS = 0;
    public static final int TYPE_AAC = 1;

    private final int type;
    private final int rate;
    private final int channels;
    private final byte[] csd;
    private final AudioOut out;
    private long handle;
    private long outFrames = 0;
    private boolean failed = false;
    private final short[] pcmBuf = new short[4096 * 2];

    public SoftDecoderEngine(int type, int rate, int channels, byte[] csd, AudioOut out) {
        this.type = type;
        this.rate = rate;
        this.channels = channels;
        this.csd = csd;
        this.out = out;
    }

    @Override
    public boolean init() {
        try {
            if (type == TYPE_OPUS) {
                handle = OpusDecoder.init(rate, channels);
            } else {
                handle = AacDecoder.init(rate, channels, csd);
            }
            if (handle == 0) {
                failed = true;
                PlayerService.logStatic("软解初始化失败 (" + (type == TYPE_OPUS ? "libopus" : "fdk-aac") + ")");
                return false;
            }
            PlayerService.logStatic("软解就绪: " + (type == TYPE_OPUS ? "libopus" : "fdk-aac")
                    + " rate=" + rate + " ch=" + channels);
            return true;
        } catch (Throwable t) {
            failed = true;
            PlayerService.logStatic("软解初始化异常: " + t);
            return false;
        }
    }

    @Override
    public void feed(byte[] data, int off, int len) {
        if (handle == 0 || failed) return;
        // fdk-aac 接受完整 ADTS 帧（含头）；opus 是裸帧
        byte[] frame = (off == 0) ? data : java.util.Arrays.copyOfRange(data, off, off + len);
        int samples;
        if (type == TYPE_OPUS) {
            samples = OpusDecoder.decode(handle, frame, off == 0 ? len : frame.length, pcmBuf);
        } else {
            samples = AacDecoder.decode(handle, frame, off == 0 ? len : frame.length, pcmBuf);
        }
        if (samples > 0) {
            out.writePcm(pcmBuf, samples);
            outFrames += samples;
        } else if (samples < 0) {
            // 个别帧错误可忽略（fdk/libopus 容错）
        }
    }

    @Override
    public void eos() {
        // 无缓冲，无需处理
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public String name() {
        return "软解(" + (type == TYPE_OPUS ? "libopus" : "fdk-aac") + ")";
    }

    @Override
    public long outputFrames() {
        return outFrames;
    }

    @Override
    public void release() {
        if (handle != 0) {
            if (type == TYPE_OPUS) {
                OpusDecoder.destroy(handle);
            } else {
                AacDecoder.destroy(handle);
            }
            handle = 0;
        }
    }
}
