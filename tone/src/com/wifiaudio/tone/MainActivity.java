package com.wifiaudio.tone;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * 发声器：主页面一个大按钮，点击发出 0.1s~1.0s（可调）的 1kHz 测试音。
 * 用于验证扬声器 / 音频链路。
 */
public class MainActivity extends Activity {

    private static final int SAMPLE_RATE = 48000;
    private static final int FREQ_HZ = 1000;   // 1kHz 标准测试音
    private static final int AMPLITUDE = 8000;

    private TextView durLabel;
    private TextView statusView;
    private Button playBtn;
    private volatile int durMs = 500;          // 默认 0.5s
    private volatile boolean playing = false;
    private AudioTrack track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(0xFFF7F7F7);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(0xFFF7F7F7);
        root.setPadding(dp(24), dp(56), dp(24), dp(40));

        // 标题
        TextView title = new TextView(this);
        title.setText("🔊 发声器");
        title.setTextSize(26);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        title.setTextColor(0xFF000000);
        title.setGravity(Gravity.CENTER);
        root.addView(title, lpM());

        TextView sub = new TextView(this);
        sub.setText("点击按钮发出测试音（1kHz 正弦波）");
        sub.setTextSize(13);
        sub.setTextColor(0xFF8C93B0);
        sub.setGravity(Gravity.CENTER);
        root.addView(sub, lpM(0, dp(8), 0, dp(56)));

        // 大播放按钮（圆形）
        playBtn = new Button(this);
        playBtn.setText("▶");
        playBtn.setTextSize(58);
        playBtn.setTextColor(0xFFFFFFFF);
        playBtn.setGravity(Gravity.CENTER);
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(0xFF3482FF);
        playBtn.setBackground(circle);
        root.addView(playBtn, new LinearLayout.LayoutParams(dp(180), dp(180)));
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        // 时长选择
        TextView durTitle = new TextView(this);
        durTitle.setText("声音时长");
        durTitle.setTextSize(14);
        durTitle.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        durTitle.setTextColor(0xFF000000);
        root.addView(durTitle, lpM(0, dp(56), 0, 0));

        durLabel = new TextView(this);
        durLabel.setTextSize(30);
        durLabel.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        durLabel.setTextColor(0xFF3482FF);
        durLabel.setGravity(Gravity.CENTER);
        root.addView(durLabel, lpM(0, dp(6), 0, 0));

        SeekBar seek = new SeekBar(this);
        seek.setMax(90); // progress 0~90 → 0.1s~1.0s
        seek.setProgress(durMs / 10 - 10);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                if (fromUser) durMs = (progress + 10) * 10;
                updateDurLabel();
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar sb) {
            }
        });
        root.addView(seek, lpM(0, dp(8), 0, 0));

        statusView = new TextView(this);
        statusView.setText("就绪");
        statusView.setTextSize(13);
        statusView.setTextColor(0xFF8C93B0);
        statusView.setGravity(Gravity.CENTER);
        root.addView(statusView, lpM(0, dp(24), 0, 0));

        setContentView(root);
        updateDurLabel();
    }

    private void updateDurLabel() {
        if (durLabel != null) {
            durLabel.setText(String.format(java.util.Locale.US, "%.1f 秒", durMs / 1000.0));
        }
    }

    private void play() {
        if (playing) return;
        playing = true;
        statusView.setText("播放中…");
        playBtn.setTextColor(0x99FFFFFF);
        final int ms = durMs;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int samples = (int) (ms * SAMPLE_RATE / 1000.0);
                    short[] buf = new short[samples];
                    for (int i = 0; i < samples; i++) {
                        buf[i] = (short) (Math.sin(2 * Math.PI * FREQ_HZ * i / SAMPLE_RATE) * AMPLITUDE);
                    }
                    AudioTrack t = new AudioTrack.Builder()
                            .setAudioAttributes(new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build())
                            .setAudioFormat(new AudioFormat.Builder()
                                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                    .setSampleRate(SAMPLE_RATE)
                                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                    .build())
                            .setBufferSizeInBytes(buf.length * 2)
                            .setTransferMode(AudioTrack.MODE_STATIC)
                            .build();
                    track = t;
                    t.write(buf, 0, buf.length);
                    t.play();
                    Thread.sleep(ms + 150);
                    try {
                        t.stop();
                    } catch (Throwable ignored) {
                    }
                    try {
                        t.release();
                    } catch (Throwable ignored) {
                    }
                    track = null;
                } catch (final Throwable e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusView.setText("播放失败: " + e.getMessage());
                        }
                    });
                } finally {
                    playing = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusView.setText("就绪");
                            playBtn.setTextColor(0xFFFFFFFF);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (track != null) {
            try {
                track.stop();
                track.release();
            } catch (Throwable ignored) {
            }
            track = null;
        }
        super.onDestroy();
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private LinearLayout.LayoutParams lpM() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams lpM(int l, int t, int r, int b) {
        LinearLayout.LayoutParams p = lpM();
        p.setMargins(l, t, r, b);
        return p;
    }
}
