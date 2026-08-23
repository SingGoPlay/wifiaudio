package com.wifiaudio;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * AAC-LC 实时编码（MediaCodec 硬编码），输出 ADTS 流。
 * 48kHz stereo PCM16 -> AAC-LC。
 */
public final class AacEncoder implements Mixer.Sink {

    public static final int SAMPLE_RATE = 48000;
    public static final int CHANNELS = 2;
    public static final int FRAME_SAMPLES = 1024; // AAC 每帧 1024 个采样

    public interface Listener {
        /** 收到一帧 ADTS AAC 数据 */
        void onAacFrame(byte[] adts, int len);
    }

    private final Listener listener;
    private MediaCodec codec;
    private ByteBuffer[] inBuffers;
    private ByteBuffer[] outBuffers;
    private final LinkedList<byte[]> pending = new LinkedList<byte[]>();
    private final byte[] pcmBuf = new byte[FRAME_SAMPLES * CHANNELS * 2];
    private int pcmFill = 0;
    private int sampleRateIndex = 3;  // 48000
    private int channelConfig = 2;    // stereo
    private long frameCount = 0;

    public AacEncoder(int bitrateKbps, Listener listener) {
        this.listener = listener;
        try {
            codec = MediaCodec.createEncoderByType("audio/mp4a-latm");
            MediaFormat fmt = MediaFormat.createAudioFormat("audio/mp4a-latm", SAMPLE_RATE, CHANNELS);
            fmt.setInteger(MediaFormat.KEY_BIT_RATE, bitrateKbps * 1000);
            fmt.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            fmt.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, FRAME_SAMPLES * CHANNELS * 2);
            // 低延迟模式（API 30+，尽量减小编码器缓冲）
            if (android.os.Build.VERSION.SDK_INT >= 30) {
                try {
                    fmt.setInteger(MediaFormat.KEY_LOW_LATENCY, 1);
                } catch (Throwable ignored) {
                }
            }
            codec.configure(fmt, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            codec.start();
            inBuffers = codec.getInputBuffers();
            outBuffers = codec.getOutputBuffers();
            // 尝试读取 csd-0 确定采样率索引与声道配置（失败则用默认 48000/stereo）
            try {
                ByteBuffer csd = codec.getOutputFormat().getByteBuffer("csd-0");
                if (csd != null && csd.remaining() >= 2) {
                    int b0 = csd.get() & 0xFF;
                    int b1 = csd.get() & 0xFF;
                    sampleRateIndex = ((b0 << 1) | (b1 >> 7)) & 0x0F;
                    channelConfig = (b1 >> 3) & 0x0F;
                }
            } catch (Throwable ignored) {
            }
        } catch (Throwable t) {
            Util.log("Aac", "init failed", t);
            codec = null;
            inBuffers = null;
            outBuffers = null;
        }
    }

    public boolean ok() {
        return codec != null;
    }

    @Override
    public void onPcm(byte[] pcm, int frames) {
        if (codec == null) return;
        int bytes = frames * CHANNELS * 2;
        int offset = 0;
        while (offset < bytes) {
            int n = Math.min(bytes - offset, pcmBuf.length - pcmFill);
            System.arraycopy(pcm, offset, pcmBuf, pcmFill, n);
            offset += n;
            pcmFill += n;
            if (pcmFill == pcmBuf.length) {
                feed(pcmBuf, pcmFill);
                pcmFill = 0;
            }
        }
    }

    private void feed(byte[] pcm, int len) {
        try {
            int inIdx = codec.dequeueInputBuffer(20000);
            if (inIdx < 0) return;
            ByteBuffer in = inBuffers[inIdx];
            in.clear();
            in.put(pcm, 0, len);
            codec.queueInputBuffer(inIdx, 0, len, System.nanoTime() / 1000, 0);

            drain();
        } catch (Throwable t) {
            Util.log("Aac", "feed error", t);
        }
    }

    private void drain() {
        try {
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            int outIdx;
            while ((outIdx = codec.dequeueOutputBuffer(info, 0)) >= 0) {
                ByteBuffer out = outBuffers[outIdx];
                int size = info.size;
                if (size > 0 && (info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == 0) {
                    byte[] frame = new byte[size + 7];
                    addAdtsHeader(frame, size);
                    out.position(info.offset);
                    out.limit(info.offset + size);
                    out.get(frame, 7, size);
                    listener.onAacFrame(frame, frame.length);
                    frameCount++;
                    if (frameCount % 100 == 0) {
                        Util.log("Aac", "encoded " + frameCount + " frames, last=" + frame.length + "B");
                    }
                }
                codec.releaseOutputBuffer(outIdx, false);
            }
        } catch (Throwable t) {
            Util.log("Aac", "drain error", t);
        }
    }

    /** 生成 ADTS 头（MPEG-4 AAC-LC, 无 CRC） */
    private void addAdtsHeader(byte[] out, int frameLen) {
        int fullLen = frameLen + 7;
        out[0] = (byte) 0xFF;
        out[1] = (byte) 0xF1; // MPEG-4, layer 0, no CRC
        // profile(2bit)=1(AAC-LC) | sampleRateIndex(4bit) | channelConfig高位(2bit)
        out[2] = (byte) ((1 << 6) | ((sampleRateIndex & 0x0F) << 2) | ((channelConfig & 0x07) >> 2));
        out[3] = (byte) (((channelConfig & 0x07) << 6) | ((fullLen >> 11) & 0x03));
        out[4] = (byte) ((fullLen >> 3) & 0xFF);
        out[5] = (byte) (((fullLen & 0x07) << 5) | 0x1F);
        out[6] = (byte) 0xFC;
    }

    public void stop() {
        if (codec != null) {
            try {
                codec.stop();
                codec.release();
            } catch (Throwable ignored) {
            }
            codec = null;
        }
    }
}
