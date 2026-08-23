package com.wifiaudio.test;

import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * MediaExtractor 标准模式：与 ExoPlayer 相同的喂帧方式。
 * 用 MediaExtractor 解析文件 → 完整 format（含所有 csd）→ MediaCodec.configure。
 * 如果这个模式能解码，说明之前手动构造 csd 的方式有差异。
 */
public final class MediaExtractorDecoder implements Decoder {
    private MediaExtractor extractor;
    private MediaCodec codec;
    private int trackIndex;
    private int rate;
    private int channels;
    private String info = "";
    private boolean eos = false;
    private boolean started = false;
    private long decodedFrames = 0;
    private int errCount = 0;
    private int consecutiveErrors = 0;
    private String lastError = "";

    @Override
    public boolean init(String assetName, StringBuilder msg) {
        try {
            extractor = new MediaExtractor();
            AssetFileDescriptor afd = MainActivity.getAppContext().getAssets().openFd(assetName);
            extractor.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            trackIndex = 0;
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat fmt = extractor.getTrackFormat(i);
                String mime = fmt.getString(MediaFormat.KEY_MIME);
                if (mime != null && (mime.startsWith("audio/"))) {
                    trackIndex = i;
                    break;
                }
            }
            MediaFormat fmt = extractor.getTrackFormat(trackIndex);
            extractor.selectTrack(trackIndex);

            // 打印关键参数 + csd 内容（对比手动构造的差异）
            StringBuilder keys = new StringBuilder();
            String[] csdKeys = {"csd-0", "csd-1", "csd-2", "csd-3"};
            for (String k : csdKeys) {
                if (fmt.containsKey(k)) {
                    java.nio.ByteBuffer csd = fmt.getByteBuffer(k);
                    csd.rewind();
                    StringBuilder hex = new StringBuilder();
                    for (int i = 0; i < Math.min(64, csd.remaining()); i++) {
                        hex.append(String.format("%02X", csd.get() & 0xFF));
                    }
                    keys.append(k).append("(").append(csd.limit()).append("B)=").append(hex).append(" ");
                }
            }
            info = "[MediaExtractor] mime=" + fmt.getString(MediaFormat.KEY_MIME)
                    + " rate=" + fmt.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                    + " ch=" + fmt.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                    + " csd: " + keys.toString().trim();

            rate = fmt.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            channels = fmt.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            if (channels == 0) channels = 2;

            codec = MediaCodec.createDecoderByType(fmt.getString(MediaFormat.KEY_MIME));
            codec.configure(fmt, null, null, 0);
            codec.start();
            started = true;
            return true;
        } catch (Throwable t) {
            msg.append("MediaExtractor 模式初始化失败: ").append(t);
            release();
            return false;
        }
    }

    @Override
    public byte[] nextFrame() {
        if (eos) return null;
        return new byte[1]; // 占位：decodeFrame 自己从 extractor 读
    }

    @Override
    public int decodeFrame(byte[] frame, int len, short[] out, int outCapacity) {
        if (codec == null || !started) return -1;
        try {
            // 喂输入（从 extractor 读 sample）
            int inIdx = codec.dequeueInputBuffer(100000);
            if (inIdx >= 0) {
                ByteBuffer ib = codec.getInputBuffer(inIdx);
                ib.clear(); // 关键：确保从 position 0 写入
                int size = extractor.readSampleData(ib, 0);
                if (size >= 0) {
                    codec.queueInputBuffer(inIdx, 0, size, extractor.getSampleTime(), 0);
                    extractor.advance();
                } else {
                    codec.queueInputBuffer(inIdx, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    eos = true;
                }
            }
            // 收集输出
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            int total = 0;
            while (true) {
                int outIdx = codec.dequeueOutputBuffer(info, 0);
                if (outIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // ignore
                } else if (outIdx >= 0) {
                    if (info.size > 0) {
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
                        decodedFrames++;
                    }
                    codec.releaseOutputBuffer(outIdx, false);
                } else {
                    break;
                }
            }
            if (total > 0) consecutiveErrors = 0;
            return total;
        } catch (Throwable t) {
            if (errCount < 3) {
                errCount++;
                lastError = t.toString();
            }
            consecutiveErrors++;
            if (consecutiveErrors >= 5) {
                return -100; // 连续错误，终止解码
            }
            return -1;
        }
    }

    @Override
    public String info() {
        return info + " out=" + decodedFrames + (errCount > 0 ? " err=" + errCount + " [" + lastError + "]" : "");
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
        if (extractor != null) {
            try {
                extractor.release();
            } catch (Throwable ignored) {
            }
            extractor = null;
        }
    }
}
