package com.wifiaudio.manager;

import android.app.Activity;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * WiFiAudio 管理 App —— 发送端独立配置工具（root 直连）。
 * 通过 root 直连，base64 传输，彻底绕开 WebView/exec 兼容问题。
 */
public class MainActivity extends Activity {

    // M3 动态配色（Android 12+ 跟随系统主题色；低版本 fallback）
    private static final int[] ACCENTS = {0xFF4D9FFF, 0xFF34C759, 0xFFAF52DE, 0xFFFF9F0A, 0xFFFF453A};
    private static final String[] ACCENT_NAMES = {"蓝", "绿", "紫", "橙", "红"};
    private int[] c;
    private GradientDrawable bg(int color, float radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(radius);
        return g;
    }

    private int sysColor(int resId, int fallback) {
        if (android.os.Build.VERSION.SDK_INT >= 31) {
            try {
                return getColor(resId);
            } catch (Throwable ignored) {
            }
        }
        return fallback;
    }

    private Switch swEnabled;
    private Spinner modeSpinner;
    private Spinner transportSpinner;
    private Spinner codecSpinner;
    private Spinner pcmRateSpinner;
    private EditText opusRateInput;
    private EditText aacRateInput;
    private Spinner renderSpinner;
    private Spinner callSpinner;
    private EditText tcpPortInput;
    private EditText httpPortInput;

    private Spinner presetSpinner;
    private EditText presetNameInput;
    private TextView statusView;
    private TextView statusBadge;
    private TextView stIp, stCodec, stMode, stPort, stClients, stFrames, stRate, stBitrate;
    private TextView stErrView;
    private TextView logView;

    private JSONObject config = new JSONObject();
    private List<String> sysPresets = new ArrayList<String>();
    private List<String> userPresets = new ArrayList<String>();
    private Thread statusThread;
    private volatile boolean alive = true;
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 隐藏 ActionBar（避免与自绘标题重复/挤压）
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        boolean dark = (getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        if (dark) {
            c = new int[]{
                    sysColor(android.R.color.system_neutral1_1000, 0xFF242424),
                    sysColor(android.R.color.system_neutral1_900, 0xFF1E1E1E),
                    sysColor(android.R.color.system_neutral1_800, 0xFF434343),
                    sysColor(android.R.color.system_neutral1_100, 0xFFF2F2F2),
                    sysColor(android.R.color.system_neutral1_500, 0xFF787E96),
                    sysColor(android.R.color.system_accent1_400, 0xFF277AF7)};
        } else {
            c = new int[]{
                    sysColor(android.R.color.system_neutral1_10, 0xFFF7F7F7),
                    sysColor(android.R.color.system_neutral1_0, 0xFFFFFFFF),
                    sysColor(android.R.color.system_neutral1_50, 0xFFF0F0F0),
                    sysColor(android.R.color.system_neutral1_900, 0xFF000000),
                    sysColor(android.R.color.system_neutral1_500, 0xFF8C93B0),
                    sysColor(android.R.color.system_accent1_500, 0xFF3482FF)};
        }
        buildUi();
        loadConfig();
        refreshPresets();
        refreshStatus();
        startStatusThread();
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(c[0]);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(20), dp(16), dp(24));
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("🎛️ WiFiAudio 管理器");
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(c[3]);
        root.addView(title);
        TextView sub = new TextView(this);
        sub.setText("root 直连配置发送端");
        sub.setTextSize(12);
        sub.setTextColor(c[4]);
        sub.setPadding(0, 0, 0, dp(10));
        root.addView(sub);

        // ===== 状态卡片（美化）=====
        LinearLayout st = card();
        st.addView(cardTitle("📊 状态"));

        // 徽章
        statusBadge = new TextView(this);
        statusBadge.setText("○ 未运行");
        statusBadge.setTextSize(15);
        statusBadge.setTypeface(Typeface.DEFAULT_BOLD);
        statusBadge.setTextColor(c[3]);
        statusBadge.setPadding(dp(10), dp(6), dp(10), dp(6));
        statusBadge.setGravity(Gravity.CENTER);
        statusBadge.setBackground(bg(0xFF8C93B0, dp(10)));
        st.addView(statusBadge, fullLp(dp(40), 0));

        // 指标网格（2 列）
        LinearLayout grid = new LinearLayout(this);
        grid.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams gridLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gridLp.setMargins(0, dp(8), 0, 0);
        st.addView(grid, gridLp);

        LinearLayout col1 = new LinearLayout(this);
        col1.setOrientation(LinearLayout.VERTICAL);
        col1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        grid.addView(col1);
        LinearLayout col2 = new LinearLayout(this);
        col2.setOrientation(LinearLayout.VERTICAL);
        col2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        grid.addView(col2);

        stIp = statCell(col1, "IP");
        stCodec = statCell(col1, "编码");
        stRate = statCell(col1, "采样率");
        stMode = statCell(col1, "模式/传输");
        stPort = statCell(col2, "端口");
        stClients = statCell(col2, "连接数");
        stFrames = statCell(col2, "编码输出");
        stBitrate = statCell(col2, "比特率");

        stErrView = new TextView(this);
        stErrView.setTextSize(11);
        stErrView.setTextColor(0xFFF12522);
        stErrView.setPadding(0, dp(6), 0, 0);
        st.addView(stErrView);
        Button refreshBtn = btn("🔄 刷新状态", 1);
        st.addView(refreshBtn, fullLp(dp(40), dp(8)));
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshStatus();
            }
        });
        root.addView(st);

        // ===== 主开关 =====
        LinearLayout swCard = card();
        swCard.addView(cardTitle("⚡ 启用广播"));
        LinearLayout swRow = new LinearLayout(this);
        swRow.setOrientation(LinearLayout.HORIZONTAL);
        swRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView swLabel = new TextView(this);
        swLabel.setText("启用 WiFi 音频广播（默认关闭省电）");
        swLabel.setTextSize(14);
        swLabel.setTextColor(c[3]);
        swLabel.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        swRow.addView(swLabel);
        swEnabled = new Switch(this);
        swRow.addView(swEnabled);
        swCard.addView(swRow);
        swEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton b, boolean checked) {
                if (loading) return; // 加载中不自动保存
                try {
                    config.put("enabled", checked ? 1 : 0);
                } catch (Exception ignored) {
                }
                saveConfig();
                String r = RootShell.service(checked ? "start" : "stop");
                toast(r);
                refreshStatus();
            }
        });
        root.addView(swCard);

        // ===== 预设卡片 =====
        LinearLayout pre = card();
        pre.addView(cardTitle("🎛️ 预设"));
        LinearLayout sysRow = new LinearLayout(this);
        sysRow.setOrientation(LinearLayout.HORIZONTAL);
        Button pMusic = btn("🎵 音乐", 1);
        Button pGame = btn("🎮 游戏", 1);
        Button pWeak = btn("🗜️ 弱网", 1);
        Button pMovie = btn("📺 观影", 1);
        sysRow.addView(pMusic, halfLp());
        sysRow.addView(pGame, halfLp());
        pre.addView(sysRow);
        LinearLayout sysRow2 = new LinearLayout(this);
        sysRow2.setOrientation(LinearLayout.HORIZONTAL);
        sysRow2.addView(pWeak, halfLp());
        sysRow2.addView(pMovie, halfLp());
        pre.addView(sysRow2);
        pMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyPresetFile(RootShell.MODDIR + "/presets/music.conf");
            }
        });
        pGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyPresetFile(RootShell.MODDIR + "/presets/game.conf");
            }
        });
        pWeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyPresetFile(RootShell.MODDIR + "/presets/weak.conf");
            }
        });
        pMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyPresetFile(RootShell.MODDIR + "/presets/movie.conf");
            }
        });

        pre.addView(label("自定义预设"));
        presetSpinner = new Spinner(this);
        pre.addView(presetSpinner);
        LinearLayout preRow = new LinearLayout(this);
        preRow.setOrientation(LinearLayout.HORIZONTAL);
        preRow.setGravity(Gravity.CENTER_VERTICAL);
        presetNameInput = new EditText(this);
        presetNameInput.setHint("预设名");
        presetNameInput.setSingleLine(true);
        presetNameInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        preRow.addView(presetNameInput);
        Button savePre = btn("保存", 0);
        Button delPre = btn("删除", 1);
        preRow.addView(savePre, wrapLp());
        preRow.addView(delPre, wrapLp());
        pre.addView(preRow);
        savePre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserPreset();
            }
        });
        delPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserPreset();
            }
        });
        Button applyPre = btn("应用所选预设", 1);
        pre.addView(applyPre, fullLp(dp(40), dp(6)));
        applyPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applySelectedUserPreset();
            }
        });
        root.addView(pre);

        // ===== 音频设置 =====
        LinearLayout audio = card();
        audio.addView(cardTitle("🔊 音频设置"));
        audio.addView(label("编码方式"));
        codecSpinner = spinner(new String[]{"PCM（无损，延迟最低）", "OPUS（高质量压缩）", "AAC（兼容压缩）"});
        audio.addView(codecSpinner);
        audio.addView(label("PCM 采样率（仅 PCM 模式；自动=跟随设备 Hi-Res 能力）"));
        pcmRateSpinner = spinner(new String[]{"自动（跟随设备）", "48 kHz", "96 kHz", "192 kHz"});
        audio.addView(pcmRateSpinner);
        audio.addView(label("OPUS 码率 kbps"));
        opusRateInput = input("128");
        audio.addView(opusRateInput);
        audio.addView(label("AAC 码率 kbps"));
        aacRateInput = input("192");
        audio.addView(aacRateInput);
        audio.addView(label("本地声音"));
        renderSpinner = spinner(new String[]{"本地静音（声音只走WiFi）", "本地照常出声"});
        audio.addView(renderSpinner);
        audio.addView(label("蜂窝通话捕获（尽力而为）"));
        callSpinner = spinner(new String[]{"关闭", "开启"});
        audio.addView(callSpinner);
        root.addView(audio);

        // ===== 模式/网络 =====
        LinearLayout net = card();
        net.addView(cardTitle("🌐 模式与网络"));
        net.addView(label("模式"));
        modeSpinner = spinner(new String[]{"🎵 音乐（稳定）", "🎮 游戏（低延迟）"});
        net.addView(modeSpinner);
        net.addView(label("传输协议"));
        transportSpinner = spinner(new String[]{"TCP（稳定）", "UDP（低延迟）"});
        net.addView(transportSpinner);
        net.addView(label("TCP 端口"));
        tcpPortInput = input("47800");
        net.addView(tcpPortInput);
        net.addView(label("HTTP 端口"));
        httpPortInput = input("47801");
        net.addView(httpPortInput);
        root.addView(net);

        // ===== 日志 =====
        LinearLayout lg = card();
        lg.addView(cardTitle("📄 服务日志"));
        logView = new TextView(this);
        logView.setTextSize(11);
        logView.setTextColor(0xFF8FA3C0);
        logView.setTextIsSelectable(true);
        logView.setPadding(0, dp(4), 0, dp(6));
        lg.addView(logView);
        Button lgBtn = btn("🔄 刷新日志", 1);
        lg.addView(lgBtn, fullLp(dp(40), 0));
        lgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLog();
            }
        });
        root.addView(lg);

        // ===== 保存 + 自检 =====
        Button saveBtn = btn("💾 保存并应用", 0);
        root.addView(saveBtn, fullLp(dp(48), dp(8)));
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfig();
                String r = RootShell.service("restart");
                toast(r);
                refreshStatus();
            }
        });
        Button diagBtn = btn("🔍 自检", 1);
        root.addView(diagBtn, fullLp(dp(44), dp(8)));
        diagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diag();
            }
        });

        setContentView(scroll);
    }

    // ===== 配置读写 =====

    private void loadConfig() {
        loading = true; // 防止加载时 setChecked 触发自动保存（覆盖用户配置）
        String json = RootShell.readConfig();
        if (json == null || json.trim().isEmpty()) {
            statusView.setText("⚠ 无法读取配置（root 授权了吗？）");
            return;
        }
        try {
            config = new JSONObject(json);
        } catch (Exception e) {
            config = new JSONObject();
            statusView.setText("⚠ 配置解析失败: " + e.getMessage());
            return;
        }
        swEnabled.setChecked(config.optInt("enabled", 0) == 1);
        modeSpinner.setSelection("game".equals(config.optString("mode")) ? 1 : 0);
        transportSpinner.setSelection("udp".equals(config.optString("transport")) ? 1 : 0);
        String codec = config.optString("codec", "pcm");
        codecSpinner.setSelection("opus".equals(codec) ? 1 : "aac".equals(codec) ? 2 : 0);
        String pcmRate = config.optString("pcm_rate", "auto");
        pcmRateSpinner.setSelection("192000".equals(pcmRate) ? 3 : "96000".equals(pcmRate) ? 2 : "48000".equals(pcmRate) ? 1 : 0);
        opusRateInput.setText(String.valueOf(config.optInt("opus_bitrate", 128)));
        aacRateInput.setText(String.valueOf(config.optInt("aac_bitrate", 192)));
        renderSpinner.setSelection(config.optInt("local_render", 0) == 1 ? 1 : 0);
        callSpinner.setSelection(config.optInt("capture_call", 0) == 1 ? 1 : 0);
        tcpPortInput.setText(String.valueOf(config.optInt("tcp_port", 47800)));
        httpPortInput.setText(String.valueOf(config.optInt("http_port", 47801)));
        loading = false;
    }

    private void saveConfig() {
        try {
            config.put("enabled", swEnabled.isChecked() ? 1 : 0);
            config.put("mode", modeSpinner.getSelectedItemPosition() == 1 ? "game" : "music");
            config.put("transport", transportSpinner.getSelectedItemPosition() == 1 ? "udp" : "tcp");
            String codec = codecSpinner.getSelectedItemPosition() == 1 ? "opus"
                    : codecSpinner.getSelectedItemPosition() == 2 ? "aac" : "pcm";
            config.put("codec", codec);
            config.put("pcm_rate", pcmRateSpinner.getSelectedItemPosition() == 1 ? "48000"
                    : pcmRateSpinner.getSelectedItemPosition() == 2 ? "96000"
                    : pcmRateSpinner.getSelectedItemPosition() == 3 ? "192000" : "auto");
            config.put("opus_bitrate", parseInt(opusRateInput.getText().toString(), 128));
            config.put("aac_bitrate", parseInt(aacRateInput.getText().toString(), 192));
            config.put("local_render", renderSpinner.getSelectedItemPosition());
            config.put("capture_call", callSpinner.getSelectedItemPosition());
            config.put("tcp_port", parseInt(tcpPortInput.getText().toString(), 47800));
            config.put("http_port", parseInt(httpPortInput.getText().toString(), 47801));
        } catch (Exception ignored) {
        }
        boolean ok = RootShell.writeConfig(config.toString());
        toast(ok ? "配置已保存" : "保存失败（root 或路径问题）");
    }

    // ===== 预设 =====

    private void refreshPresets() {
        sysPresets.clear();
        String sys = RootShell.listDir(RootShell.MODDIR + "/presets");
        if (sys != null && !sys.startsWith("[error]")) {
            for (String s : sys.split("\n")) if (s.trim().endsWith(".conf")) sysPresets.add(s.trim());
        }
        userPresets.clear();
        RootShell.mkdir(RootShell.MODDIR + "/presets_user");
        String usr = RootShell.listDir(RootShell.MODDIR + "/presets_user");
        if (usr != null && !usr.startsWith("[error]")) {
            for (String s : usr.split("\n")) if (s.trim().endsWith(".conf")) userPresets.add(s.trim());
        }
        List<String> names = new ArrayList<String>();
        names.add("（选择自定义预设）");
        for (String f : userPresets) {
            String n = f.replace(".conf", "");
            names.add(n);
        }
        presetSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names));
    }

    private void applyPresetFile(String path) {
        String b64 = RootShell.exec("base64 < " + path + " 2>/dev/null");
        if (b64.startsWith("[error]") || b64.isEmpty()) {
            toast("预设读取失败");
            return;
        }
        try {
            String content = new String(android.util.Base64.decode(b64, android.util.Base64.DEFAULT), "UTF-8");
            JSONObject p = new JSONObject(content);
            // 应用预设到表单（不覆盖 enabled/端口）
            if (p.has("mode")) modeSpinner.setSelection("game".equals(p.optString("mode")) ? 1 : 0);
            if (p.has("transport")) transportSpinner.setSelection("udp".equals(p.optString("transport")) ? 1 : 0);
            if (p.has("codec")) {
                String cc = p.optString("codec");
                codecSpinner.setSelection("opus".equals(cc) ? 1 : "aac".equals(cc) ? 2 : 0);
            }
            if (p.has("pcm_rate")) {
                String pr = p.optString("pcm_rate");
                pcmRateSpinner.setSelection("192000".equals(pr) ? 3 : "96000".equals(pr) ? 2 : "48000".equals(pr) ? 1 : 0);
            }
            if (p.has("opus_bitrate")) opusRateInput.setText(String.valueOf(p.optInt("opus_bitrate")));
            if (p.has("aac_bitrate")) aacRateInput.setText(String.valueOf(p.optInt("aac_bitrate")));
            if (p.has("local_render")) renderSpinner.setSelection(p.optInt("local_render") == 1 ? 1 : 0);
            if (p.has("capture_call")) callSpinner.setSelection(p.optInt("capture_call") == 1 ? 1 : 0);
            toast("已应用预设（点保存并应用生效）");
        } catch (Exception e) {
            toast("预设解析失败");
        }
    }

    private void saveUserPreset() {
        String name = presetNameInput.getText().toString().trim();
        if (name.isEmpty()) {
            toast("请输入预设名");
            return;
        }
        try {
            JSONObject p = new JSONObject();
            p.put("name", name);
            p.put("mode", modeSpinner.getSelectedItemPosition() == 1 ? "game" : "music");
            p.put("transport", transportSpinner.getSelectedItemPosition() == 1 ? "udp" : "tcp");
            String codec = codecSpinner.getSelectedItemPosition() == 1 ? "opus"
                    : codecSpinner.getSelectedItemPosition() == 2 ? "aac" : "pcm";
            p.put("codec", codec);
            p.put("pcm_rate", pcmRateSpinner.getSelectedItemPosition() == 1 ? "48000"
                    : pcmRateSpinner.getSelectedItemPosition() == 2 ? "96000"
                    : pcmRateSpinner.getSelectedItemPosition() == 3 ? "192000" : "auto");
            p.put("opus_bitrate", parseInt(opusRateInput.getText().toString(), 128));
            p.put("aac_bitrate", parseInt(aacRateInput.getText().toString(), 192));
            p.put("local_render", renderSpinner.getSelectedItemPosition());
            p.put("capture_call", callSpinner.getSelectedItemPosition());
            RootShell.mkdir(RootShell.MODDIR + "/presets_user");
            RootShell.writeText(RootShell.MODDIR + "/presets_user/" + name + ".conf", p.toString());
            refreshPresets();
            toast("预设已保存: " + name);
        } catch (Exception e) {
            toast("保存失败");
        }
    }

    private void applySelectedUserPreset() {
        int pos = presetSpinner.getSelectedItemPosition();
        if (pos <= 0 || pos > userPresets.size()) {
            toast("请先选择预设");
            return;
        }
        applyPresetFile(RootShell.MODDIR + "/presets_user/" + userPresets.get(pos - 1));
    }

    private void deleteUserPreset() {
        int pos = presetSpinner.getSelectedItemPosition();
        if (pos <= 0 || pos > userPresets.size()) {
            toast("请先选择预设");
            return;
        }
        RootShell.delete(RootShell.MODDIR + "/presets_user/" + userPresets.get(pos - 1));
        refreshPresets();
        toast("已删除");
    }

    // ===== 状态/日志/自检 =====

    private void startStatusThread() {
        statusThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (alive) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        break;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshStatus();
                        }
                    });
                }
            }
        });
        statusThread.start();
    }

    private void refreshStatus() {
        try {
            JSONObject st = new JSONObject(RootShell.readStatus());
            boolean running = st.optBoolean("running");
            statusBadge.setText(running ? "● 运行中" : "○ 未运行");
            statusBadge.setBackground(bg(running ? 0xFF2FA85C : 0xFF8C93B0, dp(10)));
            statusBadge.setTextColor(Color.WHITE);
            stIp.setText(st.optString("ip", "—"));
            stCodec.setText(st.optString("codec", "—").toUpperCase());
            int sRate = st.optInt("sampleRate", 0);
            stRate.setText(sRate > 0 ? (sRate / 1000) + " kHz" : "—");
            long br = st.optLong("bitrate", 0);
            stBitrate.setText(br >= 1000000 ? String.format("%.1f Mbps", br / 1000000.0)
                    : (br > 0 ? (br / 1000) + " kbps" : "—"));
            stMode.setText(st.optString("mode", "—") + " / " + st.optString("transport", "—").toUpperCase());
            stPort.setText("T" + st.optInt("tcpPort", 47800) + " U" + st.optInt("udpPort", 48800) + " H" + st.optInt("httpPort", 47801));
            stClients.setText(String.valueOf(st.optInt("clients", 0)));
            stFrames.setText(String.valueOf(st.optLong("encoderFrames", 0)));
            String err = st.optString("lastError", "");
            stErrView.setText(err.isEmpty() ? "" : "⚠ " + err);
        } catch (Exception e) {
            statusBadge.setText("○ 状态读取失败");
        }
    }

    private void refreshLog() {
        String log = RootShell.readFileBase64("/storage/emulated/0/WiFiAudio/capture.log");
        if (log != null) {
            String[] lines = log.split("\n");
            StringBuilder sb = new StringBuilder();
            for (int i = Math.max(0, lines.length - 40); i < lines.length; i++) {
                sb.append(lines[i]).append('\n');
            }
            logView.setText(sb.toString().trim());
        } else {
            logView.setText("（无日志，服务未运行）");
        }
    }

    private void diag() {
        StringBuilder sb = new StringBuilder();
        sb.append("◆ root: ").append(RootShell.exec("id -u").trim()).append("\n");
        sb.append("◆ config.json 存在: ").append(RootShell.exec("test -f " + RootShell.CONFIG + " && echo 是 || echo 否").trim()).append("\n");
        String cfg = RootShell.readConfig();
        sb.append("◆ config.json 读取: ").append(cfg != null ? "✓ " + cfg.length() + " 字符" : "✗ 失败").append("\n");
        sb.append("◆ 服务: ").append(RootShell.readStatus()).append("\n");
        sb.append("◆ 目录: ").append(RootShell.listDir(RootShell.MODDIR)).append("\n");
        logView.setText(sb.toString());
        toast("自检完成（结果见日志区）");
    }

    // ===== UI helpers =====

    private TextView statCell(LinearLayout parent, String label) {
        LinearLayout cell = new LinearLayout(this);
        cell.setOrientation(LinearLayout.VERTICAL);
        cell.setBackground(bg(c[2], dp(8)));
        cell.setPadding(dp(8), dp(6), dp(8), dp(6));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, dp(4), dp(6));
        cell.setLayoutParams(lp);
        TextView lab = new TextView(this);
        lab.setText(label);
        lab.setTextSize(10);
        lab.setTextColor(c[4]);
        cell.addView(lab);
        TextView val = new TextView(this);
        val.setText("—");
        val.setTextSize(13);
        val.setTypeface(Typeface.DEFAULT_BOLD);
        val.setTextColor(c[3]);
        cell.addView(val);
        parent.addView(cell);
        return val;
    }

    private LinearLayout card() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackground(bg(c[1], dp(20)));
        l.setPadding(dp(14), dp(12), dp(14), dp(12));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(10));
        l.setLayoutParams(lp);
        return l;
    }

    private TextView cardTitle(String s) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(14);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setTextColor(c[3]);
        t.setPadding(0, 0, 0, dp(8));
        return t;
    }

    private TextView label(String s) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(13);
        t.setTextColor(c[4]);
        t.setPadding(0, dp(6), 0, dp(3));
        return t;
    }

    private Spinner spinner(String[] items) {
        Spinner s = new Spinner(this);
        s.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items));
        return s;
    }

    private EditText input(String def) {
        EditText e = new EditText(this);
        e.setText(def);
        e.setTextColor(c[3]);
        e.setSingleLine(true);
        e.setBackground(bg(c[2], dp(12)));
        e.setPadding(dp(10), dp(6), dp(10), dp(6));
        return e;
    }

    private Button btn(String text, int style) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(13);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setTextColor(Color.WHITE);
        b.setBackground(bg(style == 0 ? c[5] : c[2], dp(16)));
        b.setPadding(dp(6), dp(4), dp(6), dp(4));
        return b;
    }

    private LinearLayout.LayoutParams fullLp(int h, int topMargin) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, h);
        if (topMargin > 0) lp.setMargins(0, dp(topMargin), 0, 0);
        return lp;
    }

    private LinearLayout.LayoutParams halfLp() {
        return new LinearLayout.LayoutParams(0, dp(42), 1f);
    }

    private LinearLayout.LayoutParams wrapLp() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(42));
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private int dp(int v) {
        return Math.round(getResources().getDisplayMetrics().density * v);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alive = false;
        if (statusThread != null) statusThread.interrupt();
    }
}
