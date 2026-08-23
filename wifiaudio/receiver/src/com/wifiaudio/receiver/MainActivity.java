package com.wifiaudio.receiver;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * WiFiAudio 接收端 —— 主界面（支持夜间模式）
 */
public class MainActivity extends Activity {

    private static final String PREFS_NAME = "wifiaudio_prefs";
    private static final String KEY_PRESETS = "presets";
    private static final String KEY_LAST = "last_connect";
    private static final String KEY_NIGHT = "night_mode";

    // 配色（日间 / 夜间）：[背景, 卡片, 输入框, 文字, 次要文字, 主色] —— MIUI/Miuix 风格
    private static final int[][] THEMES = {
            {0xFFF7F7F7, 0xFFFFFFFF, 0xFFF0F0F0, 0xFF000000, 0xFF8C93B0, 0xFF3482FF}, // 日间
            {0xFF242424, 0xFF1E1E1E, 0xFF434343, 0xFFF2F2F2, 0xFF787E96, 0xFF277AF7}, // 夜间
    };

    private boolean night = false;
    private int[] c; // 当前配色

    private EditText ipInput;
    private EditText portInput;
    private Spinner transportSpinner;
    private Spinner bufferSpinner;
    private Spinner outputSpinner;
    private Spinner decodeModeSpinner;
    private Spinner decoderSpinner;
    private EditText bufferMsInput;
    private android.widget.SeekBar volumeBar;
    private Switch autoReconnectSwitch;
    private Button connectBtn;
    private TextView statusView;
    private TextView diagView;
    private TextView logView;
    private TextView nearbyView;
    private java.util.Set<String> nearbyDevices = new java.util.LinkedHashSet<String>();
    private Thread discoverThread;
    private TextView decoderListView;
    private boolean connected = false;
    // ===== 重构 UI：状态横幅 + 统计网格 =====
    private LinearLayout heroCard;
    private TextView heroDot;
    private TextView heroStatus;
    private LinearLayout statsGrid;
    private TextView statsDetail;
    private TextView stRate, stKbps, stBuf, stLat, stJit, stRx;

    // 预设
    private Spinner presetSpinner;
    private EditText presetNameInput;
    private List<JSONObject> presets = new ArrayList<JSONObject>();
    private boolean presetSelectGuard = false;

    private static final String[] TRANSPORT_VALUES = {"auto", "tcp", "udp"};
    private static final String[] BUFFER_VALUES = {"auto", "low", "high", "custom"};

    private final BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!PlayerService.ACTION_STATE.equals(intent.getAction())) return;
            onPlayerState(intent.getStringExtra(PlayerService.EXTRA_STATE),
                    intent.getStringExtra(PlayerService.EXTRA_MSG),
                    intent.getStringExtra(PlayerService.EXTRA_CODEC),
                    intent.getIntExtra(PlayerService.EXTRA_RATE, 0));
        }
    };

    /** 实时统计（每秒刷新） */
    private final BroadcastReceiver statsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!PlayerService.ACTION_STATS.equals(intent.getAction())) return;
            updateStats(intent.getStringExtra(PlayerService.EXTRA_CUR_TRANSPORT),
                    intent.getStringExtra(PlayerService.EXTRA_CODEC),
                    intent.getIntExtra(PlayerService.EXTRA_RATE, 0),
                    intent.getIntExtra(PlayerService.EXTRA_CHANNELS, 0),
                    intent.getIntExtra(PlayerService.EXTRA_BITRATE, 0),
                    intent.getIntExtra(PlayerService.EXTRA_AUDIO_MS, 0),
                    intent.getIntExtra(PlayerService.EXTRA_JITTER_MS, 0),
                    intent.getIntExtra(PlayerService.EXTRA_LATENCY_MS, 0),
                    intent.getIntExtra(PlayerService.EXTRA_RX_KB, 0),
                    intent.getIntExtra(PlayerService.EXTRA_DEC_FRAMES, 0),
                    intent.getIntExtra(PlayerService.EXTRA_TARGET_MS, 0),
                    intent.getLongExtra(PlayerService.EXTRA_DROPPED, 0),
                    intent.getLongExtra(PlayerService.EXTRA_UNDERRUN, 0),
                    intent.getStringExtra(PlayerService.EXTRA_OUTPUT),
                    intent.getStringArrayListExtra(PlayerService.EXTRA_LOG));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        night = prefs().getBoolean(KEY_NIGHT, false);
        c = THEMES[night ? 1 : 0];
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setStatusBarColor(c[0]);

        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        buildUi();
        loadPresets();
        restoreLastConnect();
        startDiscovery();
        PlayerService.setStateListener(new PlayerService.StateListener() {
            @Override
            public void onState(final String state, final String msg, final String codec, final int rate, final int channels) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onPlayerState(state, msg, codec, rate);
                    }
                });
            }
        });
        IntentFilter filter = new IntentFilter(PlayerService.ACTION_STATE);
        IntentFilter sf = new IntentFilter(PlayerService.ACTION_STATS);
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(stateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
            registerReceiver(statsReceiver, sf, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(stateReceiver, filter);
            registerReceiver(statsReceiver, sf);
        }
    }

    // ================= UI 构建 =================

    private GradientDrawable bg(int color, float radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(radius);
        return g;
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(bg(c[1], dp(16)));
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(lp);
        return card;
    }

    private TextView cardTitle(String text) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(14);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setTextColor(c[3]);
        t.setPadding(0, 0, 0, dp(10));
        return t;
    }

    private TextView label(String text) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(13);
        t.setTextColor(c[4]);
        t.setPadding(0, dp(6), 0, dp(4));
        return t;
    }

    private EditText input(String hint, int inputType) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setHintTextColor(c[4]);
        e.setTextColor(c[3]);
        e.setSingleLine(true);
        e.setBackground(bg(c[2], dp(12)));
        e.setPadding(dp(12), dp(10), dp(12), dp(10));
        if (inputType >= 0) e.setInputType(inputType);
        return e;
    }

    private Spinner spinner(String[] items) {
        Spinner s = new Spinner(this);
        ArrayAdapter<String> a = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(a);
        return s;
    }

    private Button button(String text, int style) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(15);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        if (style == 0) { // 主按钮
            b.setBackground(bg(c[5], dp(16)));
            b.setTextColor(Color.WHITE);
        } else { // 次按钮
            b.setBackground(bg(c[2], dp(16)));
            b.setTextColor(c[3]);
        }
        b.setPadding(dp(8), dp(6), dp(8), dp(6));
        return b;
    }

    /** 统计格子（网格卡片用）：大数字 + 小标签，返回数值 TextView */
    private TextView statValue(LinearLayout row, String label) {
        LinearLayout cell = new LinearLayout(this);
        cell.setOrientation(LinearLayout.VERTICAL);
        cell.setGravity(Gravity.CENTER);
        cell.setBackground(bg(c[2], dp(16)));
        cell.setPadding(dp(4), dp(12), dp(4), dp(10));
        cell.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView val = new TextView(this);
        val.setText("—");
        val.setTextSize(22);
        val.setTypeface(Typeface.DEFAULT_BOLD);
        val.setTextColor(c[5]);
        val.setGravity(Gravity.CENTER);
        cell.addView(val);
        TextView lb = new TextView(this);
        lb.setText(label);
        lb.setTextSize(11);
        lb.setTextColor(c[4]);
        lb.setGravity(Gravity.CENTER);
        cell.addView(lb);
        row.addView(cell);
        return val;
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(c[0]);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(20), dp(16), dp(24));
        scroll.addView(root);

        // ===== 标题行 =====
        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        titleRow.setPadding(0, 0, 0, dp(12));
        TextView title = new TextView(this);
        title.setText("🎧 WiFiAudio 接收端");
        title.setTextSize(22);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(c[3]);
        titleRow.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        Button nightBtn = new Button(this);
        nightBtn.setText(night ? "☀️" : "🌙");
        nightBtn.setTextSize(16);
        nightBtn.setBackground(bg(c[2], dp(14)));
        nightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                night = !night;
                prefs().edit().putBoolean(KEY_NIGHT, night).apply();
                recreate();
            }
        });
        titleRow.addView(nightBtn);
        root.addView(titleRow);

        // ===== Hero 状态横幅 =====
        heroCard = new LinearLayout(this);
        heroCard.setOrientation(LinearLayout.VERTICAL);
        heroCard.setBackground(bg(0xFF3482FF, dp(16)));
        heroCard.setPadding(dp(20), dp(18), dp(20), dp(14));
        LinearLayout.LayoutParams heroLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        heroLp.setMargins(0, 0, 0, dp(12));
        heroCard.setLayoutParams(heroLp);

        LinearLayout heroTop = new LinearLayout(this);
        heroTop.setOrientation(LinearLayout.HORIZONTAL);
        heroTop.setGravity(Gravity.CENTER_VERTICAL);
        heroDot = new TextView(this);
        heroDot.setText("●");
        heroDot.setTextSize(26);
        heroDot.setTextColor(0xCCFFFFFF);
        heroTop.addView(heroDot);
        heroStatus = new TextView(this);
        heroStatus.setText("未连接");
        heroStatus.setTextSize(22);
        heroStatus.setTypeface(Typeface.DEFAULT_BOLD);
        heroStatus.setTextColor(Color.WHITE);
        heroStatus.setPadding(dp(12), 0, 0, 0);
        heroTop.addView(heroStatus);
        heroCard.addView(heroTop);

        // 提示/连接详情（保留 statusView 字段语义，供 connect() 提示）
        statusView = new TextView(this);
        statusView.setText("等待连接…");
        statusView.setTextSize(12);
        statusView.setTextColor(0xCCFFFFFF);
        statusView.setPadding(0, dp(6), 0, 0);
        heroCard.addView(statusView);

        // 快捷操作（Hero 内）
        LinearLayout quickRow = new LinearLayout(this);
        quickRow.setOrientation(LinearLayout.HORIZONTAL);
        quickRow.setPadding(0, dp(14), 0, 0);
        Button toneBtn = new Button(this);
        toneBtn.setText("🔊 测试音");
        toneBtn.setTextSize(14);
        toneBtn.setTypeface(Typeface.DEFAULT_BOLD);
        GradientDrawable qb = new GradientDrawable();
        qb.setColor(0x33FFFFFF);
        qb.setCornerRadius(dp(16));
        toneBtn.setBackground(qb);
        toneBtn.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams tblp = new LinearLayout.LayoutParams(0, dp(40), 1f);
        tblp.setMargins(0, 0, dp(6), 0);
        quickRow.addView(toneBtn, tblp);
        toneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PlayerService.class);
                i.setAction(PlayerService.ACTION_TEST_TONE);
                startService(i);
                toast("正在播放 1kHz 测试音（1 秒）");
            }
        });
        Button diagBtn = new Button(this);
        diagBtn.setText("🔍 诊断");
        diagBtn.setTextSize(14);
        diagBtn.setTypeface(Typeface.DEFAULT_BOLD);
        GradientDrawable qb2 = new GradientDrawable();
        qb2.setColor(0x33FFFFFF);
        qb2.setCornerRadius(dp(16));
        diagBtn.setBackground(qb2);
        diagBtn.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams dblp = new LinearLayout.LayoutParams(0, dp(40), 1f);
        dblp.setMargins(dp(6), 0, 0, 0);
        quickRow.addView(diagBtn, dblp);
        diagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDiag();
            }
        });
        heroCard.addView(quickRow);
        root.addView(heroCard);

        // ===== 实时统计网格（连接后显示） =====
        LinearLayout statsCard = card();
        statsCard.addView(cardTitle("📊 实时统计"));
        statsGrid = new LinearLayout(this);
        statsGrid.setOrientation(LinearLayout.VERTICAL);
        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        stRate = statValue(row1, "采样率");
        stKbps = statValue(row1, "码率");
        stBuf = statValue(row1, "缓冲");
        statsGrid.addView(row1);
        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setPadding(0, dp(8), 0, 0);
        stLat = statValue(row2, "延迟");
        stJit = statValue(row2, "抖动");
        stRx = statValue(row2, "接收");
        statsGrid.addView(row2);
        statsCard.addView(statsGrid);
        statsDetail = new TextView(this);
        statsDetail.setTextSize(11);
        statsDetail.setTextColor(c[4]);
        statsDetail.setPadding(0, dp(8), 0, 0);
        statsCard.addView(statsDetail);
        statsCard.setVisibility(View.GONE);
        root.addView(statsCard);

        // ===== 连接配置卡片 =====
        LinearLayout cfgCard = card();
        cfgCard.addView(cardTitle("📡 连接配置"));

        cfgCard.addView(label("附近设备（自动发现，点击填入）"));
        nearbyView = new TextView(this);
        nearbyView.setText("扫描中...");
        nearbyView.setTextSize(12);
        nearbyView.setTextColor(c[5]);
        nearbyView.setPadding(0, 0, 0, dp(6));
        cfgCard.addView(nearbyView);

        // IP + 端口（一行）
        cfgCard.addView(label("发送端地址"));
        LinearLayout rIp = new LinearLayout(this);
        rIp.setOrientation(LinearLayout.HORIZONTAL);
        ipInput = input("IP 或域名（如 192.168.1.100）", -1);
        ipInput.setLayoutParams(new LinearLayout.LayoutParams(0, dp(44), 1f));
        rIp.addView(ipInput);
        portInput = input("端口", InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams portLp = new LinearLayout.LayoutParams(dp(100), dp(44));
        portLp.setMargins(dp(8), 0, 0, 0);
        portInput.setLayoutParams(portLp);
        rIp.addView(portInput);
        cfgCard.addView(rIp);

        // 传输 + 输出（一行）
        cfgCard.addView(label("传输方式 / 输出"));
        LinearLayout rTo = new LinearLayout(this);
        rTo.setOrientation(LinearLayout.HORIZONTAL);
        transportSpinner = spinner(new String[]{"自动", "TCP 稳定", "UDP 低延迟"});
        transportSpinner.setLayoutParams(new LinearLayout.LayoutParams(0, dp(44), 1f));
        rTo.addView(transportSpinner);
        outputSpinner = spinner(new String[]{"AudioTrack", "AAudio 低延迟"});
        LinearLayout.LayoutParams osp = new LinearLayout.LayoutParams(0, dp(44), 1f);
        osp.setMargins(dp(8), 0, 0, 0);
        outputSpinner.setLayoutParams(osp);
        rTo.addView(outputSpinner);
        cfgCard.addView(rTo);

        // 解码方式 + 解码器（一行）
        cfgCard.addView(label("解码方式 / 解码器"));
        LinearLayout rDec = new LinearLayout(this);
        rDec.setOrientation(LinearLayout.HORIZONTAL);
        decodeModeSpinner = spinner(new String[]{"自动（硬解+软解兜底）", "仅硬解", "仅软解"});
        decodeModeSpinner.setLayoutParams(new LinearLayout.LayoutParams(0, dp(44), 1f));
        rDec.addView(decodeModeSpinner);
        decoderSpinner = spinner(new String[]{"自动选择"});
        LinearLayout.LayoutParams dsp = new LinearLayout.LayoutParams(0, dp(44), 1f);
        dsp.setMargins(dp(8), 0, 0, 0);
        decoderSpinner.setLayoutParams(dsp);
        rDec.addView(decoderSpinner);
        cfgCard.addView(rDec);
        refreshDecoderList();

        // 缓冲档位 + 自定义毫秒（一行）
        cfgCard.addView(label("缓冲档位"));
        LinearLayout rBuf = new LinearLayout(this);
        rBuf.setOrientation(LinearLayout.HORIZONTAL);
        rBuf.setGravity(Gravity.CENTER_VERTICAL);
        bufferSpinner = spinner(new String[]{"跟随模式", "低延迟（游戏）", "高音质（稳定）", "自定义（毫秒）"});
        bufferSpinner.setLayoutParams(new LinearLayout.LayoutParams(0, dp(44), 1f));
        rBuf.addView(bufferSpinner);
        bufferMsInput = input("20~500ms", InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(dp(110), dp(44));
        blp.setMargins(dp(8), 0, 0, 0);
        bufferMsInput.setLayoutParams(blp);
        bufferMsInput.setVisibility(View.GONE);
        rBuf.addView(bufferMsInput);
        cfgCard.addView(rBuf);
        bufferSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean custom = position == 3;
                bufferMsInput.setVisibility(custom ? View.VISIBLE : View.GONE);
                if (custom) statusView.setText("自定义缓冲：越小延迟越低，太小可能卡顿");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 音量 + 自动重连（一行）
        LinearLayout rVol = new LinearLayout(this);
        rVol.setOrientation(LinearLayout.HORIZONTAL);
        rVol.setGravity(Gravity.CENTER_VERTICAL);
        rVol.setPadding(0, dp(10), 0, 0);
        TextView volLabel = new TextView(this);
        volLabel.setText("音量");
        volLabel.setTextSize(13);
        volLabel.setTextColor(c[3]);
        rVol.addView(volLabel);
        volumeBar = new android.widget.SeekBar(this);
        volumeBar.setMax(100);
        volumeBar.setProgress(100);
        volumeBar.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        rVol.addView(volumeBar);
        autoReconnectSwitch = new Switch(this);
        rVol.addView(autoReconnectSwitch);
        cfgCard.addView(rVol);
        TextView recHint = new TextView(this);
        recHint.setText("右侧开关 = 断线自动重连");
        recHint.setTextSize(10);
        recHint.setTextColor(c[4]);
        recHint.setPadding(0, 0, 0, dp(4));
        cfgCard.addView(recHint);
        volumeBar.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) sendVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
            }
        });

        // 连接大按钮
        connectBtn = button("连 接", 0);
        connectBtn.setTextSize(17);
        LinearLayout.LayoutParams cblp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(50));
        cblp.setMargins(0, dp(14), 0, 0);
        cfgCard.addView(connectBtn, cblp);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!connected) {
                    connect();
                } else {
                    disconnect();
                }
            }
        });
        root.addView(cfgCard);

        // ===== 预设卡片 =====
        LinearLayout preCard = card();
        preCard.addView(cardTitle("🗂️ 连接预设"));

        preCard.addView(label("选择预设（选中即自动加载）"));
        LinearLayout presetRow = new LinearLayout(this);
        presetRow.setOrientation(LinearLayout.HORIZONTAL);
        presetRow.setGravity(Gravity.CENTER_VERTICAL);
        presetSpinner = new Spinner(this);
        presetSpinner.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        presetRow.addView(presetSpinner);

        Button delBtn = button("删除", 1);
        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dp(42));
        dlp.setMargins(dp(8), 0, 0, 0);
        presetRow.addView(delBtn, dlp);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedPreset();
            }
        });
        preCard.addView(presetRow);

        preCard.addView(label("保存当前连接为预设"));
        LinearLayout saveRow = new LinearLayout(this);
        saveRow.setOrientation(LinearLayout.HORIZONTAL);
        saveRow.setGravity(Gravity.CENTER_VERTICAL);
        presetNameInput = input("预设名（如：客厅平板）", -1);
        presetNameInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        saveRow.addView(presetNameInput);

        Button saveBtn = button("保存", 0);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dp(42));
        slp.setMargins(dp(8), 0, 0, 0);
        saveRow.addView(saveBtn, slp);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentAsPreset();
            }
        });
        preCard.addView(saveRow);
        root.addView(preCard);

        // ===== 诊断与日志卡片 =====
        LinearLayout logCard = card();
        logCard.addView(cardTitle("🩺 诊断与日志"));

        diagView = new TextView(this);
        diagView.setTextSize(12);
        diagView.setTextColor(c[3]);
        diagView.setPadding(0, 0, 0, dp(6));
        diagView.setVisibility(View.GONE);
        logCard.addView(diagView);

        logView = new TextView(this);
        logView.setTextSize(10);
        logView.setTextColor(c[4]);
        logView.setTextIsSelectable(true);
        logView.setPadding(0, dp(4), 0, 0);
        logView.setVisibility(View.GONE);
        logCard.addView(logView);

        File logDir = getExternalFilesDir(null);
        if (logDir != null) {
            final String logPath = new File(logDir, "wifiaudio.log").getAbsolutePath();
            TextView logHint = new TextView(this);
            logHint.setText("📄 完整日志: " + logPath + "\n（长按日志可复制）");
            logHint.setTextSize(10);
            logHint.setTextColor(c[4]);
            logHint.setPadding(0, dp(6), 0, 0);
            logCard.addView(logHint);
        }
        root.addView(logCard);

        // ===== 当前设备解码器列表 =====
        TextView devTitle = new TextView(this);
        devTitle.setText("📋 当前设备解码器");
        devTitle.setTextSize(14);
        devTitle.setTypeface(Typeface.DEFAULT_BOLD);
        devTitle.setTextColor(c[3]);
        devTitle.setPadding(0, dp(10), 0, dp(4));
        root.addView(devTitle);

        decoderListView = new TextView(this);
        decoderListView.setTextSize(11);
        decoderListView.setTextColor(c[4]);
        decoderListView.setPadding(0, 0, 0, dp(4));
        root.addView(decoderListView);
        refreshDecoderList();

        setContentView(scroll);
    }

    private void updateStats(String transport, String codec, int rate, int channels,
                             int kbps, int audioMs, int jitterMs, int latencyMs, int rxKb, int decFrames,
                             int targetMs, long dropped, long underruns, String output,
                             java.util.ArrayList<String> logs) {
        if (stRate == null) return;
        stRate.setText(rate > 0 ? (rate / 1000.0 >= 1 ? (rate / 1000) + "k" : String.valueOf(rate)) : "—");
        stKbps.setText(kbps > 0 ? kbps + "" : "—");
        stBuf.setText(audioMs + "ms");
        stLat.setText(latencyMs + "ms");
        stJit.setText(jitterMs > 0 ? jitterMs + "ms" : "—");
        stRx.setText(rxKb >= 1024 ? (rxKb / 1024.0 >= 10 ? Math.round(rxKb / 1024.0) + "MB"
                : String.format(java.util.Locale.US, "%.1fMB", rxKb / 1024.0)) : rxKb + "KB");
        if (statsDetail != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("协议 ").append(transport == null ? "-" : transport);
            if (output != null && !output.isEmpty()) sb.append("  ·  输出 ").append(output);
            sb.append("  ·  ").append(codec == null ? "-" : codec);
            if ("PCM".equals(codec)) sb.append("（实测）");
            if (targetMs > 0) sb.append("  ·  自适应目标 ").append(targetMs).append("ms");
            if (dropped > 0 || underruns > 0) {
                sb.append("  ·  跳帧 ").append(dropped).append(" 下溢 ").append(underruns);
            }
            statsDetail.setText(sb.toString());
        }
        // 日志
        if (logView != null && logs != null && !logs.isEmpty()) {
            StringBuilder lb = new StringBuilder();
            for (String l : logs) lb.append(l).append("\n");
            logView.setText(lb.toString());
            logView.setVisibility(View.VISIBLE);
        }
    }

    // ================= 预设逻辑 =================

    private SharedPreferences prefs() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void loadPresets() {
        presets.clear();
        String raw = prefs().getString(KEY_PRESETS, "[]");
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                presets.add(arr.getJSONObject(i));
            }
        } catch (Exception e) {
            presets.clear();
        }
        refreshPresetSpinner();
    }

    private void refreshPresetSpinner() {
        List<String> names = new ArrayList<String>();
        names.add("（无预设，点击下方保存）");
        for (JSONObject p : presets) {
            names.add(p.optString("name", "未命名"));
        }
        ArrayAdapter<String> pa = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, names);
        pa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        presetSelectGuard = true;
        presetSpinner.setAdapter(pa);
        presetSelectGuard = false;
        presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (presetSelectGuard) return;
                if (position > 0 && position <= presets.size()) {
                    applyPreset(presets.get(position - 1));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void applyPreset(JSONObject p) {
        ipInput.setText(p.optString("ip", ""));
        portInput.setText(String.valueOf(p.optInt("port", 47800)));
        int t = indexOf(TRANSPORT_VALUES, p.optString("transport", "auto"));
        transportSpinner.setSelection(t >= 0 ? t : 0);
        String buf = p.optString("buffer", "auto");
        if (buf.startsWith("custom:")) {
            bufferSpinner.setSelection(3);
            bufferMsInput.setText(buf.substring(7));
            bufferMsInput.setVisibility(View.VISIBLE);
        } else {
            int b = indexOf(BUFFER_VALUES, buf);
            bufferSpinner.setSelection(b >= 0 ? b : 0);
            bufferMsInput.setVisibility(View.GONE);
        }
        statusView.setText("已加载预设：" + p.optString("name", ""));
    }

    private String bufferValue() {
        if (bufferSpinner.getSelectedItemPosition() == 3) {
            String ms = bufferMsInput.getText().toString().trim();
            if (ms.isEmpty()) return "custom:60";
            return "custom:" + ms;
        }
        return BUFFER_VALUES[bufferSpinner.getSelectedItemPosition()];
    }

    private void saveCurrentAsPreset() {
        String name = presetNameInput.getText().toString().trim();
        if (name.isEmpty()) {
            statusView.setText("请输入预设名称");
            return;
        }
        String ip = ipInput.getText().toString().trim();
        int port;
        try {
            port = Integer.parseInt(portInput.getText().toString().trim());
        } catch (NumberFormatException e) {
            port = 47800;
        }
        try {
            JSONObject p = new JSONObject();
            p.put("name", name);
            p.put("ip", ip);
            p.put("port", port);
            p.put("transport", TRANSPORT_VALUES[transportSpinner.getSelectedItemPosition()]);
            p.put("buffer", bufferValue());
            for (int i = 0; i < presets.size(); i++) {
                if (name.equals(presets.get(i).optString("name"))) {
                    presets.set(i, p);
                    savePresets();
                    presetNameInput.setText("");
                    loadPresets();
                    statusView.setText("已更新预设：" + name);
                    return;
                }
            }
            presets.add(p);
            savePresets();
            presetNameInput.setText("");
            loadPresets();
            statusView.setText("已保存预设：" + name);
        } catch (Exception e) {
            statusView.setText("保存失败");
        }
    }

    private void deleteSelectedPreset() {
        int pos = presetSpinner.getSelectedItemPosition();
        if (pos <= 0 || pos > presets.size()) {
            statusView.setText("请先选择要删除的预设");
            return;
        }
        presets.remove(pos - 1);
        savePresets();
        loadPresets();
        statusView.setText("已删除预设");
    }

    private void savePresets() {
        JSONArray arr = new JSONArray();
        for (JSONObject p : presets) {
            arr.put(p);
        }
        prefs().edit().putString(KEY_PRESETS, arr.toString()).apply();
    }

    private void restoreLastConnect() {
        String raw = prefs().getString(KEY_LAST, "");
        if (raw.isEmpty()) return;
        try {
            applyPreset(new JSONObject(raw));
        } catch (Exception ignored) {
        }
    }

    private void rememberCurrent() {
        try {
            JSONObject p = new JSONObject();
            p.put("name", "最近连接");
            p.put("ip", ipInput.getText().toString().trim());
            int port;
            try {
                port = Integer.parseInt(portInput.getText().toString().trim());
            } catch (NumberFormatException e) {
                port = 47800;
            }
            p.put("port", port);
            p.put("transport", TRANSPORT_VALUES[transportSpinner.getSelectedItemPosition()]);
            p.put("buffer", bufferValue());
            prefs().edit().putString(KEY_LAST, p.toString()).apply();
        } catch (Exception ignored) {
        }
    }

    private static int indexOf(String[] arr, String v) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(v)) return i;
        }
        return -1;
    }

    // ================= 连接 =================

    private java.util.List<String> decoderChoices = new java.util.ArrayList<String>();
    private java.util.Map<String, String> decoderNames = new java.util.LinkedHashMap<String, String>();

    /** 自动检测设备支持的 OPUS/AAC 解码器 */
    private void refreshDecoderList() {
        decoderChoices.clear();
        decoderNames.clear();
        decoderChoices.add("自动选择");
        StringBuilder devList = new StringBuilder();
        java.util.List<String> opusList = new java.util.ArrayList<String>();
        java.util.List<String> aacList = new java.util.ArrayList<String>();
        try {
            android.media.MediaCodecList mcl = new android.media.MediaCodecList(android.media.MediaCodecList.ALL_CODECS);
            for (android.media.MediaCodecInfo info : mcl.getCodecInfos()) {
                if (info.isEncoder()) continue;
                for (String t : info.getSupportedTypes()) {
                    if ("audio/opus".equals(t)) {
                        String display = "[OPUS] " + info.getName();
                        decoderNames.put(display, info.getName());
                        decoderChoices.add(display);
                        opusList.add(info.getName());
                        break;
                    } else if ("audio/mp4a-latm".equals(t)) {
                        String display = "[AAC] " + info.getName();
                        decoderNames.put(display, info.getName());
                        decoderChoices.add(display);
                        aacList.add(info.getName());
                        break;
                    }
                }
            }
        } catch (Throwable t) {
            PlayerService.logStatic("解码器自动检测失败: " + t);
        }
        // 底部列表
        if (decoderListView != null) {
            devList.append("OPUS 解码器:\n");
            if (opusList.isEmpty()) devList.append("  （无）\n");
            for (String n : opusList) devList.append("  · ").append(n).append("\n");
            devList.append("AAC 解码器:\n");
            if (aacList.isEmpty()) devList.append("  （无）\n");
            for (String n : aacList) devList.append("  · ").append(n).append("\n");
            decoderListView.setText(devList.toString().trim());
        }
        if (decoderChoices.size() <= 1) {
            PlayerService.logStatic("未检测到 OPUS/AAC 解码器（仅软解可用）");
        }
        ArrayAdapter<String> a = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, decoderChoices);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        decoderSpinner.setAdapter(a);
    }

    /** 局域网自动发现发送端（UDP 广播监听） */
    private void startDiscovery() {
        discoverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buf = new byte[256];
                try {
                    java.net.DatagramSocket socket = new java.net.DatagramSocket(47802);
                    socket.setSoTimeout(3000);
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            java.net.DatagramPacket pkt = new java.net.DatagramPacket(buf, buf.length);
                            socket.receive(pkt);
                            String msg = new String(pkt.getData(), pkt.getOffset(), pkt.getLength());
                            if (msg.startsWith("WFAU-DISCOVER|")) {
                                String[] parts = msg.split("\\|");
                                if (parts.length >= 3) {
                                    final String devIp = parts[1];
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            nearbyDevices.add(devIp);
                                            updateNearbyView();
                                        }
                                    });
                                }
                            }
                        } catch (java.net.SocketTimeoutException ignored) {
                        } catch (java.io.IOException e) {
                            break;
                        }
                    }
                    socket.close();
                } catch (Exception ignored) {
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (nearbyDevices.isEmpty()) {
                            nearbyView.setText("（未发现发送端，请确保发送端已启用广播）");
                        }
                    }
                });
            }
        }, "discover");
        discoverThread.start();
    }

    private void updateNearbyView() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String dev : nearbyDevices) {
            if (i++ > 0) sb.append("  ");
            sb.append(dev);
        }
        nearbyView.setText(sb.toString());
        nearbyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击填入第一个发现的 IP
                if (!nearbyDevices.isEmpty()) {
                    String first = nearbyDevices.iterator().next();
                    ipInput.setText(first);
                    toast("已填入: " + first);
                }
            }
        });
    }

    /** 实时调整音量（广播给播放服务） */
    private void sendVolume(int vol) {
        android.content.Intent i = new android.content.Intent(PlayerService.ACTION_VOLUME);
        i.putExtra(PlayerService.EXTRA_VOLUME, vol);
        sendBroadcast(i);
    }

    private void connect() {
        String ip = ipInput.getText().toString().trim();
        int port;
        try {
            port = Integer.parseInt(portInput.getText().toString().trim());
        } catch (NumberFormatException e) {
            port = 47800;
        }
        if (ip.isEmpty()) {
            statusView.setText("请填写发送端 IP 地址");
            return;
        }
        String transport = TRANSPORT_VALUES[transportSpinner.getSelectedItemPosition()];
        String buffer = bufferValue();

        Intent i = new Intent(this, PlayerService.class);
        i.setAction(PlayerService.ACTION_CONNECT);
        i.putExtra(PlayerService.EXTRA_IP, ip);
        i.putExtra(PlayerService.EXTRA_PORT, port);
        i.putExtra(PlayerService.EXTRA_TRANSPORT, transport);
        i.putExtra(PlayerService.EXTRA_BUFFER, buffer);
        i.putExtra(PlayerService.EXTRA_AAUDIO, outputSpinner.getSelectedItemPosition() == 1 ? "1" : "0");
        i.putExtra(PlayerService.EXTRA_VOLUME, volumeBar.getProgress());
        i.putExtra(PlayerService.EXTRA_AUTORECONNECT, autoReconnectSwitch.isChecked() ? "1" : "0");
        i.putExtra(PlayerService.EXTRA_DECODE_MODE,
                new String[]{"auto", "hard", "soft"}[decodeModeSpinner.getSelectedItemPosition()]);
        String dName = "";
        if (decoderSpinner.getSelectedItemPosition() > 0) {
            String display = decoderChoices.get(decoderSpinner.getSelectedItemPosition());
            dName = decoderNames.get(display); // 显示[OPUS]xxx -> 真实名
            if (dName == null) dName = "";
        }
        i.putExtra(PlayerService.EXTRA_DECODER_NAME, dName);
        startForegroundService(i);
        rememberCurrent();
        setConnectedUi(true, "正在连接 " + ip + ":" + port + " …");
    }

    private void disconnect() {
        Intent i = new Intent(this, PlayerService.class);
        i.setAction(PlayerService.ACTION_STOP);
        startService(i);
        setConnectedUi(false, "已断开");
    }

    private void onPlayerState(String state, String msg, String codec, int rate) {
        boolean isPlaying = "playing".equals(state);
        boolean isConnecting = "connecting".equals(state);
        boolean isStopped = "stopped".equals(state);
        boolean isError = "error".equals(state);
        if (isPlaying) {
            setConnectedUi(true, msg != null ? msg : "正在播放");
        } else if (isConnecting) {
            setConnectedUi(true, msg != null ? msg : "连接中…");
        } else if (isError) {
            setConnectedUi(false, msg != null ? msg : "发生错误");
        } else if (isStopped) {
            setConnectedUi(false, msg != null ? msg : "已停止");
        }
    }

    private void setConnectedUi(boolean connected, String statusText) {
        this.connected = connected;
        connectBtn.setText(connected ? "断 开" : "连 接");
        statusView.setText(statusText);
        if (heroCard != null) {
            boolean playing = connected && statusText != null
                    && (statusText.contains("播放") || statusText.contains("直通") || statusText.contains("缓冲"));
            int col = !connected ? 0xFF3482FF : (playing ? 0xFF2FA85C : 0xFFE6A23C);
            ((GradientDrawable) heroCard.getBackground()).setColor(col);
            heroDot.setTextColor(0xFFFFFFFF);
            heroStatus.setText(!connected ? "未连接" : (playing ? "播放中" : "连接中"));
        }
        if (statsGrid != null) {
            statsGrid.setVisibility(connected ? View.VISIBLE : View.GONE);
        }
        if (!connected && diagView != null) {
            diagView.setVisibility(View.GONE);
        }
    }

    // ================= 诊断 =================

    private void runDiag() {
        final String ip = ipInput.getText().toString().trim();
        int port;
        try {
            port = Integer.parseInt(portInput.getText().toString().trim());
        } catch (NumberFormatException e) {
            port = 47800;
        }
        final int httpPort = port + 1; // 约定 HTTP 端口 = TCP 端口 + 1
        diagView.setVisibility(View.VISIBLE);
        diagView.setText("正在查询服务端 " + ip + ":" + httpPort + " ...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String result = queryStatus(ip, httpPort);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        diagView.setText(result);
                        diagView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    /** 查询服务端 HTTP /status */
    private String queryStatus(String ip, int httpPort) {
        Socket s = null;
        try {
            s = new Socket(ip, httpPort);
            s.setSoTimeout(3000);
            OutputStream out = s.getOutputStream();
            out.write(("GET /status HTTP/1.0\r\nHost: " + ip + "\r\n\r\n").getBytes("UTF-8"));
            out.flush();
            InputStream in = s.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) > 0) baos.write(buf, 0, n);
            String resp = new String(baos.toByteArray(), "UTF-8");
            int idx = resp.indexOf("\r\n\r\n");
            String body = idx >= 0 ? resp.substring(idx + 4) : resp;
            JSONObject j = new JSONObject(body);
            StringBuilder sb = new StringBuilder();
            sb.append("── 服务端状态 ──\n");
            sb.append("发送端 IP: ").append(j.optString("ip", "-")).append("\n");
            sb.append("运行: ").append(j.optBoolean("running") ? "✅ 是" : "❌ 否").append("\n");
            sb.append("实际编码: ").append(j.optString("codec", "-").toUpperCase()).append("\n");
            sb.append("编码器输出: ").append(j.optBoolean("encoderActive") ? "✅ " + j.optLong("encoderFrames", 0) + " 帧" : "❌ 无输出!").append("\n");
            sb.append("模式: ").append(j.optString("mode", "-")).append(" / ").append(j.optString("transport", "-").toUpperCase()).append("\n");
            sb.append("已连接设备: ").append(j.optInt("clients", 0)).append(" 个");
            String list = j.optString("clientsList", "");
            if (!list.isEmpty()) sb.append(" (").append(list).append(")");
            sb.append("\n");
            sb.append("错误: ").append(j.optString("lastError", "无"));
            return sb.toString();
        } catch (Exception e) {
            return "无法查询服务端状态: " + e.getMessage() + "\n提示: App 假设 HTTP 端口 = TCP端口 + 1，若发送端修改过 HTTP 端口请告知";
        } finally {
            try {
                if (s != null) s.close();
            } catch (Exception ignored) {
            }
        }
    }

    private int dp(int v) {
        return Math.round(getResources().getDisplayMetrics().density * v);
    }

    private void toast(String msg) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (discoverThread != null) {
            discoverThread.interrupt();
            discoverThread = null;
        }
        PlayerService.setStateListener(null);
        try {
            unregisterReceiver(stateReceiver);
        } catch (Throwable ignored) {
        }
        try {
            unregisterReceiver(statsReceiver);
        } catch (Throwable ignored) {
        }
    }
}
