package com.wifiaudio.test;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * MediaCodec 硬件/系统解码 AAC。
 * 输入：ADTS 流（assets）→ 切帧 + AudioSpecificConfig(csd-0)。
 * 与 WiFiAudio 格式一致（ADTS + csd-0）。
 */
public final class MediaCodecAacDecoder implements Decoder {
    private MediaCodec codec;
    private int rate = 44100;
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

    public MediaCodecAacDecoder(String codecName) {
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
            frames = AdtsParser.parse(data);
            if (frames.isEmpty()) {
                msg.append("ADTS 解析失败: 无帧");
                return false;
            }
            byte[] csd0 = AdtsParser.buildCsd(frames.get(0));
            int r = AdtsParser.sampleRate(frames.get(0));
            if (r > 0) rate = r;

            MediaFormat fmt = MediaFormat.createAudioFormat("audio/mp4a-latm", rate, channels);
            fmt.setByteBuffer("csd-0", ByteBuffer.wrap(csd0));
            try {
                fmt.setInteger(MediaFormat.KEY_CHANNEL_MASK,
                        channels >= 2 ? android.media.AudioFormat.CHANNEL_OUT_STEREO : android.media.AudioFormat.CHANNEL_OUT_MONO);
            } catch (Throwable ignored) {
            }
            if (codecName != null && !codecName.isEmpty()) {
                codec = MediaCodec.createByCodecName(codecName);
            } else {
                codec = MediaCodec.createDecoderByType("audio/mp4a-latm");
            }
            codecName = codec.getCodecInfo().getName();
            codec.configure(fmt, null, null, 0);
            codec.start();
            info = "MediaCodec AAC (系统硬解/软解) rate=" + rate + " ch=" + channels
                    + " frames=" + frames.size();
            frameIndex = 0;
            return true;
        } catch (Throwable t) {
            msg.append("AAC 硬解初始化失败: ").append(t);
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
        if (frameIndex >= frames.size()) return null;
        return frames.get(frameIndex++);
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
