package com.wifiaudio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** 小工具：日志、原子写文件、字节序转换 */
public final class Util {

    private static final SimpleDateFormat TS = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);
    private static String logFile; // 设置后日志同时写入文件

    private Util() {}

    public static void setLogFile(String path) {
        logFile = path;
    }

    public static void log(String tag, String msg) {
        String line = "[" + TS.format(new Date()) + "] [" + tag + "] " + msg;
        System.out.println(line);
        System.out.flush();
        if (logFile != null) {
            try {
                FileOutputStream fos = new FileOutputStream(logFile, true);
                fos.write((line + "\n").getBytes(StandardCharsets.UTF_8));
                fos.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void log(String tag, String msg, Throwable t) {
        log(tag, msg + " :: " + String.valueOf(t));
        if (t != null) {
            Throwable c = t;
            int depth = 0;
            while (c != null && depth < 10) {
                StringBuilder sb = new StringBuilder();
                sb.append("Caused by: ").append(c).append('\n');
                StackTraceElement[] st = c.getStackTrace();
                int len = Math.min(st.length, 12);
                for (int i = 0; i < len; i++) {
                    sb.append("    at ").append(st[i]).append('\n');
                }
                System.out.println(sb.toString());
                c = c.getCause();
                depth++;
            }
        }
    }

    /** 原子写文本文件（先写临时文件再 rename） */
    public static boolean writeFileAtomic(String path, String content) {
        try {
            File tmp = new File(path + ".tmp");
            FileOutputStream fos = new FileOutputStream(tmp);
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            fos.getFD().sync();
            fos.close();
            File dst = new File(path);
            if (dst.exists()) {
                dst.delete();
            }
            return tmp.renameTo(dst);
        } catch (IOException e) {
            System.err.println("writeFileAtomic failed: " + e);
            return false;
        }
    }

    public static String readFile(String path) {
        try {
            RandomAccessFile raf = new RandomAccessFile(path, "r");
            byte[] buf = new byte[(int) Math.min(raf.length(), 1 << 20)];
            raf.readFully(buf);
            raf.close();
            return new String(buf, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    /** 获取本机 IPv4 地址（优先 wlan0，即 WiFi 接口） */
    public static String getLocalIp() {
        try {
            java.net.NetworkInterface nif;
            java.util.Enumeration<java.net.NetworkInterface> ifs = java.net.NetworkInterface.getNetworkInterfaces();
            while (ifs != null && ifs.hasMoreElements()) {
                nif = ifs.nextElement();
                if (!nif.isUp() || nif.isLoopback()) continue;
                String name = nif.getName();
                // 优先 WiFi 接口
                if (name.startsWith("wlan") || name.startsWith("ap")) {
                    String ip = ipOf(nif);
                    if (ip != null) return ip;
                }
            }
            // 其次任意非回环 IPv4
            ifs = java.net.NetworkInterface.getNetworkInterfaces();
            while (ifs != null && ifs.hasMoreElements()) {
                nif = ifs.nextElement();
                if (!nif.isUp() || nif.isLoopback()) continue;
                String ip = ipOf(nif);
                if (ip != null) return ip;
            }
        } catch (Throwable ignored) {
        }
        return "127.0.0.1";
    }

    private static String ipOf(java.net.NetworkInterface nif) {
        java.util.Enumeration<java.net.InetAddress> addrs = nif.getInetAddresses();
        while (addrs != null && addrs.hasMoreElements()) {
            java.net.InetAddress a = addrs.nextElement();
            if (a instanceof java.net.Inet4Address && !a.isLoopbackAddress()) {
                return a.getHostAddress();
            }
        }
        return null;
    }

    /** 从 Throwable 提取原因链第一条消息 */
    public static String causeMsg(Throwable t) {
        Throwable c = t;
        while (c.getCause() != null) c = c.getCause();
        return c.getMessage() != null ? c.getMessage() : c.toString();
    }
}
