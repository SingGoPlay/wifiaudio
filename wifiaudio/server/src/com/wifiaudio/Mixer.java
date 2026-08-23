package com.wifiaudio;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 音频混合与分发：
 *  - 主输入：系统回环捕获 (48kHz stereo PCM16)
 *  - 副输入：蜂窝通话 VOICE_CALL (任意采样率 mono/stereo，线性重采样到 48k stereo)
 *  - 输出：48kHz stereo PCM16，分发给所有接收端（TCP / HTTP / 编码器）
 */
public final class Mixer {

    public static final int OUT_RATE = 48000;
    public static final int OUT_CHANNELS = 2;

    private final List<Sink> sinks = new CopyOnWriteArrayList<Sink>();

    public interface Sink {
        /** 收到一帧 48k stereo PCM16 数据（字节数 = frames*4） */
        void onPcm(byte[] pcm, int frames);
    }

    public void addSink(Sink s) {
        sinks.add(s);
    }

    public void removeSink(Sink s) {
        sinks.remove(s);
    }

    /** 回环 PCM（48k stereo）直接分发 */
    public void mixLoopback(byte[] pcm, int bytes) {
        int frames = bytes / 4;
        if (frames <= 0) return;
        for (Sink s : sinks) {
            try {
                s.onPcm(pcm, frames);
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * 通话 PCM 混入。源采样率/声道任意，线性重采样到 48k stereo 后与主路叠加。
     * 注意：此方法直接改写 pcm 缓冲（在主路数据上叠加），仅用于在 loopback 帧间插入通话帧。
     * 简化实现：通话帧独立成帧分发前先重采样成 48k stereo 的副本，由 Sink 侧自行叠加或替换。
     * 由于通话与媒体极少同时发生（通话时媒体通常暂停），此处策略：通话期间通话帧直接作为一帧输出，
     * 媒体恢复时自然切换，避免复杂混音。
     */
    public void mixCallPcm(byte[] pcm, int frames, int channels, int sampleRate) {
        if (sampleRate == OUT_RATE && channels == OUT_CHANNELS) {
            for (Sink s : sinks) {
                try {
                    s.onPcm(pcm, frames);
                } catch (Throwable ignored) {
                }
            }
            return;
        }
        // 重采样到 48k stereo
        int outFrames = (int) Math.ceil((long) frames * OUT_RATE / sampleRate);
        byte[] out = new byte[outFrames * OUT_CHANNELS * 2];
        if (channels == 1) {
            resampleMonoToStereo(pcm, frames, sampleRate, out, outFrames);
        } else {
            resampleStereo(pcm, frames, sampleRate, out, outFrames);
        }
        for (Sink s : sinks) {
            try {
                s.onPcm(out, outFrames);
            } catch (Throwable ignored) {
            }
        }
    }

    /** mono -> stereo 线性重采样 */
    private static void resampleMonoToStereo(byte[] in, int inFrames, int inRate, byte[] out, int outFrames) {
        short[] tmp = new short[inFrames];
        for (int i = 0; i < inFrames; i++) {
            tmp[i] = (short) ((in[i * 2] & 0xFF) | (in[i * 2 + 1] << 8));
        }
        int outIdx = 0;
        for (int o = 0; o < outFrames; o++) {
            double pos = (double) o * inFrames / outFrames;
            int i0 = (int) pos;
            int i1 = Math.min(i0 + 1, inFrames - 1);
            double frac = pos - i0;
            short v = (short) (tmp[i0] * (1 - frac) + tmp[i1] * frac);
            out[outIdx++] = (byte) (v & 0xFF);
            out[outIdx++] = (byte) (v >> 8);
            out[outIdx++] = (byte) (v & 0xFF);
            out[outIdx++] = (byte) (v >> 8);
        }
    }

    /** stereo -> stereo 线性重采样 */
    private static void resampleStereo(byte[] in, int inFrames, int inRate, byte[] out, int outFrames) {
        short[] tmpL = new short[inFrames];
        short[] tmpR = new short[inFrames];
        for (int i = 0; i < inFrames; i++) {
            int base = i * 4;
            tmpL[i] = (short) ((in[base] & 0xFF) | (in[base + 1] << 8));
            tmpR[i] = (short) ((in[base + 2] & 0xFF) | (in[base + 3] << 8));
        }
        int outIdx = 0;
        for (int o = 0; o < outFrames; o++) {
            double pos = (double) o * inFrames / outFrames;
            int i0 = (int) pos;
            int i1 = Math.min(i0 + 1, inFrames - 1);
            double frac = pos - i0;
            short l = (short) (tmpL[i0] * (1 - frac) + tmpL[i1] * frac);
            short r = (short) (tmpR[i0] * (1 - frac) + tmpR[i1] * frac);
            out[outIdx++] = (byte) (l & 0xFF);
            out[outIdx++] = (byte) (l >> 8);
            out[outIdx++] = (byte) (r & 0xFF);
            out[outIdx++] = (byte) (r >> 8);
        }
    }
}
