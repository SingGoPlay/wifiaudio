package com.wifiaudio.receiver;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 异步 MediaCodec 硬解（ExoPlayer Media3 1.9 同款模式）。
 * 绕开部分 ROM（ColorOS 等）同步模式 dequeue 触发 Released 的兼容性 bug。
 */
public final class AsyncHardDecoder implements DecoderEngine {
    private final String mime;
    private final int rate;
    private final int channels;
    private final byte[] csd;
    private final AudioOut out;
    private final String codecName; // null=默认
    private MediaCodec codec;
    private final LinkedBlockingQueue<byte[]> inputQueue = new LinkedBlockingQueue<byte[]>(512);
    private volatile boolean failed = false;
    private volatile boolean eosSent = false;
    private volatile boolean eosDone = false;
    private long outFrames = 0;
    private long submitted = 0;
    private String errorMsg = "";

    public AsyncHardDecoder(String mime, int rate, int channels, byte[] csd, AudioOut out, String codecName) {
        this.mime = mime;
        this.rate = rate;
        this.channels = channels;
        this.csd = csd;
        this.out = out;
        this.codecName = codecName;
    }

    private final MediaCodec.Callback callback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(MediaCodec codec, int index) {
            try {
                ByteBuffer ib = codec.getInputBuffer(index);
                ib.clear();
                byte[] frame = inputQueue.poll();
                if (frame != null) {
                    ib.put(frame, 0, frame.length);
                    codec.queueInputBuffer(index, 0, frame.length, System.nanoTime() / 1000, 0);
                    submitted++;
                    // 无输出检测：提交超过 100 帧仍无输出 → 判定解码器异常
                    if (submitted > 100 && outFrames == 0) {
                        failed = true;
                        errorMsg = "无输出（提交" + submitted + "帧无输出，解码器异常）";
                    }
                } else if (eosSent) {
                    codec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                }
            } catch (Throwable t) {
                failed = true;
                errorMsg = "input: " + t;
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
                    out.writePcm(pcm, samples);
                    outFrames += samples;
                }
                codec.releaseOutputBuffer(index, false);
                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    eosDone = true;
                }
            } catch (Throwable t) {
                failed = true;
                errorMsg = "output: " + t;
                try {
                    codec.releaseOutputBuffer(index, false);
                } catch (Throwable ignored) {
                }
            }
        }

        @Override
        public void onError(MediaCodec codec, MediaCodec.CodecException e) {
            failed = true;
            errorMsg = "onError: " + e;
            eosDone = true;
        }

        @Override
        public void onOutputFormatChanged(MediaCodec codec, MediaFormat format) {
            // ignore
        }
    };

    @Override
    public boolean init() {
        try {
            MediaFormat fmt = MediaFormat.createAudioFormat(mime, rate, channels);
            if (csd != null) {
                fmt.setByteBuffer("csd-0", ByteBuffer.wrap(csd));
            }
            try {
                fmt.setInteger(MediaFormat.KEY_CHANNEL_MASK,
                        channels >= 2 ? android.media.AudioFormat.CHANNEL_OUT_STEREO : android.media.AudioFormat.CHANNEL_OUT_MONO);
            } catch (Throwable ignored) {
            }
            if (codecName != null && !codecName.isEmpty()) {
                codec = MediaCodec.createByCodecName(codecName);
            } else {
                codec = MediaCodec.createDecoderByType(mime);
            }
            codec.setCallback(callback); // 异步模式：start 前必须
            codec.configure(fmt, null, null, 0);
            codec.start();
            PlayerService.logStatic("异步硬解启动: " + codec.getCodecInfo().getName() + " (" + mime + ")");
            return true;
        } catch (Throwable t) {
            errorMsg = "init: " + t;
            failed = true;
            release();
            return false;
        }
    }

    @Override
    public void feed(byte[] data, int off, int len) {
        if (failed) return;
        byte[] copy = new byte[len];
        System.arraycopy(data, off, copy, 0, len);
        inputQueue.offer(copy);
    }

    @Override
    public void eos() {
        eosSent = true;
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public String name() {
        return "硬解(" + mime + (codec != null ? "/" + codec.getCodecInfo().getName() : "") + ")"
                + " sub=" + submitted + " out=" + outFrames
                + (failed ? "[FAIL:" + errorMsg + "]" : "");
    }

    @Override
    public long outputFrames() {
        return outFrames;
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
        inputQueue.clear();
    }
}
