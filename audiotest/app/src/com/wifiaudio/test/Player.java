package com.wifiaudio.test;

/**
 * 播放引擎：解码器 + 输出（AudioTrack / AAudio）组合。
 * 统计解码耗时、输出缓冲、错误。
 */
public final class Player {
    public interface Listener {
        void onLog(String line);
    }

    private final Listener listener;
    private Decoder decoder;
    private boolean useAaudio;
    private AudioTrackOutput trackOut;
    private long aaudioHandle;
    private int rate;
    private int channels;
    private Thread thread;
    private volatile boolean running;

    // 统计
    private volatile long decodedFrames = 0;
    private volatile long decodeTimeUs = 0;
    private volatile int errCount = 0;
    private volatile String lastError = "";
    private volatile int outBufferMs = 0;

    public Player(Listener l) {
        this.listener = l;
    }

    /** 启动播放（工作线程中调用 init） */
    public void start(final Decoder decoder, final boolean aaudio, final int bufferMs) {
        stop();
        this.decoder = decoder;
        this.useAaudio = aaudio;
        running = true;
        decodedFrames = 0;
        decodeTimeUs = 0;
        errCount = 0;
        lastError = "";
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runPlayback(bufferMs);
                } catch (Throwable t) {
                    lastError = String.valueOf(t);
                    errCount++;
                    log("播放异常: " + t);
                } finally {
                    closeOutput();
                    running = false;
                }
            }
        }, "player");
        thread.start();
    }

    private void runPlayback(int bufferMs) throws Exception {
        rate = decoder.rate();
        channels = decoder.channels();
        if (rate <= 0) rate = 48000;
        if (channels <= 0) channels = 2;
        log("解码器: " + decoder.info());
        log("输出: " + (useAaudio ? "AAudio (NDK 低延迟)" : "AudioTrack") + "  " + rate + "Hz/" + channels + "ch");

        if (useAaudio) {
            aaudioHandle = AaudioNative.open(rate, channels, bufferMs);
            if (aaudioHandle == 0) {
                lastError = "AAudio 打开失败（可能系统不支持低延迟）";
                throw new RuntimeException(lastError);
            }
            log("AAudio 流已打开");
        } else {
            trackOut = new AudioTrackOutput(rate, channels, bufferMs);
            log("AudioTrack 已打开");
        }

        short[] pcm = new short[4096 * 2]; // 最多 4096 采样/声道
        long frameCount = 0;
        while (running) {
            byte[] frame = decoder.nextFrame();
            if (frame == null) break;
            long t0 = System.nanoTime();
            int samples = decoder.decodeFrame(frame, frame.length, pcm, 4096);
            long t1 = System.nanoTime();
            if (samples > 0) {
                decodeTimeUs += (t1 - t0) / 1000;
                if (useAaudio) {
                    AaudioNative.write(aaudioHandle, pcm, samples);
                } else {
                    trackOut.write(pcm, samples);
                }
                decodedFrames += samples;
                frameCount++;
                if (frameCount % 50 == 0) {
                    long bufferedFrames = useAaudio ? AaudioNative.getBufferedFrames(aaudioHandle) : 0;
                    outBufferMs = useAaudio ? (int) (bufferedFrames * 1000L / rate) : trackOut.getBufferMs();
                    log(String.format("播放中... 帧%d 已解码%.1f秒 解码耗时%.1fms/帧 输出缓冲%dms | %s",
                            frameCount, decodedFrames / (double) rate,
                            decodeTimeUs / 1000.0 / Math.max(1, frameCount), outBufferMs, decoder.info()));
                }
            } else if (samples == -100) {
                log("解码终止：连续解码错误");
                break;
            } else if (samples < 0) {
                errCount++;
                lastError = "解码错误码=" + samples;
                if (errCount <= 3) {
                    log("解码错误码=" + samples + " (详情见结束日志)");
                }
            }
        }
        log("播放结束: 共解码 " + String.format("%.1f", decodedFrames / (double) rate) + " 秒, 错误 " + errCount + " 次 | " + decoder.info());
    }

    public void stop() {
        running = false;
        if (thread != null) {
            try {
                thread.join(2000);
            } catch (InterruptedException ignored) {
            }
            thread = null;
        }
    }

    private void closeOutput() {
        if (aaudioHandle != 0) {
            AaudioNative.close(aaudioHandle);
            aaudioHandle = 0;
        }
        if (trackOut != null) {
            trackOut.release();
            trackOut = null;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public String stats() {
        return "解码输出 " + String.format("%.1f", decodedFrames / (double) Math.max(1, rate)) + "s"
                + " | 平均解码 " + (decodeTimeUs / 1000.0 / Math.max(1, decodedFrames / 1024.0)) + "ms/帧"
                + " | 输出缓冲 " + outBufferMs + "ms | 错误 " + errCount + (lastError.isEmpty() ? "" : " (" + lastError + ")");
    }

    private void log(String s) {
        if (listener != null) listener.onLog(s);
    }
}
