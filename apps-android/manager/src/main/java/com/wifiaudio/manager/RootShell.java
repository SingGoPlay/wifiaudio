package com.wifiaudio.manager;

import android.util.Base64;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/* loaded from: classes2.dex */
public final class RootShell {
    public static final String CONFIG = "/storage/emulated/0/WiFiAudio/config.json";
    public static final String MODDIR = "/data/adb/modules/wifiaudio";
    public static final String STATUS = "/storage/emulated/0/WiFiAudio/status.json";

    private RootShell() {
    }

    public static boolean hasRoot() {
        String[] strArr = {"/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/app/Superuser.apk", "/system/app/SuperSU", "/system/app/magisk.apk", "/data/adb/magisk", "/data/adb/ksu", "/data/adb/apatch"};
        for (int i = 0; i < 9; i++) {
            if (new File(strArr[i]).exists()) {
                return true;
            }
        }
        return false;
    }

    public static String exec(String str) {
        try {
            Process processExec = Runtime.getRuntime().exec(new String[]{"su", "-c", str});
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(processExec.getInputStream()));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line).append('\n');
            }
            processExec.waitFor();
            String strTrim = sb.toString().trim();
            return strTrim.isEmpty() ? "" : strTrim;
        } catch (Throwable th) {
            return "[error] " + th;
        }
    }

    public static String readConfig() {
        String strExec = exec("base64 < /storage/emulated/0/WiFiAudio/config.json 2>/dev/null");
        if (!strExec.startsWith("[error]") && !strExec.isEmpty() && strExec.indexOf("base64:") != 0) {
            try {
                return new String(Base64.decode(strExec, 0), StandardCharsets.UTF_8);
            } catch (Throwable unused) {
            }
        }
        return null;
    }

    public static boolean writeConfig(String str) {
        return exec("sh /data/adb/modules/wifiaudio/bin/wifiaudio.sh set-all '" + Base64.encodeToString(str.getBytes(StandardCharsets.UTF_8), 2) + "' 2>&1").contains("OK config written");
    }

    public static String readStatus() {
        String strExec = exec("sh /data/adb/modules/wifiaudio/bin/wifiaudio.sh status 2>&1");
        return (strExec.startsWith("[error]") || strExec.isEmpty()) ? "{}" : strExec;
    }

    public static String service(String str) {
        return exec("sh /data/adb/modules/wifiaudio/bin/wifiaudio.sh " + str + " 2>&1");
    }

    public static String readFileBase64(String str) {
        String strExec = exec("base64 < " + str + " 2>/dev/null");
        if (!strExec.startsWith("[error]") && !strExec.isEmpty() && strExec.indexOf("base64:") != 0) {
            try {
                return new String(Base64.decode(strExec, 0), StandardCharsets.UTF_8);
            } catch (Throwable unused) {
            }
        }
        return null;
    }

    public static String listDir(String str) {
        return exec("ls " + str + " 2>/dev/null");
    }

    public static String readText(String str) {
        return exec("cat " + str + " 2>/dev/null");
    }

    public static boolean writeText(String str, String str2) {
        String strExec = exec("echo '" + Base64.encodeToString(str2.getBytes(StandardCharsets.UTF_8), 2) + "' | base64 -d > " + str + " 2>&1");
        return (strExec.startsWith("[error]") || strExec.indexOf("base64:") == 0) ? false : true;
    }

    public static void delete(String str) {
        exec("rm -f " + str + " 2>/dev/null");
    }

    public static void mkdir(String str) {
        exec("mkdir -p " + str + " 2>/dev/null");
    }
}
