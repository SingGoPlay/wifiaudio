package com.wifiaudio.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;

/**
 * WiFiAudio 编解码测试工具
 *  - 音频: AAC (aac.aac) / OPUS (opus.opus)
 *  - 解码: MediaCodec 硬解 / 软解 (libopus / fdk-aac)
 *  - 输出: AudioTrack / AAudio
 */
public class MainActivity extends Activity {

    private static Context appContext;

    private Spinner audioSpinner;
    private Spinner decodeSpinner;
    private Spinner outputSpinner;
    private Spinner bufferSpinner;
    private Button playBtn;
    private TextView statusView;
    private TextView logView;
    private Player player;
    private Thread statsThread;
    private volatile boolean playing = false;

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        buildUi();
        player = new Player(new Player.Listener() {
            @Override
            public void onLog(final String line) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appendLog(line);
                    }
                });
            }
        });
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xFF101318);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(20), dp(16), dp(24));
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("🎛️ WiFiAudio 编解码测试");
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(0xFFE8EAED);
        root.addView(title);

        TextView sub = new TextView(this);
        sub.setText("对比 MediaCodec 硬解 / 软解，AudioTrack / AAudio 输出");
        sub.setTextSize(12);
        sub.setTextColor(0xFF9AA3B2);
        sub.setPadding(0, 0, 0, dp(12));
        root.addView(sub);

        // 音频选择
        root.addView(label("音频文件"));
        audioSpinner = spinner(new String[]{"AAC (aac.aac)", "OPUS (opus.opus)"});
        root.addView(audioSpinner);

        // 解码器选择（动态列出设备支持的 AAC/OPUS 解码器）
        root.addView(label("解码器"));
        decodeSpinner = new Spinner(this);
        refreshDecodeSpinner(audioSpinner.getSelectedItemPosition());
        root.addView(decodeSpinner);
        audioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshDecodeSpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 输出选择
        root.addView(label("输出"));
        outputSpinner = spinner(new String[]{"AudioTrack", "AAudio (NDK 低延迟)"});
        root.addView(outputSpinner);

        // 缓冲
        root.addView(label("输出缓冲"));
        bufferSpinner = spinner(new String[]{"40ms (低延迟)", "80ms", "160ms (稳定)"});
        root.addView(bufferSpinner);

        // 播放按钮
        playBtn = button("▶ 开始测试");
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(48));
        blp.setMargins(0, dp(12), 0, 0);
        root.addView(playBtn, blp);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    stopTest();
                } else {
                    startTest();
                }
            }
        });

        // 状态
        statusView = new TextView(this);
        statusView.setText("未开始");
        statusView.setTextSize(13);
        statusView.setTextColor(0xFFE8EAED);
        statusView.setPadding(0, dp(10), 0, 0);
        root.addView(statusView);

        // 日志
        logView = new TextView(this);
        logView.setTextSize(11);
        logView.setTextColor(0xFF8FA3C0);
        logView.setTextIsSelectable(true);
        logView.setPadding(0, dp(8), 0, 0);
        root.addView(logView);

        setContentView(scroll);
    }

    /** 刷新解码器下拉：默认 + 设备所有 AAC/OPUS 解码器 + 软解 */
    private java.util.List<String> decoderChoices = new java.util.ArrayList<String>();

    private void refreshDecodeSpinner(int audioType) {
        decoderChoices.clear();
        decoderChoices.add("默认（系统自动选）");
        String mime = audioType == 0 ? "audio/mp4a-latm" : "audio/opus";
        try {
            android.media.MediaCodecList mcl = new android.media.MediaCodecList(android.media.MediaCodecList.ALL_CODECS);
            for (android.media.MediaCodecInfo info : mcl.getCodecInfos()) {
                if (info.isEncoder()) continue;
                for (String t : info.getSupportedTypes()) {
                    if (mime.equals(t)) {
                        decoderChoices.add(info.getName());
                        break;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        decoderChoices.add("软解 (libopus/fdk-aac)");
        decoderChoices.add("MediaExtractor 标准模式 (ExoPlayer同款)");
        decoderChoices.add("MediaCodec 异步模式 (ExoPlayer 1.9同款)");
        ArrayAdapter<String> a = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, decoderChoices);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        decodeSpinner.setAdapter(a);
    }

    private void startTest() {
        int audioType = audioSpinner.getSelectedItemPosition(); // 0=AAC 1=OPUS
        int decodePos = decodeSpinner.getSelectedItemPosition();
        boolean useAaudio = outputSpinner.getSelectedItemPosition() == 1;
        int bufferMs = new int[]{40, 80, 160}[bufferSpinner.getSelectedItemPosition()];
        String asset = audioType == 0 ? "aac.aac" : "opus.opus";

        // 创建解码器
        Decoder decoder;
        StringBuilder msg = new StringBuilder();
        String choice = decodePos >= 0 && decodePos < decoderChoices.size() ? decoderChoices.get(decodePos) : "默认（系统自动选）";
        if ("软解 (libopus/fdk-aac)".equals(choice)) {
            decoder = new SoftDecoder(audioType == 0 ? SoftDecoder.TYPE_AAC : SoftDecoder.TYPE_OPUS);
        } else if ("MediaExtractor 标准模式 (ExoPlayer同款)".equals(choice)) {
            decoder = new MediaExtractorDecoder();
        } else if ("MediaCodec 异步模式 (ExoPlayer 1.9同款)".equals(choice)) {
            decoder = new MediaCodecAsyncDecoder();
        } else if ("默认（系统自动选）".equals(choice)) {
            decoder = audioType == 0 ? new MediaCodecAacDecoder(null) : new MediaCodecOpusDecoder(null);
        } else {
            decoder = audioType == 0 ? new MediaCodecAacDecoder(choice) : new MediaCodecOpusDecoder(choice);
        }
        if (!decoder.init(asset, msg)) {
            appendLog("初始化失败: " + msg);
            statusView.setText("初始化失败");
            return;
        }

        appendLog("════════ 测试开始 ════════");
        appendLog("音频: " + asset + " | 解码器: " + choice
                + " | " + (useAaudio ? "AAudio" : "AudioTrack") + " | 缓冲 " + bufferMs + "ms");
        playing = true;
        playBtn.setText("■ 停止");
        statusView.setText("播放中...");
        player.start(decoder, useAaudio, bufferMs);

        // 状态刷新线程
        statsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (playing) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusView.setText(player.stats());
                        }
                    });
                }
            }
        }, "stats");
        statsThread.start();
    }

    private void stopTest() {
        playing = false;
        player.stop();
        playBtn.setText("▶ 开始测试");
        statusView.setText(player.stats());
        appendLog("════════ 测试停止 ════════");
    }

    private void appendLog(String s) {
        String cur = logView.getText().toString();
        String next = cur.isEmpty() ? s : cur + "\n" + s;
        // 截断：最多 500 行，防 UI 卡死
        int count = 0;
        for (int i = 0; i < next.length(); i++) if (next.charAt(i) == '\n') count++;
        if (count > 500) {
            int idx = next.indexOf('\n');
            for (int k = 0; k < count - 500 && idx >= 0; k++) idx = next.indexOf('\n', idx + 1);
            next = idx >= 0 ? next.substring(idx + 1) : next;
        }
        logView.setText(next);
        final ScrollView parent = (ScrollView) logView.getParent().getParent();
        parent.post(new Runnable() {
            @Override
            public void run() {
                parent.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    // ===== UI helpers =====

    private TextView label(String text) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(13);
        t.setTextColor(0xFF9AA3B2);
        t.setPadding(0, dp(8), 0, dp(4));
        return t;
    }

    private Spinner spinner(String[] items) {
        Spinner s = new Spinner(this);
        ArrayAdapter<String> a = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(a);
        return s;
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(16);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setTextColor(Color.WHITE);
        b.setBackground(bg(0xFF3D8BFF, dp(12)));
        return b;
    }

    private GradientDrawable bg(int color, float radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(radius);
        return g;
    }

    private int dp(int v) {
        return Math.round(getResources().getDisplayMetrics().density * v);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playing = false;
        player.stop();
    }
}
