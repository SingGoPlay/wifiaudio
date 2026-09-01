package com.wifiaudio;

import org.json.JSONObject;

/** 运行状态，供 WiFiAudio 管理器 / HTTP /status 查询 */
public final class Status {

    public static volatile boolean running = false;
    public static volatile long startTime = 0;
    public static volatile int pid = 0;
    public static volatile int tcpPort = 47800;
    public static volatile int httpPort = 47801;
    public static volatile int udpPort = 48800;
    public static volatile String codec = "pcm";
    public static volatile int sampleRate = 48000;
    public static volatile int channels = 2;
    public static volatile long bitrate = 0;   // 实时比特率 bps（PCM=采样率×位深×声道；OPUS/AAC=配置码率）
    public static volatile int clients = 0;
    public static volatile boolean callCaptureSupported = false;
    public static volatile boolean callCaptureActive = false;
    public static volatile boolean callActive = false;
    public static volatile String lastError = "";
    public static volatile long bytesSent = 0;
    public static volatile String mode = "music";
    public static volatile String transport = "tcp";
    public static volatile String clientsList = "";
    public static volatile long encoderFrames = 0;
    public static volatile boolean encoderActive = false;

    private static String statusFile;

    private Status() {}

    public static void setStatusFile(String path) {
        statusFile = path;
    }

    public static void save() {
        if (statusFile == null) return;
        JSONObject o = new JSONObject();
        try {
            o.put("running", running);
            o.put("startTime", startTime);
            o.put("pid", pid);
            o.put("tcpPort", tcpPort);
            o.put("httpPort", httpPort);
            o.put("udpPort", udpPort);
            o.put("codec", codec);
            o.put("sampleRate", sampleRate);
            o.put("channels", channels);
            o.put("bitrate", bitrate);
            o.put("clients", clients);
            o.put("callCaptureSupported", callCaptureSupported);
            o.put("callCaptureActive", callCaptureActive);
            o.put("callActive", callActive);
            o.put("lastError", lastError);
            o.put("bytesSent", bytesSent);
            o.put("mode", mode);
            o.put("transport", transport);
            o.put("clientsList", clientsList);
            o.put("encoderFrames", encoderFrames);
            o.put("encoderActive", encoderActive);
            o.put("ip", Util.getLocalIp());
            o.put("uptimeSec", running ? (System.currentTimeMillis() - startTime) / 1000 : 0);
        } catch (Exception ignored) {
        }
        Util.writeFileAtomic(statusFile, o.toString());
    }

    public static String toJson() {
        JSONObject o = new JSONObject();
        try {
            o.put("running", running);
            o.put("startTime", startTime);
            o.put("pid", pid);
            o.put("tcpPort", tcpPort);
            o.put("httpPort", httpPort);
            o.put("udpPort", udpPort);
            o.put("codec", codec);
            o.put("sampleRate", sampleRate);
            o.put("channels", channels);
            o.put("bitrate", bitrate);
            o.put("clients", clients);
            o.put("callCaptureSupported", callCaptureSupported);
            o.put("callCaptureActive", callCaptureActive);
            o.put("callActive", callActive);
            o.put("lastError", lastError);
            o.put("bytesSent", bytesSent);
            o.put("mode", mode);
            o.put("transport", transport);
            o.put("clientsList", clientsList);
            o.put("encoderFrames", encoderFrames);
            o.put("encoderActive", encoderActive);
            o.put("ip", Util.getLocalIp());
            o.put("uptimeSec", running ? (System.currentTimeMillis() - startTime) / 1000 : 0);
        } catch (Exception ignored) {
        }
        return o.toString();
    }
}
