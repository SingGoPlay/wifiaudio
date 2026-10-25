package com.wifiaudio.receiver;

import androidx.collection.SieveCacheKt;
import com.wifiaudio.AaudioNative;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes2.dex */
public interface AudioOut {
    void close();

    int getBufferMs();

    default boolean healthy() {
        return true;
    }

    String name();

    boolean open(int i, int i2, int i3);

    void setVolume(int i);

    void writePcm(short[] sArr, int i);

    default void writePcmBytes(byte[] bArr, int i) {
        int i2 = i / 2;
        short[] sArr = new short[i2];
        ByteBuffer.wrap(bArr, 0, i).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(sArr, 0, i2);
        writePcm(sArr, i2);
    }

    public static final class AaudioOut implements AudioOut {
        private int channels;
        private volatile long handle;
        private int rate;
        private short[] xfer = new short[0];
        private int shortWrites = 0;
        private volatile boolean dead = false;
        private final AtomicInteger inFlight = new AtomicInteger();

        @Override // com.wifiaudio.receiver.AudioOut
        public void setVolume(int i) {
        }

        @Override // com.wifiaudio.receiver.AudioOut
        public boolean open(int i, int i2, int i3) {
            this.rate = i;
            this.channels = i2;
            try {
                this.handle = AaudioNative.open(i, i2, i3);
                if (this.handle != 0) {
                    return true;
                }
                PlayerService.logStatic("AAudio 打开失败（设备不支持该采样率或独占模式）");
                return false;
            } catch (Throwable th) {
                PlayerService.logStatic("AAudio 不可用（native 库加载或调用失败），回退 AudioTrack: " + th);
                this.handle = 0L;
                return false;
            }
        }

        @Override // com.wifiaudio.receiver.AudioOut
        public void writePcm(short[] sArr, int i) {
            if (this.dead) {
                return;
            }
            this.inFlight.incrementAndGet();
            try {
                long j = this.handle;
                if (j != 0 && AaudioNative.write(j, sArr, i) < i) {
                    countShortWrite();
                }
            } finally {
                this.inFlight.decrementAndGet();
            }
        }

        @Override // com.wifiaudio.receiver.AudioOut
        public void writePcmBytes(byte[] bArr, int i) {
            int i2;
            int i3;
            if (this.dead || this.channels <= 0 || i <= 0) {
                return;
            }
            this.inFlight.incrementAndGet();
            try {
                long j = this.handle;
                if (j != 0 && (i3 = (i / 2) / (i2 = this.channels)) > 0) {
                    int i4 = i2 * i3;
                    if (this.xfer.length < i4) {
                        this.xfer = new short[i4];
                    }
                    ByteBuffer.wrap(bArr, 0, i4 * 2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(this.xfer, 0, i4);
                    if (AaudioNative.write(j, this.xfer, i3) < i3) {
                        countShortWrite();
                    }
                }
            } finally {
                this.inFlight.decrementAndGet();
            }
        }

        private void countShortWrite() {
            int i = this.shortWrites + 1;
            this.shortWrites = i;
            if (i < 3 || this.dead) {
                return;
            }
            this.dead = true;
            PlayerService.logStatic("AAudio 输出连续写失败（设备切换/流断开？），标记失效并将回退 AudioTrack");
        }

        @Override // com.wifiaudio.receiver.AudioOut
        public boolean healthy() {
            return (this.dead || this.handle == 0) ? false : true;
        }

        @Override // com.wifiaudio.receiver.AudioOut
        public int getBufferMs() {
            if (this.dead) {
                return 0;
            }
            this.inFlight.incrementAndGet();
            try {
                long j = this.handle;
                if (j != 0) {
                    long bufferedFrames = AaudioNative.getBufferedFrames(j);
                    if (bufferedFrames > 0) {
                        long jMax = (bufferedFrames * 1000) / Math.max(1, this.rate);
                        if (jMax > SieveCacheKt.NodeLinkMask) {
                            jMax = 2147483647L;
                        }
                        return PlayerService.saneBufferedMs((int) jMax);
                    }
                }
                return 0;
            } finally {
                this.inFlight.decrementAndGet();
            }
        }

        @Override // com.wifiaudio.receiver.AudioOut
        public void close() throws InterruptedException {
            long j = this.handle;
            if (j == 0) {
                return;
            }
            this.handle = 0L;
            for (int i = 0; i < 200 && this.inFlight.get() > 0; i++) {
                try {
                    Thread.sleep(5L);
                } catch (InterruptedException unused) {
                }
            }
            if (this.inFlight.get() > 0) {
                PlayerService.logStatic("AAudio 关闭时仍有写入未退出，保留句柄不释放（宁可泄漏也不崩溃）");
            } else {
                AaudioNative.close(j);
            }
        }

        @Override // com.wifiaudio.receiver.AudioOut
        public String name() {
            return this.dead ? "AAudio(已失效→AudioTrack)" : "AAudio";
        }
    }
}
