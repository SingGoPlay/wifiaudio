package com.wifiaudio.test;

import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * MediaCodec 异步模式（与 ExoPlayer Media3 1.9 的 AsynchronousMediaCodecAdapter 相同）。
 * 用 setCallback + 回调线程喂输入/收输出，绕开同步模式在部分 ROM 的 Released 问题。
 */
public final class MediaCodecAsyncDecoder implements Decoder {
    private MediaCodec codec;
    private MediaExtractor extractor;
    private int rate;
    private int channels;
    private String info = "";
    private volatile boolean eosInput = false;
    private volatile boolean eosOutput = false;
    private final LinkedBlockingQueue<short[]> outQueue = new LinkedBlockingQueue<short[]>(256);
    private int errCount = 0;
    private String lastError = "";
    private long decodedFrames = 0;
    private volatile boolean started = false;

    private final MediaCodec.Callback callback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(MediaCodec codec, int index) {
            if (eosInput) return;
            try {
                ByteBuffer ib = codec.getInputBuffer(index);
                ib.clear();
                int size = extractor.readSampleData(ib, 0);
                if (size >= 0) {
                    codec.queueInputBuffer(index, 0, size, extractor.getSampleTime(), 0);
                    extractor.advance();
                } else {
                    codec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    eosInput = true;
                }
            } catch (Throwable t) {
                eosInput = true;
                lastError = "input: " + t;
            }
        }

        @Override
        public void onOutputBufferAvailable(MediaCodec codec, int index, MediaCodec.BufferInfo info) {
            try {
                if (info.size > 0) {
                    ByteBuffer ob = codec.getOutputBuffer(index);
                    ob.position(info.offset);
                    ob.limit(info.offset + info.size);
                    int samples = info.size / (channels * 2);
                    short[] pcm = new short[samples * channels];
                    for (int i = 0; i < pcm.length; i++) pcm[i] = ob.getShort();
                    outQueue.offer(pcm);
                    decodedFrames++;
                }
                codec.releaseOutputBuffer(index, false);
                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    eosOutput = true;
                }
            } catch (Throwable t) {
                lastError = "output: " + t;
                try {
                    codec.releaseOutputBuffer(index, false);
                } catch (Throwable ignored) {
                }
            }
        }

        @Override
        public void onError(MediaCodec codec, MediaCodec.CodecException e) {
            lastError = "onError: " + e;
            eosOutput = true;
        }

        @Override
        public void onOutputFormatChanged(MediaCodec codec, MediaFormat format) {
            // ignore
        }
    };

    @Override
    public boolean init(String assetName, StringBuilder msg) {
        try {
            extractor = new MediaExtractor();
            AssetFileDescriptor afd = MainActivity.getAppContext().getAssets().openFd(assetName);
            extractor.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            MediaFormat fmt = extractor.getTrackFormat(0);
            extractor.selectTrack(0);
            rate = fmt.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            channels = fmt.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            if (channels == 0) channels = 2;

            String mime = fmt.getString(MediaFormat.KEY_MIME);
            codec = MediaCodec.createDecoderByType(mime);
            // 异步模式：必须 start 前 setCallback
            codec.setCallback(callback);
            codec.configure(fmt, null, null, 0);
            codec.start();
            started = true;
            info = "[MediaCodec异步] " + mime + " rate=" + rate + " ch=" + channels;
            return true;
        } catch (Throwable t) {
            msg.append("异步模式初始化失败: ").append(t);
            release();
            return false;
        }
    }

    @Override
    public byte[] nextFrame() {
        if (eosOutput && outQueue.isEmpty()) return null;
        return new byte[1]; // 占位
    }

    @Override
    public int decodeFrame(byte[] frame, int len, short[] out, int outCapacity) {
        if (codec == null || !started) return -1;
        short[] pcm = outQueue.poll();
        if (pcm != null) {
            int samples = pcm.length / channels;
            if (samples <= outCapacity) {
                System.arraycopy(pcm, 0, out, 0, pcm.length);
                return samples;
            }
            return 0;
        }
        if (eosOutput) {
            return -100; // 结束
        }
        return 0;
    }

    @Override
    public String info() {
        return info + " out=" + decodedFrames + (lastError.isEmpty() ? "" : " [" + lastError + "]");
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
        outQueue.clear();
    }
}
