package com.wifiaudio.receiver;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;

import com.wifiaudio.AaudioNative;

/**
 * 音频输出抽象：AudioTrack / AAudio 可切换。
 */
public interface AudioOut {
    /** 打开输出 */
    boolean open(int rate, int channels, int bufferMs);

    /** 写入 PCM（short 交错） */
    void writePcm(short[] pcm, int frames);

    /** 当前输出缓冲 ms */
    int getBufferMs();

    /** 音量 0-100（AAudio 可能忽略） */
    void setVolume(int volume);

    void close();

    String name();

    /** AudioTrack 输出 */
    final class AudioTrackOut implements AudioOut {
        private AudioTrack track;
        private int rate;
        private int channels;

        @Override
        public boolean open(int rate, int channels, int bufferMs) {
            this.rate = rate;
            this.channels = channels;
            int chMask = channels >= 2 ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO;
            int minBuf = AudioTrack.getMinBufferSize(rate, chMask, AudioFormat.ENCODING_PCM_16BIT);
            int buf = Math.max(minBuf, (int) Math.round(rate * 2.0 * channels * bufferMs / 1000.0));
            try {
                AudioTrack.Builder b = new AudioTrack.Builder()
                        .setAudioAttributes(new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build())
                        .setAudioFormat(new AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(rate)
                                .setChannelMask(chMask)
                                .build())
                        .setBufferSizeInBytes(buf)
                        .setTransferMode(AudioTrack.MODE_STREAM);
                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    try {
                        b.setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY);
                    } catch (Throwable ignored) {
                    }
                }
                track = b.build();
                track.play();
                return true;
            } catch (Throwable t) {
                PlayerService.logStatic("AudioTrack 打开失败: " + t);
                track = null;
                return false;
            }
        }

        @Override
        public void writePcm(short[] pcm, int frames) {
            if (track != null) track.write(pcm, 0, frames * channels);
        }

        @Override
        public int getBufferMs() {
            if (track == null) return 0;
            return (int) (track.getBufferSizeInFrames() * 1000L / rate);
        }

        @Override
        public void setVolume(int volume) {
            if (track != null) {
                try {
                    track.setVolume(volume / 100f);
                } catch (Throwable ignored) {
                }
            }
        }

        @Override
        public void close() {
            if (track != null) {
                try {
                    track.stop();
                    track.release();
                } catch (Throwable ignored) {
                }
                track = null;
            }
        }

        @Override
        public String name() {
            return "AudioTrack";
        }
    }

    /** AAudio 输出 */
    final class AaudioOut implements AudioOut {
        private long handle;
        private int rate;

        @Override
        public boolean open(int rate, int channels, int bufferMs) {
            this.rate = rate;
            handle = AaudioNative.open(rate, channels, bufferMs);
            if (handle == 0) {
                PlayerService.logStatic("AAudio 打开失败");
                return false;
            }
            return true;
        }

        @Override
        public void writePcm(short[] pcm, int frames) {
            if (handle != 0) AaudioNative.write(handle, pcm, frames);
        }

        @Override
        public int getBufferMs() {
            if (handle == 0) return 0;
            return (int) (AaudioNative.getBufferedFrames(handle) * 1000L / rate);
        }

        @Override
        public void setVolume(int volume) {
            // AAudio 用系统音量
        }

        @Override
        public void close() {
            if (handle != 0) {
                AaudioNative.close(handle);
                handle = 0;
            }
        }

        @Override
        public String name() {
            return "AAudio";
        }
    }
}
