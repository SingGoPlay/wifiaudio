package com.wifiaudio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

/* loaded from: classes.dex */
public final class Util {
    private static String logFile;
    private static volatile Thread logWriter;
    private static final SimpleDateFormat TS = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);
    private static final ArrayBlockingQueue<String> LOG_Q = new ArrayBlockingQueue<>(AudioLoopbackCapture.MAX_READ_FRAMES);

    private Util() {
    }

    public static void setLogFile(String str) {
        logFile = str;
    }

    private static void ensureLogWriter() {
        if (logWriter != null) {
            return;
        }
        synchronized (Util.class) {
            if (logWriter != null) {
                return;
            }
            Thread thread = new Thread(new Runnable() { // from class: com.wifiaudio.Util.1
                @Override // java.lang.Runnable
                public void run() throws IOException {
                    while (true) {
                        try {
                            Util.writeLine((String) Util.LOG_Q.take());
                        } catch (InterruptedException unused) {
                            return;
                        }
                    }
                }
            }, "log-writer");
            thread.setDaemon(true);
            thread.start();
            logWriter = thread;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void writeLine(String str) throws IOException {
        try {
            System.out.println(str);
            System.out.flush();
        } catch (Throwable unused) {
        }
        if (logFile != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(logFile, true);
                fileOutputStream.write((str + "\n").getBytes(StandardCharsets.UTF_8));
                fileOutputStream.close();
            } catch (IOException unused2) {
            }
        }
    }

    public static void log(String str, String str2) {
        String str3;
        ensureLogWriter();
        SimpleDateFormat simpleDateFormat = TS;
        synchronized (simpleDateFormat) {
            str3 = "[" + simpleDateFormat.format(new Date()) + "] [" + str + "] " + str2;
        }
        ArrayBlockingQueue<String> arrayBlockingQueue = LOG_Q;
        if (arrayBlockingQueue.offer(str3)) {
            return;
        }
        arrayBlockingQueue.poll();
        arrayBlockingQueue.offer(str3);
    }

    public static void flushLog(long j) throws InterruptedException {
        long jCurrentTimeMillis = System.currentTimeMillis() + j;
        while (!LOG_Q.isEmpty() && System.currentTimeMillis() < jCurrentTimeMillis) {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException unused) {
                return;
            }
        }
    }

    public static void log(String str, String str2, Throwable th) {
        log(str, str2 + " :: " + String.valueOf(th));
        if (th != null) {
            for (int i = 0; th != null && i < 10; i++) {
                StringBuilder sb = new StringBuilder("Caused by: ");
                sb.append(th);
                sb.append('\n');
                StackTraceElement[] stackTrace = th.getStackTrace();
                int iMin = Math.min(stackTrace.length, 12);
                for (int i2 = 0; i2 < iMin; i2++) {
                    sb.append("    at ");
                    sb.append(stackTrace[i2]);
                    sb.append('\n');
                }
                System.out.println(sb.toString());
                th = th.getCause();
            }
        }
    }

    public static boolean writeFileAtomic(String str, String str2) throws IOException {
        try {
            File file = new File(str + ".tmp");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(str2.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.getFD().sync();
            fileOutputStream.close();
            File file2 = new File(str);
            if (file2.exists()) {
                file2.delete();
            }
            return file.renameTo(file2);
        } catch (IOException e) {
            System.err.println("writeFileAtomic failed: " + e);
            return false;
        }
    }

    public static String readFile(String str) throws IOException {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(str, "r");
            byte[] bArr = new byte[(int) Math.min(randomAccessFile.length(), 1048576L)];
            randomAccessFile.readFully(bArr);
            randomAccessFile.close();
            return new String(bArr, StandardCharsets.UTF_8);
        } catch (IOException unused) {
            return null;
        }
    }

    public static String getLocalIp() {
        String strIpOf;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces != null && networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterfaceNextElement = networkInterfaces.nextElement();
                if (networkInterfaceNextElement.isUp() && !networkInterfaceNextElement.isLoopback()) {
                    String name = networkInterfaceNextElement.getName();
                    if (name.startsWith("wlan") || name.startsWith("ap")) {
                        String strIpOf2 = ipOf(networkInterfaceNextElement);
                        if (strIpOf2 != null) {
                            return strIpOf2;
                        }
                    }
                }
            }
            Enumeration<NetworkInterface> networkInterfaces2 = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces2 != null) {
                if (networkInterfaces2.hasMoreElements()) {
                    NetworkInterface networkInterfaceNextElement2 = networkInterfaces2.nextElement();
                    if (networkInterfaceNextElement2.isUp() && !networkInterfaceNextElement2.isLoopback() && (strIpOf = ipOf(networkInterfaceNextElement2)) != null) {
                        return strIpOf;
                    }
                } else {
                    return "127.0.0.1";
                }
            }
            return "127.0.0.1";
        } catch (Throwable unused) {
            return "127.0.0.1";
        }
    }

    private static String ipOf(NetworkInterface networkInterface) {
        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
        while (inetAddresses != null && inetAddresses.hasMoreElements()) {
            InetAddress inetAddressNextElement = inetAddresses.nextElement();
            if ((inetAddressNextElement instanceof Inet4Address) && !inetAddressNextElement.isLoopbackAddress()) {
                return inetAddressNextElement.getHostAddress();
            }
        }
        return null;
    }

    public static String causeMsg(Throwable th) {
        while (th.getCause() != null) {
            th = th.getCause();
        }
        return th.getMessage() != null ? th.getMessage() : th.toString();
    }
}
