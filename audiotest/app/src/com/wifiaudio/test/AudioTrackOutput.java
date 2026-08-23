package com.wifiaudio.test;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;

/** AudioTrack 输出（系统默认路径） */
public final class AudioTrackOutput {
    private AudioTrack track;
    private final int rate;
    private final int channels;

    public AudioTrackOutput(int rate, int channels, int bufferMs) {
        this.rate = rate;
        this.channels = channels;
        int chMask = channels >= 2 ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO;
        int minBuf = AudioTrack.getMinBufferSize(rate, chMask, AudioFormat.ENCODING_PCM_16BIT);
        int buf = Math.max(minBuf, (int) Math.round(rate * 2.0 * channels * bufferMs / 1000.0));
        AudioTrack.Builder builder = new AudioTrack.Builder()
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
                builder.setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY);
            } catch (Throwable ignored) {
            }
        }
        track = builder.build();
        track.play();
    }

    public void write(short[] pcm, int frames) {
        if (track != null) track.write(pcm, 0, frames * channels);
    }

    /** 当前缓冲毫秒（播放头位置估算） */
    public int getBufferMs() {
        if (track == null) return 0;
        return (int) (track.getBufferSizeInFrames() * 1000L / rate);
    }

    public void release() {
        if (track != null) {
            try {
                track.stop();
                track.release();
            } catch (Throwable ignored) {
            }
            track = null;
        }
    }
}
