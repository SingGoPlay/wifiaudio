package com.wifiaudio.manager;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * root 交互层：通过 su 执行命令，base64 传输数据（绕开文本编码/截断问题）。
 * 与模块 wifiaudio.sh 命令完全一致，但输出处理完全可控。
 */
public final class RootShell {

    public static final String MODDIR = "/data/adb/modules/wifiaudio";
    public static final String CONFIG = "/storage/emulated/0/WiFiAudio/config.json";
    public static final String STATUS = "/storage/emulated/0/WiFiAudio/status.json";

    private RootShell() {}

    /** 检测 Root 环境（su 二进制 / 常见 root 框架路径，纯文件检查不触发授权弹窗） */
    public static boolean hasRoot() {
        String[] candidates = {
                "/system/bin/su", "/system/xbin/su", "/sbin/su",
                "/system/app/Superuser.apk", "/system/app/SuperSU", "/system/app/magisk.apk",
                "/data/adb/magisk", "/data/adb/ksu", "/data/adb/apatch"
        };
        for (String p : candidates) {
            if (new java.io.File(p).exists()) return true;
        }
        return false;
    }

    /** 执行 su 命令，返回 stdout；失败返回 [error] 开头 */
    public static String exec(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line).append('\n');
            }
            p.waitFor();
            String out = sb.toString().trim();
            return out.isEmpty() ? "" : out;
        } catch (Throwable t) {
            return "[error] " + t;
        }
    }

    /** 读取配置文件（base64，绕开文本问题） */
    public static String readConfig() {
        String b64 = exec("base64 < " + CONFIG + " 2>/dev/null");
        if (b64.startsWith("[error]") || b64.isEmpty() || b64.indexOf("base64:") == 0) {
            return null;
        }
        try {
            return new String(Base64.decode(b64, Base64.DEFAULT), StandardCharsets.UTF_8);
        } catch (Throwable t) {
            return null;
        }
    }

    /** 写入配置（base64 → set-all），成功返回 true */
    public static boolean writeConfig(String json) {
        String b64 = Base64.encodeToString(json.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
        String r = exec("sh " + MODDIR + "/bin/wifiaudio.sh set-all '" + b64 + "' 2>&1");
        return r.contains("OK config written");
    }

    /** 读取服务状态（JSON） */
    public static String readStatus() {
        String r = exec("sh " + MODDIR + "/bin/wifiaudio.sh status 2>&1");
        if (r.startsWith("[error]") || r.isEmpty()) return "{}";
        return r;
    }

    /** 启停服务 */
    public static String service(String action) { // start/stop/restart
        return exec("sh " + MODDIR + "/bin/wifiaudio.sh " + action + " 2>&1");
    }

    /** 读取文件内容（日志等，base64） */
    public static String readFileBase64(String path) {
        String b64 = exec("base64 < " + path + " 2>/dev/null");
        if (b64.startsWith("[error]") || b64.isEmpty() || b64.indexOf("base64:") == 0) {
            return null;
        }
        try {
            return new String(Base64.decode(b64, Base64.DEFAULT), StandardCharsets.UTF_8);
        } catch (Throwable t) {
            return null;
        }
    }

    /** 目录文件列表（换行分隔） */
    public static String listDir(String dir) {
        return exec("ls " + dir + " 2>/dev/null");
    }

    /** 读文件（文本，小文件用） */
    public static String readText(String path) {
        return exec("cat " + path + " 2>/dev/null");
    }

    /** 写文本文件 */
    public static boolean writeText(String path, String content) {
        String b64 = Base64.encodeToString(content.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
        String r = exec("echo '" + b64 + "' | base64 -d > " + path + " 2>&1");
        return !r.startsWith("[error]") && r.indexOf("base64:") != 0;
    }

    /** 删除文件 */
    public static void delete(String path) {
        exec("rm -f " + path + " 2>/dev/null");
    }

    /** 创建目录 */
    public static void mkdir(String path) {
        exec("mkdir -p " + path + " 2>/dev/null");
    }
}
