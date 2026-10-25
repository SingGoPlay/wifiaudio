package com.wifiaudio;

import java.io.File;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class Config {
    public static final String CONFIG_PATH = "/storage/emulated/0/WiFiAudio/config.json";
    private JSONObject json = new JSONObject();
    private final String path;

    public Config(String str) {
        this.path = str;
        reload();
    }

    public synchronized void reload() {
        try {
            String file = Util.readFile(this.path);
            if (file == null || file.trim().isEmpty()) {
                file = Util.readFile(CONFIG_PATH);
            }
            if (file != null && !file.trim().isEmpty()) {
                this.json = new JSONObject(file);
            } else {
                this.json = new JSONObject();
            }
        } catch (Exception e) {
            Util.log("Config", "reload failed, using defaults: " + Util.causeMsg(e));
            this.json = new JSONObject();
        }
    }

    private String get(String str, String str2) {
        return this.json.optString(str, str2);
    }

    public boolean isEnabled() {
        return "1".equals(get("enabled", "0"));
    }

    public boolean localRender() {
        return "1".equals(get("local_render", "0"));
    }

    public int tcpPort() {
        return parseInt(get("tcp_port", "47800"), 47800);
    }

    public int httpPort() {
        return parseInt(get("http_port", "47801"), 47801);
    }

    public boolean captureCall() {
        return "1".equals(get("capture_call", "0"));
    }

    public int autoOffMinutes() {
        return parseInt(get("auto_off_min", "0"), 0);
    }

    public boolean gameMode() {
        return "game".equalsIgnoreCase(get("mode", "music"));
    }

    public boolean useUdp() {
        return "udp".equalsIgnoreCase(get("transport", "tcp"));
    }

    public int pcmRate() {
        String strTrim = get("pcm_rate", "auto").trim();
        if ("auto".equalsIgnoreCase(strTrim) || strTrim.isEmpty()) {
            return 0;
        }
        return parseInt(strTrim, 0);
    }

    private static int parseInt(String str, int i) throws NumberFormatException {
        try {
            int i2 = Integer.parseInt(str.trim());
            return (i2 <= 0 || i2 >= 65536) ? i : i2;
        } catch (Exception unused) {
            return i;
        }
    }

    public String getPath() {
        return this.path;
    }

    public String getDir() {
        try {
            File file = new File(this.path);
            return file.getParent() != null ? file.getParent() : "/storage/emulated/0/WiFiAudio";
        } catch (Exception unused) {
            return "/storage/emulated/0/WiFiAudio";
        }
    }
}
