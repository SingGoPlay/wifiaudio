package com.wifiaudio;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 音频混合与分发：
 *  - 主输入：系统回环捕获 (48kHz stereo PCM16)
 *  - 副输入：蜂窝通话 VOICE_CALL (任意采样率 mono/stereo，线性重采样到 48k stereo)
 *  - 输出：48kHz stereo PCM16，分发给所有接收端（TCP / UDP / HTTP / 编码器）
 *
 * <pre>
 * v4.18 线程解耦（重要）：
 *   捕获线程（AudioRecord.read 循环）只做「拷贝一帧 → 入队」，随即返回；
 *   独立的 mixer-dispatch 线程依次调用各 Sink.onPcm()。
 *   原因：Sink 内部存在真实阻塞点 ——
 *     · MediaCodec.dequeueInputBuffer(20000)：OPUS/AAC 编码每帧最长阻塞 20ms
 *     · DatagramSocket.send()：发送缓冲满（WiFi 拥塞）时阻塞
 *   这些调用原先直接跑在捕获线程上，一旦阻塞就会让 AudioRecord 内部缓冲溢出，
 *   表现为爆音/断续/丢帧。解耦后 Sink 再慢也只会丢「分发队列」里的帧，不砸捕获。
 *
 *   顺带修掉一个竞态：通话帧（CallCapture 线程）与回环帧原先并发调用 Sink，
 *   现在统一走同一个分发线程，Sink 侧不再被并发重入。
 *
 * 队列语义（实时优先）：深度上限 QUEUE_FRAMES 帧，满则丢弃最旧一帧并计数，
 *   **绝不阻塞生产者**；帧缓冲用池复用，避免每帧 new byte[]。
 * </pre>
 */
public final class Mixer {

    public static final int OUT_RATE = 48000;
    public static final int OUT_CHANNELS = 2;

    /** 分发队列深度（帧）。48k/512 帧≈10.7ms/帧 → 满队列约 214ms 冗余 */
    private static final int QUEUE_FRAMES = 20;
    /** 帧缓冲池容量（须 ≥ 队列深度，避免消费者稍慢就无缓冲可用） */
    private static final int POOL_SIZE = 24;
    /** 单帧字节数上限：512 帧 × 2ch × 2B = 2048 */
    private static final int MAX_FRAME_BYTES = 512 * OUT_CHANNELS * 2;

    private final List<Sink> sinks = new CopyOnWriteArrayList<Sink>();

    private final ArrayBlockingQueue<Frame> queue = new ArrayBlockingQueue<Frame>(QUEUE_FRAMES);
    private final ArrayBlockingQueue<byte[]> pool = new ArrayBlockingQueue<byte[]>(POOL_SIZE);
    private volatile boolean running = true;
    private final Thread dispatchThread;

    private long drops = 0;
    private long lastDropLogAt = 0;
    private long lastSlowLogAt = 0;

    public interface Sink {
        /** 收到一帧 48k stereo PCM16 数据（字节数 = frames*4）。
         *  实现必须在本方法内完成数据拷贝，不得保留 pcm 引用（缓冲会被回收复用）。 */
        void onPcm(byte[] pcm, int frames);
    }

    private static final class Frame {
        final byte[] buf;
        final int bytes;
        final int frames;

        Frame(byte[] buf, int bytes, int frames) {
            this.buf = buf;
            this.bytes = bytes;
            this.frames = frames;
        }
    }

    public Mixer() {
        // 预填充帧缓冲池（不预填充 = 第一帧起就无缓冲可用，全部被丢弃）
        for (int i = 0; i < POOL_SIZE; i++) {
            pool.offer(new byte[MAX_FRAME_BYTES]);
        }
        dispatchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                dispatchLoop();
            }
        }, "mixer-dispatch");
        dispatchThread.setDaemon(true);
        dispatchThread.start();
        Util.log("Mixer", "dispatch thread started (queue=" + QUEUE_FRAMES
                + " frames, pool=" + POOL_SIZE + "x" + MAX_FRAME_BYTES + "B)");
    }

    public void addSink(Sink s) {
        sinks.add(s);
    }

    public void removeSink(Sink s) {
        sinks.remove(s);
    }

    /** 回环 PCM（捕获线程调用：只拷贝入队，立即返回，绝不阻塞） */
    public void mixLoopback(byte[] pcm, int bytes) {
        int frames = bytes / (OUT_CHANNELS * 2);
        if (frames <= 0) return;
        enqueueCopy(pcm, bytes, frames);
    }

    /**
     * 通话 PCM 混入。源采样率/声道任意，线性重采样到 48k stereo 后入队。
     * 由于通话与媒体极少同时发生（通话时媒体通常暂停），此处策略：通话期间通话帧直接作为一帧输出，
     * 媒体恢复时自然切换，避免复杂混音。
     */
    public void mixCallPcm(byte[] pcm, int frames, int channels, int sampleRate) {
        if (frames <= 0) return;
        if (sampleRate == OUT_RATE && channels == OUT_CHANNELS) {
            enqueueCopy(pcm, frames * OUT_CHANNELS * 2, frames);
            return;
        }
        int outFrames = (int) Math.ceil((long) frames * OUT_RATE / sampleRate);
        int outBytes = outFrames * OUT_CHANNELS * 2;
        // 重采样结果是自己新建的缓冲 → 可「拥有」入队，免去二次拷贝
        byte[] out = (outBytes <= MAX_FRAME_BYTES) ? pool.poll() : null;
        if (out == null) out = new byte[outBytes];
        if (channels == 1) {
            resampleMonoToStereo(pcm, frames, sampleRate, out, outFrames);
        } else {
            resampleStereo(pcm, frames, sampleRate, out, outFrames);
        }
        enqueueOwned(new Frame(out, outBytes, outFrames));
    }

    /** 拷贝入队（源缓冲由调用方复用，必须先拷贝） */
    private void enqueueCopy(byte[] src, int bytes, int frames) {
        byte[] buf = pool.poll();
        if (buf == null || buf.length < bytes) {
            // 缓冲池耗尽（消费者严重落后）：宁可丢帧也不在捕获线程里分配/阻塞
            if (buf != null) pool.offer(buf);
            countDrop();
            return;
        }
        System.arraycopy(src, 0, buf, 0, bytes);
        enqueueOwned(new Frame(buf, bytes, frames));
    }

    /** 入队（缓冲所有权转移给队列）。满则丢最旧帧，保持实时性。 */
    private void enqueueOwned(Frame f) {
        if (queue.offer(f)) {
            Status.mixerQueueFrames = queue.size();
            return;
        }
        Frame old = queue.poll();
        if (old != null) pool.offer(old.buf);
        if (!queue.offer(f)) {
            pool.offer(f.buf);
            countDrop();
            return;
        }
        Status.mixerQueueFrames = queue.size();
        countDrop();
    }

    private void countDrop() {
        drops++;
        Status.mixerDrops = drops;
        long now = System.currentTimeMillis();
        if (now - lastDropLogAt > 2000) {
            lastDropLogAt = now;
            Util.log("Mixer", "分发队列溢出：累计丢弃 " + drops + " 帧（有 Sink 阻塞或消费过慢）");
        }
    }

    /** 分发线程：串行调用所有 Sink，Sink 再慢也只影响本线程 */
    private void dispatchLoop() {
        while (running) {
            Frame f;
            try {
                f = queue.take();
            } catch (InterruptedException e) {
                break;
            }
            long t0 = System.nanoTime();
            for (Sink s : sinks) {
                try {
                    s.onPcm(f.buf, f.frames);
                } catch (Throwable ignored) {
                }
            }
            long us = (System.nanoTime() - t0) / 1000;
            Status.mixerDispatchUs = us;
            // 单帧分发耗时超过一帧时长（48k/512≈10.7ms）说明有 Sink 在阻塞，限流告警
            if (us > 10700) {
                long now = System.currentTimeMillis();
                if (now - lastSlowLogAt > 5000) {
                    lastSlowLogAt = now;
                    Util.log("Mixer", "分发偏慢: " + (us / 1000) + "ms/帧（Sink 阻塞？队列深度="
                            + queue.size() + "）");
                }
            }
            pool.offer(f.buf);
            Status.mixerQueueFrames = queue.size();
        }
        Util.log("Mixer", "dispatch thread exited");
    }

    public void stop() {
        running = false;
        if (dispatchThread != null) dispatchThread.interrupt();
        queue.clear();
        sinks.clear();
        Util.log("Mixer", "stopped, total dropped frames=" + drops);
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
