package com.wifiaudio.test;

import com.wifiaudio.AacDecoder;
import com.wifiaudio.OpusDecoder;

import java.io.InputStream;
import java.util.List;

/**
 * 软解对比（libopus / fdk-aac，复用 WiFiAudio 的 JNI 库）。
 * 与硬解使用相同的帧输入，用于对比"硬解是否损坏"。
 */
public final class SoftDecoder implements Decoder {
    public static final int TYPE_OPUS = 0;
    public static final int TYPE_AAC = 1;

    private final int type;
    private long handle;
    private int rate;
    private int channels;
    private List<byte[]> frames;
    private int frameIndex;
    private String info = "";
    private long decodedFrames = 0;

    public SoftDecoder(int type) {
        this.type = type;
    }

    @Override
    public boolean init(String assetName, StringBuilder msg) {
        try {
            InputStream is = MainActivity.getAppContext().getAssets().open(assetName);
            byte[] data = new byte[is.available()];
            int off = 0;
            while (off < data.length) {
                int n = is.read(data, off, data.length - off);
                if (n < 0) break;
                off += n;
            }
            is.close();
            if (type == TYPE_OPUS) {
                frames = OggParser.parse(data);
                if (frames.size() < 2) {
                    msg.append("Ogg 解析失败");
                    return false;
                }
                byte[] head = frames.get(0);
                channels = (head.length >= 10 && head[9] != 0) ? (head[9] & 0xFF) : 2;
                rate = 48000;
                handle = OpusDecoder.init(rate, channels);
                if (handle == 0) {
                    msg.append("libopus 初始化失败");
                    return false;
                }
                frameIndex = 1;
                info = "libopus 软解 rate=" + rate + " ch=" + channels + " frames=" + (frames.size() - 1);
                return true;
            } else {
                frames = AdtsParser.parse(data);
                if (frames.isEmpty()) {
                    msg.append("ADTS 解析失败");
                    return false;
                }
                rate = AdtsParser.sampleRate(frames.get(0));
                channels = 2;
                byte[] csd0 = AdtsParser.buildCsd(frames.get(0));
                handle = AacDecoder.init(rate, channels, csd0);
                if (handle == 0) {
                    msg.append("fdk-aac 初始化失败");
                    return false;
                }
                frameIndex = 0;
                info = "fdk-aac 软解 rate=" + rate + " ch=" + channels + " frames=" + frames.size();
                return true;
            }
        } catch (Throwable t) {
            msg.append("软解初始化失败: ").append(t);
            release();
            return false;
        }
    }

    @Override
    public int decodeFrame(byte[] frame, int len, short[] out, int outCapacity) {
        if (handle == 0) return -1;
        int samples;
        if (type == TYPE_OPUS) {
            samples = OpusDecoder.decode(handle, frame, len, out);
        } else {
            samples = AacDecoder.decode(handle, frame, len, out);
        }
        if (samples > 0) decodedFrames++;
        return samples;
    }

    @Override
    public byte[] nextFrame() {
        while (frameIndex < frames.size()) {
            byte[] f = frames.get(frameIndex++);
            // 跳过 Ogg 中的 OpusTags 元数据帧（不是音频）
            if (type == TYPE_OPUS && f.length >= 8 && f[0] == 'O' && f[1] == 'p' && f[2] == 'u' && f[3] == 's'
                    && f[4] == 'T' && f[5] == 'a' && f[6] == 'g' && f[7] == 's') {
                continue;
            }
            return f;
        }
        return null;
    }

    @Override
    public String info() {
        return info + " decoded=" + decodedFrames;
    }

    @Override
    public int rate() {
        return rate;
    }

    @Override
    public int channels() {
        return channels;
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
