package com.wifiaudio;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class Status {
    public static volatile long bitrate = 0;
    public static volatile long bytesSent = 0;
    public static volatile boolean callActive = false;
    public static volatile boolean callCaptureActive = false;
    public static volatile boolean callCaptureSupported = false;
    public static volatile int captureLatencyMs = 0;
    public static volatile int channels = 2;
    public static volatile int clients = 0;
    public static volatile String clientsList = "";
    public static volatile String codec = "pcm";
    public static volatile int httpPort = 47801;
    public static volatile String lastError = "";
    public static volatile long mixerDispatchUs = 0;
    public static volatile long mixerDrops = 0;
    public static volatile int mixerQueueFrames = 0;
    public static volatile String mode = "music";
    public static volatile int pid = 0;
    public static volatile int recordBufferFrames = -1;
    public static volatile boolean running = false;
    public static volatile int sampleRate = 48000;
    public static volatile long startTime = 0;
    private static String statusFile = null;
    public static volatile int tcpClients = 0;
    public static volatile int tcpPort = 47800;
    public static volatile int tcpQueueFrames = 0;
    public static volatile String transport = "tcp";
    public static volatile int udpClients = 0;
    public static volatile int udpPort = 48800;

    public static void setTcpClients(int i) {
        tcpClients = i;
        clients = tcpClients + udpClients;
    }

    public static void setUdpClients(int i) {
        udpClients = i;
        clients = tcpClients + udpClients;
    }

    public static int senderLatencyMs() {
        int i = captureLatencyMs;
        if (i <= 0) {
            return 0;
        }
        if (i > 65535) {
            return 65535;
        }
        return i;
    }

    private Status() {
    }

    public static void setStatusFile(String str) {
        statusFile = str;
    }

    public static void save() throws JSONException, IOException {
        if (statusFile == null) {
            return;
        }
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("running", running);
            jSONObject.put("startTime", startTime);
            jSONObject.put("pid", pid);
            jSONObject.put("tcpPort", tcpPort);
            jSONObject.put("httpPort", httpPort);
            jSONObject.put("udpPort", udpPort);
            jSONObject.put("codec", codec);
            jSONObject.put("sampleRate", sampleRate);
            jSONObject.put("channels", channels);
            jSONObject.put("bitrate", bitrate);
            jSONObject.put("clients", clients);
            jSONObject.put("callCaptureSupported", callCaptureSupported);
            jSONObject.put("callCaptureActive", callCaptureActive);
            jSONObject.put("callActive", callActive);
            jSONObject.put("lastError", lastError);
            jSONObject.put("bytesSent", bytesSent);
            jSONObject.put("mode", mode);
            jSONObject.put("transport", transport);
            jSONObject.put("clientsList", clientsList);
            jSONObject.put("mixerDrops", mixerDrops);
            jSONObject.put("mixerQueueFrames", mixerQueueFrames);
            jSONObject.put("tcpQueueFrames", tcpQueueFrames);
            jSONObject.put("captureLatencyMs", captureLatencyMs);
            jSONObject.put("recordBufferFrames", recordBufferFrames);
            jSONObject.put("mixerDispatchUs", mixerDispatchUs);
            jSONObject.put("ip", Util.getLocalIp());
            jSONObject.put("uptimeSec", running ? (System.currentTimeMillis() - startTime) / 1000 : 0L);
        } catch (Exception unused) {
        }
        Util.writeFileAtomic(statusFile, jSONObject.toString());
    }

    public static String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("running", running);
            jSONObject.put("startTime", startTime);
            jSONObject.put("pid", pid);
            jSONObject.put("tcpPort", tcpPort);
            jSONObject.put("httpPort", httpPort);
            jSONObject.put("udpPort", udpPort);
            jSONObject.put("codec", codec);
            jSONObject.put("sampleRate", sampleRate);
            jSONObject.put("channels", channels);
            jSONObject.put("bitrate", bitrate);
            jSONObject.put("clients", clients);
            jSONObject.put("callCaptureSupported", callCaptureSupported);
            jSONObject.put("callCaptureActive", callCaptureActive);
            jSONObject.put("callActive", callActive);
            jSONObject.put("lastError", lastError);
            jSONObject.put("bytesSent", bytesSent);
            jSONObject.put("mode", mode);
            jSONObject.put("transport", transport);
            jSONObject.put("clientsList", clientsList);
            jSONObject.put("mixerDrops", mixerDrops);
            jSONObject.put("mixerQueueFrames", mixerQueueFrames);
            jSONObject.put("tcpQueueFrames", tcpQueueFrames);
            jSONObject.put("captureLatencyMs", captureLatencyMs);
            jSONObject.put("recordBufferFrames", recordBufferFrames);
            jSONObject.put("mixerDispatchUs", mixerDispatchUs);
            jSONObject.put("ip", Util.getLocalIp());
            jSONObject.put("uptimeSec", running ? (System.currentTimeMillis() - startTime) / 1000 : 0L);
        } catch (Exception unused) {
        }
        return jSONObject.toString();
    }
}
