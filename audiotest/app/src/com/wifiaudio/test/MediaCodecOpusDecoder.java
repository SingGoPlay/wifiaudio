package com.wifiaudio.test;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * MediaCodec 硬件/系统解码 OPUS。
 * 输入：Ogg Opus 文件（assets）→ 解析出 OpusHead(csd) + 裸 opus 帧。
 * 解码路径与 WiFiAudio 一致（裸帧 + csd-0）。
 */
public final class MediaCodecOpusDecoder implements Decoder {
    private MediaCodec codec;
    private int rate = 48000;
    private int channels = 2;
    private List<byte[]> frames;
    private int frameIndex;
    private String info = "";
    private int decodeErrCount = 0;
    private long decodedFrames = 0;
    private long submittedFrames = 0;
    private boolean formatChanged = false;
    private String codecName;
    private int dequeueTimeoutCount = 0;
    private String lastDequeueResult = "";

    public MediaCodecOpusDecoder(String codecName) {
        this.codecName = codecName;
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
            frames = OggParser.parse(data);
            if (frames.size() < 2) {
                msg.append("Ogg 解析失败: 帧太少 (").append(frames.size()).append(")");
                return false;
            }
            byte[] head = frames.get(0);
            // OpusHead: magic(8) version(1) channels(1) preSkip(2) inputRate(4)
            if (head.length >= 12 && head[0] == 'O' && head[1] == 'p') {
                channels = head[9] & 0xFF;
                if (channels == 0) channels = 2;
                int inputRate = (head[12] & 0xFF) | ((head[13] & 0xFF) << 8)
                        | ((head[14] & 0xFF) << 16) | ((head[15] & 0xFF) << 24);
                if (inputRate > 0) rate = inputRate;
            }

            MediaFormat fmt = MediaFormat.createAudioFormat("audio/opus", rate, channels);
            fmt.setByteBuffer("csd-0", ByteBuffer.wrap(head));
            try {
                fmt.setInteger(MediaFormat.KEY_CHANNEL_MASK,
                        channels >= 2 ? android.media.AudioFormat.CHANNEL_OUT_STEREO : android.media.AudioFormat.CHANNEL_OUT_MONO);
            } catch (Throwable ignored) {
            }
            if (codecName != null && !codecName.isEmpty()) {
                codec = MediaCodec.createByCodecName(codecName);
            } else {
                codec = MediaCodec.createDecoderByType("audio/opus");
            }
            codecName = codec.getCodecInfo().getName();
            codec.configure(fmt, null, null, 0);
            codec.start();
            info = "MediaCodec OPUS (系统硬解/软解) rate=" + rate + " ch=" + channels
                    + " frames=" + (frames.size() - 1);
            frameIndex = 1; // 跳过 OpusHead
            return true;
        } catch (Throwable t) {
            msg.append("OPUS 硬解初始化失败: ").append(t);
            release();
            return false;
        }
    }

    @Override
    public int decodeFrame(byte[] frame, int len, short[] out, int outCapacity) {
        if (codec == null) return -1;
        try {
            int inIdx = codec.dequeueInputBuffer(100000);
            if (inIdx < 0) {
                dequeueTimeoutCount++;
                if (lastDequeueResult.isEmpty()) {
                    if (inIdx == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        lastDequeueResult = "TRY_AGAIN(无空闲输入缓冲)";
                    } else {
                        lastDequeueResult = "返回码=" + inIdx;
                    }
                }
                return 0;
            }
            ByteBuffer ib = codec.getInputBuffer(inIdx);
            ib.clear();
            ib.put(frame, 0, len);
            codec.queueInputBuffer(inIdx, 0, len, System.nanoTime() / 1000, 0);
            submittedFrames++;
            // 收集输出
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            int total = 0;
            while (true) {
                int outIdx = codec.dequeueOutputBuffer(info, 0);
                if (outIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    formatChanged = true;
                } else if (outIdx >= 0) {
                    ByteBuffer ob = codec.getOutputBuffer(outIdx);
                    ob.position(info.offset);
                    ob.limit(info.offset + info.size);
                    int samples = info.size / (channels * 2);
                    if (total + samples <= outCapacity) {
                        for (int i = 0; i < samples * channels; i++) {
                            out[total * channels + i] = ob.getShort();
                        }
                        total += samples;
                    }
                    codec.releaseOutputBuffer(outIdx, false);
                    decodedFrames++;
                } else {
                    break;
                }
            }
            return total;
        } catch (Throwable t) {
            if (decodeErrCount < 5) {
                decodeErrCount++;
                throw new RuntimeException("硬解错误: " + t);
            }
            return -1;
        }
    }

    @Override
    public byte[] nextFrame() {
        while (frameIndex < frames.size()) {
            byte[] f = frames.get(frameIndex++);
            if (f.length >= 8 && f[0] == 'O' && f[1] == 'p' && f[2] == 'u' && f[3] == 's'
                    && f[4] == 'T' && f[5] == 'a' && f[6] == 'g' && f[7] == 's') {
                continue; // 跳过 OpusTags 元数据
            }
            return f;
        }
        return null;
    }

    @Override
    public String info() {
        String warn = (decodedFrames == 0 && submittedFrames > 50)
                ? " \u26a0 解码器无输出！硬件解码可能异常" : "";
        return "[" + (codecName == null || codecName.isEmpty() ? "默认" : codecName) + "]" + info
                + " formatChanged=" + formatChanged + " submitted=" + submittedFrames + " out=" + decodedFrames
                + (dequeueTimeoutCount > 0 ? " ⚠ 输入缓冲超时" + dequeueTimeoutCount + "次[" + lastDequeueResult + "]" : "") + warn;
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
