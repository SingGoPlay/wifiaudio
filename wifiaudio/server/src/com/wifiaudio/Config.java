package com.wifiaudio;

import org.json.JSONObject;

/**
 * 配置管理。配置存放在用户可访问目录 /storage/emulated/0/WiFiAudio/config.json（JSON 格式）。
 * 所有配置均可在 KernelSU/Magisk 模块 WebUI 中修改。
 */
public final class Config {

    /** 配置文件路径（用户可访问，WebUI 直接读写） */
    public static final String CONFIG_PATH = "/storage/emulated/0/WiFiAudio/config.json";

    private JSONObject json = new JSONObject();
    private final String path;

    public Config(String path) {
        this.path = path;
        reload();
    }

    public synchronized void reload() {
        try {
            String content = Util.readFile(path);
            if (content == null || content.trim().isEmpty()) {
                // 尝试默认位置（config.json）
                content = Util.readFile(CONFIG_PATH);
            }
            if (content != null && !content.trim().isEmpty()) {
                json = new JSONObject(content);
            } else {
                json = new JSONObject();
            }
        } catch (Exception e) {
            Util.log("Config", "reload failed, using defaults: " + Util.causeMsg(e));
            json = new JSONObject();
        }
    }

    private String get(String key, String def) {
        return json.optString(key, def);
    }

    public boolean isEnabled() { return "1".equals(get("enabled", "0")); }
    public boolean localRender() { return "1".equals(get("local_render", "0")); }
    public int tcpPort() { return parseInt(get("tcp_port", "47800"), 47800); }
    public int httpPort() { return parseInt(get("http_port", "47801"), 47801); }
    public boolean useAac() { return "aac".equalsIgnoreCase(get("codec", "pcm")); }
    public boolean useOpus() { return "opus".equalsIgnoreCase(get("codec", "pcm")); }
    public int aacBitrate() { return parseInt(get("aac_bitrate", "192"), 192); }
    public int opusBitrate() { return parseInt(get("opus_bitrate", "128"), 128); }
    public boolean captureCall() { return "1".equals(get("capture_call", "0")); }
    /** 自动关闭分钟数（0=不自动关） */
    public int autoOffMinutes() { return parseInt(get("auto_off_min", "0"), 0); }
    /** 游戏模式: 低延迟 (接收端应使用小缓冲, 且优先 UDP) */
    public boolean gameMode() { return "game".equalsIgnoreCase(get("mode", "music")); }
    /** 传输协议: true=UDP */
    public boolean useUdp() { return "udp".equalsIgnoreCase(get("transport", "tcp")); }
    /** PCM 采样率: auto=跟随设备输出能力(0) / 48000 / 96000 / 192000。仅 codec=pcm 时生效 */
    public int pcmRate() {
        String v = get("pcm_rate", "auto").trim();
        if ("auto".equalsIgnoreCase(v) || v.isEmpty()) return 0;
        return parseInt(v, 0);
    }

    private static int parseInt(String s, int def) {
        try {
            int v = Integer.parseInt(s.trim());
            return v > 0 && v < 65536 ? v : def;
        } catch (Exception e) {
            return def;
        }
    }

    public String getPath() { return path; }

    /** 返回配置目录路径 (配置文件所在目录) */
    public String getDir() {
        String dir = "/storage/emulated/0/WiFiAudio";
        try {
            java.io.File f = new java.io.File(path);
            if (f.getParent() != null) dir = f.getParent();
        } catch (Exception ignored) {
        }
        return dir;
    }
}
