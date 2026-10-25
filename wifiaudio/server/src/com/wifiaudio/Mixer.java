package com.wifiaudio;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public final class Mixer {
    private static final int MAX_FRAME_BYTES = 2048;
    public static final int OUT_CHANNELS = 2;
    public static final int OUT_RATE = 48000;
    private static final int POOL_SIZE = 24;
    private static final int QUEUE_FRAMES = 8;
    private final Thread dispatchThread;
    private final List<Sink> sinks = new CopyOnWriteArrayList();
    private final ArrayBlockingQueue<Frame> queue = new ArrayBlockingQueue<>(QUEUE_FRAMES);
    private final ArrayBlockingQueue<byte[]> pool = new ArrayBlockingQueue<>(POOL_SIZE);
    private volatile boolean running = true;
    private long drops = 0;
    private long lastDropLogAt = 0;
    private long lastSlowLogAt = 0;

    public interface Sink {
        void onPcm(byte[] bArr, int i);
    }

    private static final class Frame {
        final byte[] buf;
        final int bytes;
        final int frames;

        Frame(byte[] bArr, int i, int i2) {
            this.buf = bArr;
            this.bytes = i;
            this.frames = i2;
        }
    }

    public Mixer() {
        for (int i = 0; i < POOL_SIZE; i++) {
            this.pool.offer(new byte[2048]);
        }
        Thread thread = new Thread(new Runnable() { // from class: com.wifiaudio.Mixer.1
            @Override // java.lang.Runnable
            public void run() throws InterruptedException {
                Mixer.this.dispatchLoop();
            }
        }, "mixer-dispatch");
        this.dispatchThread = thread;
        thread.setDaemon(true);
        thread.start();
        Util.log("Mixer", "dispatch thread started (queue=8 frames, pool=24x2048B)");
    }

    public void addSink(Sink sink) {
        this.sinks.add(sink);
    }

    public void removeSink(Sink sink) {
        this.sinks.remove(sink);
    }

    public void mixLoopback(byte[] bArr, int i) {
        int i2 = i / 4;
        if (i2 <= 0) {
            return;
        }
        enqueueCopy(bArr, i, i2);
    }

    public void mixCallPcm(byte[] bArr, int i, int i2, int i3) {
        if (i <= 0) {
            return;
        }
        if (i3 == 48000 && i2 == 2) {
            enqueueCopy(bArr, i * 4, i);
            return;
        }
        int iCeil = (int) Math.ceil((i * 48000) / i3);
        int i4 = iCeil * 4;
        byte[] bArrPoll = i4 <= 2048 ? this.pool.poll() : null;
        if (bArrPoll == null) {
            bArrPoll = new byte[i4];
        }
        if (i2 == 1) {
            resampleMonoToStereo(bArr, i, i3, bArrPoll, iCeil);
        } else {
            resampleStereo(bArr, i, i3, bArrPoll, iCeil);
        }
        enqueueOwned(new Frame(bArrPoll, i4, iCeil));
    }

    private void enqueueCopy(byte[] bArr, int i, int i2) {
        byte[] bArrPoll = this.pool.poll();
        if (bArrPoll == null || bArrPoll.length < i) {
            if (bArrPoll != null) {
                this.pool.offer(bArrPoll);
            }
            countDrop();
        } else {
            System.arraycopy(bArr, 0, bArrPoll, 0, i);
            enqueueOwned(new Frame(bArrPoll, i, i2));
        }
    }

    private void enqueueOwned(Frame frame) {
        if (this.queue.offer(frame)) {
            Status.mixerQueueFrames = this.queue.size();
            return;
        }
        Frame framePoll = this.queue.poll();
        if (framePoll != null) {
            this.pool.offer(framePoll.buf);
        }
        if (!this.queue.offer(frame)) {
            this.pool.offer(frame.buf);
            countDrop();
        } else {
            Status.mixerQueueFrames = this.queue.size();
            countDrop();
        }
    }

    private void countDrop() {
        long j = this.drops + 1;
        this.drops = j;
        Status.mixerDrops = j;
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis - this.lastDropLogAt > 2000) {
            this.lastDropLogAt = jCurrentTimeMillis;
            Util.log("Mixer", "分发队列溢出：累计丢弃 " + this.drops + " 帧（有 Sink 阻塞或消费过慢）");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchLoop() throws InterruptedException {
        while (this.running) {
            try {
                Frame frameTake = this.queue.take();
                long jNanoTime = System.nanoTime();
                Iterator<Sink> it = this.sinks.iterator();
                while (it.hasNext()) {
                    try {
                        it.next().onPcm(frameTake.buf, frameTake.frames);
                    } catch (Throwable unused) {
                    }
                }
                long jNanoTime2 = (System.nanoTime() - jNanoTime) / 1000;
                Status.mixerDispatchUs = jNanoTime2;
                if (jNanoTime2 > 10700) {
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    if (jCurrentTimeMillis - this.lastSlowLogAt > 5000) {
                        this.lastSlowLogAt = jCurrentTimeMillis;
                        Util.log("Mixer", "分发偏慢: " + (jNanoTime2 / 1000) + "ms/帧（Sink 阻塞？队列深度=" + this.queue.size() + "）");
                    }
                }
                this.pool.offer(frameTake.buf);
                Status.mixerQueueFrames = this.queue.size();
            } catch (InterruptedException unused2) {
            }
        }
        Util.log("Mixer", "dispatch thread exited");
    }

    public void stop() {
        this.running = false;
        Thread thread = this.dispatchThread;
        if (thread != null) {
            thread.interrupt();
        }
        this.queue.clear();
        this.sinks.clear();
        Util.log("Mixer", "stopped, total dropped frames=" + this.drops);
    }

    private static void resampleMonoToStereo(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        short[] sArr = new short[i];
        for (int i4 = 0; i4 < i; i4++) {
            int i5 = i4 * 2;
            sArr[i4] = (short) ((bArr[i5 + 1] << 8) | (bArr[i5] & 255));
        }
        int i6 = 0;
        for (int i7 = 0; i7 < i3; i7++) {
            double d = (i7 * i) / i3;
            double d2 = d - ((int) d);
            short s = (short) ((sArr[r3] * (1.0d - d2)) + (sArr[Math.min(r3 + 1, i - 1)] * d2));
            byte b = (byte) (s & 255);
            bArr2[i6] = b;
            byte b2 = (byte) (s >> 8);
            bArr2[i6 + 1] = b2;
            int i8 = i6 + 3;
            bArr2[i6 + 2] = b;
            i6 += 4;
            bArr2[i8] = b2;
        }
    }

    private static void resampleStereo(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        short[] sArr = new short[i];
        short[] sArr2 = new short[i];
        for (int i4 = 0; i4 < i; i4++) {
            int i5 = i4 * 4;
            sArr[i4] = (short) ((bArr[i5] & 255) | (bArr[i5 + 1] << 8));
            sArr2[i4] = (short) ((bArr[i5 + 3] << 8) | (bArr[i5 + 2] & 255));
        }
        int i6 = 0;
        for (int i7 = 0; i7 < i3; i7++) {
            double d = (i7 * i) / i3;
            int i8 = (int) d;
            int iMin = Math.min(i8 + 1, i - 1);
            double d2 = 1.0d - (d - i8);
            short s = (short) ((sArr[i8] * d2) + (sArr[iMin] * r6));
            short s2 = (short) ((sArr2[i8] * d2) + (sArr2[iMin] * r6));
            bArr2[i6] = (byte) (s & 255);
            bArr2[i6 + 1] = (byte) (s >> 8);
            int i9 = i6 + 3;
            bArr2[i6 + 2] = (byte) (s2 & 255);
            i6 += 4;
            bArr2[i9] = (byte) (s2 >> 8);
        }
    }
}
