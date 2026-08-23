package com.wifiaudio;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

/**
 * OPUS 实时编码 —— 使用系统 MediaCodec OPUS 编码器（AOSP 自带软编，Android 10+ 普遍可用）。
 * 48kHz stereo PCM16 -> OPUS（20ms/帧 = 960 采样）。
 * 输出为裸 OPUS 帧（无容器），与 libopus 输出一致，协议不变。
 *
 * 注意：不再使用 musl 静态编译的 .so（在 Android bionic linker 下 dlopen 存在卡死风险）。
 */
public final class OpusEncoder implements Mixer.Sink {

    public static final int SAMPLE_RATE = 48000;
    public static final int CHANNELS = 2;
    public static final int FRAME_SAMPLES = 960;   // 20ms @48k

    public interface Listener {
        /** 收到一帧 OPUS 数据 */
        void onOpusFrame(byte[] frame, int len);
    }

    private final Listener listener;
    private MediaCodec codec;
    private ByteBuffer[] inBuffers;
    private ByteBuffer[] outBuffers;
    private final short[] pcmBuf = new short[FRAME_SAMPLES * CHANNELS];
    private int pcmFill = 0;
    private final byte[] outBuf = new byte[4096];
    private long frameCount = 0;
    private byte[] encoderCsd = null;  // 编码器原始 csd（可能 83B 含 OpusTags）
    private byte[] opusHead = buildOpusHead(SAMPLE_RATE, CHANNELS, 312); // 19B 标准头

    /**
     * 获取标准 19 字节 OpusHead（csd-0）。
     * 优先从编码器 csd 提取前 19 字节（含实际 pre-skip）；否则用默认。
     */
    public byte[] getOpusHead() {
        if (encoderCsd != null && encoderCsd.length >= 19
                && encoderCsd[0] == 'O' && encoderCsd[1] == 'p' && encoderCsd[2] == 'u' && encoderCsd[3] == 's'
                && encoderCsd[4] == 'H' && encoderCsd[5] == 'e' && encoderCsd[6] == 'a' && encoderCsd[7] == 'd') {
            byte[] h = new byte[19];
            System.arraycopy(encoderCsd, 0, h, 0, 19);
            int preSkip = (h[10] & 0xFF) | (h[11] << 8);
            Util.log("Opus", "OpusHead extracted from encoder csd (preSkip=" + preSkip + ")");
            return h;
        }
        return opusHead;
    }

    /** 构建 OpusHead（pre-skip 48kHz 标准值 312） */
    private static byte[] buildOpusHead(int sampleRate, int channels, int preSkip) {
        byte[] h = new byte[19];
        byte[] magic = "OpusHead".getBytes();
        System.arraycopy(magic, 0, h, 0, 8);
        h[8] = 1;  // version
        h[9] = (byte) channels;
        h[10] = (byte) (preSkip & 0xFF);
        h[11] = (byte) (preSkip >> 8);
        h[12] = (byte) (sampleRate & 0xFF);
        h[13] = (byte) ((sampleRate >> 8) & 0xFF);
        h[14] = (byte) ((sampleRate >> 16) & 0xFF);
        h[15] = (byte) ((sampleRate >> 24) & 0xFF);
        h[16] = 0; h[17] = 0; // output gain
        h[18] = 0;            // channel mapping family
        return h;
    }

    /** 检测系统是否支持 OPUS 编码器 */
    public static boolean systemSupportsOpusEncoder() {
        try {
            MediaCodecList list = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
            for (MediaCodecInfo info : list.getCodecInfos()) {
                if (!info.isEncoder()) continue;
                for (String t : info.getSupportedTypes()) {
                    if ("audio/opus".equalsIgnoreCase(t)) return true;
                }
            }
        } catch (Throwable t) {
            Util.log("Opus", "codec list check failed", t);
        }
        return false;
    }

    public OpusEncoder(int bitrateKbps, Listener listener) {
        this.listener = listener;
        // 预置默认 OpusHead（编码器输出 csd 后会替换）
        opusHead = buildOpusHead(SAMPLE_RATE, CHANNELS, 312);
        try {
            codec = MediaCodec.createEncoderByType("audio/opus");
            MediaFormat fmt = MediaFormat.createAudioFormat("audio/opus", SAMPLE_RATE, CHANNELS);
            fmt.setInteger(MediaFormat.KEY_BIT_RATE, bitrateKbps * 1000);
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
            Util.log("Opus", "encoder ready (system MediaCodec), bitrate=" + bitrateKbps + "kbps, frame=" + FRAME_SAMPLES + "samples");
        } catch (Throwable t) {
            Util.log("Opus", "init failed (system encoder unavailable?)", t);
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
        int samples = frames * CHANNELS;
        for (int i = 0; i < samples; i++) {
            pcmBuf[pcmFill++] = (short) ((pcm[i * 2] & 0xFF) | (pcm[i * 2 + 1] << 8));
            if (pcmFill == FRAME_SAMPLES * CHANNELS) {
                encodeFrame();
                pcmFill = 0;
            }
        }
    }

    private void encodeFrame() {
        try {
            int inIdx = codec.dequeueInputBuffer(20000);
            if (inIdx < 0) return;
            ByteBuffer in = inBuffers[inIdx];
            in.clear();
            for (int i = 0; i < FRAME_SAMPLES * CHANNELS; i++) {
                in.putShort(pcmBuf[i]);
            }
            codec.queueInputBuffer(inIdx, 0, FRAME_SAMPLES * CHANNELS * 2, System.nanoTime() / 1000, 0);
            drain();
        } catch (Throwable t) {
            Util.log("Opus", "encode error", t);
        }
    }

    private void drain() {
        try {
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            int outIdx;
            while ((outIdx = codec.dequeueOutputBuffer(info, 0)) >= 0) {
                if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0 && info.size > 0) {
                    // 编码器输出的 csd（可能是 OpusHead + OpusTags，保留原始用于提取）
                    ByteBuffer out = outBuffers[outIdx];
                    encoderCsd = new byte[info.size];
                    out.position(info.offset);
                    out.get(encoderCsd, 0, info.size);
                    Util.log("Opus", "got csd from encoder (" + encoderCsd.length + "B)");
                } else if (info.size > 0) {
                    ByteBuffer out = outBuffers[outIdx];
                    out.position(info.offset);
                    out.limit(info.offset + info.size);
                    int len = Math.min(info.size, outBuf.length);
                    out.get(outBuf, 0, len);
                    listener.onOpusFrame(outBuf, len);
                    frameCount++;
                    if (frameCount % 100 == 0) {
                        Util.log("Opus", "encoded " + frameCount + " frames, last=" + len + "B");
                    }
                }
                codec.releaseOutputBuffer(outIdx, false);
            }
            // 确保 OpusHead 就绪（编码器未输出 csd 时手动构建）
            if (opusHead == null) {
                opusHead = buildOpusHead(SAMPLE_RATE, CHANNELS, 312);
            }
        } catch (Throwable t) {
            Util.log("Opus", "drain error", t);
        }
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
