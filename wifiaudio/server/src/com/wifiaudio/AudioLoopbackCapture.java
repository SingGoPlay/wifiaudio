package com.wifiaudio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;

import java.lang.reflect.Method;

/**
 * 音频回环捕获：基于 Android 13+ (API 33) 的 AudioPolicy + AudioMix loopback 机制，
 * 以 root 身份注册 AudioPolicy，捕获系统所有音频输出（媒体/游戏/通知/系统音效/VoIP 通话）。
 *
 * 实现参考 Genymobile scrcpy 的 AudioPlaybackCapture（已在海量设备验证），
 * 使用反射调用隐藏/系统 API，兼容 Android 13 - 16。
 */
public final class AudioLoopbackCapture {

    /** 默认/兜底采样率 */
    public static final int DEFAULT_SAMPLE_RATE = 48000;
    public static final int CHANNELS = 2;
    public static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    public static final int BYTES_PER_SAMPLE = 2;
    /** 每次最多读 512 帧采样（每个声道）。48k 时约 10.7ms/块，96k 约 5.3ms —— 减小读取粒度降低发送端批量延迟 */
    public static final int MAX_READ_FRAMES = 512;
    public static final int MAX_READ_BYTES = MAX_READ_FRAMES * CHANNELS * BYTES_PER_SAMPLE;

    /** 要捕获的所有音频使用场景 */
    private static final int[] CAPTURED_USAGES = {
            AudioAttributes.USAGE_MEDIA,                // 媒体播放
            AudioAttributes.USAGE_GAME,                 // 游戏
            AudioAttributes.USAGE_UNKNOWN,              // 未知（部分应用）
            AudioAttributes.USAGE_ASSISTANT,            // 语音助手
            AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY,   // 无障碍
            AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE, // 导航
            AudioAttributes.USAGE_ASSISTANCE_SONIFICATION,      // 系统音效
            AudioAttributes.USAGE_NOTIFICATION,         // 通知
            AudioAttributes.USAGE_NOTIFICATION_RINGTONE,
            AudioAttributes.USAGE_NOTIFICATION_EVENT,
            AudioAttributes.USAGE_ALARM,                // 闹钟
            AudioAttributes.USAGE_VOICE_COMMUNICATION,  // VoIP 通话（微信语音等）
            AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING,
    };

    private AudioRecord recorder;
    private boolean keepPlayingOnDevice;
    /** 实际生效采样率（start 后读取） */
    private int sampleRate = DEFAULT_SAMPLE_RATE;

    public AudioLoopbackCapture(boolean keepPlayingOnDevice) {
        this.keepPlayingOnDevice = keepPlayingOnDevice;
    }

    /** 当前实际采样率 */
    public int getSampleRate() { return sampleRate; }

    /**
     * 自动检测当前输出设备支持的采样率（48k 系列最高档）。
     * 仅对"支持 Hi-Res 输出"的设备返回 >48k；普通设备返回 48000。
     */
    public static int detectDeviceSampleRate(Context context) {
        try {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (am == null) return DEFAULT_SAMPLE_RATE;
            int best = DEFAULT_SAMPLE_RATE;
            for (android.media.AudioDeviceInfo d : am.getDevices(AudioManager.GET_DEVICES_OUTPUTS)) {
                if (d == null || !d.isSink()) continue;
                for (int r : d.getSampleRates()) {
                    // 只取 48k 系列（避免 44.1k 系列带来的怪异切换），且 ≤192kHz
                    if ((r == 48000 || r == 96000 || r == 192000) && r > best) {
                        best = r;
                    }
                }
            }
            return best;
        } catch (Throwable t) {
            return DEFAULT_SAMPLE_RATE;
        }
    }

    /** 创建并启动回环捕获。失败抛出带原因的 RuntimeException。 */
    @SuppressLint({"PrivateApi", "SoonBlockedPrivateApi"})
    public void start(Context context) throws Exception {
        start(context, 0);
    }

    /**
     * 创建并启动回环捕获。
     * @param targetRate 目标采样率；0=自动检测设备能力；48000/96000/192000=固定值
     */
    @SuppressLint({"PrivateApi", "SoonBlockedPrivateApi"})
    public void start(Context context, int targetRate) throws Exception {
        if (targetRate <= 0) {
            targetRate = detectDeviceSampleRate(context);
        }
        if (targetRate != 48000 && targetRate != 96000 && targetRate != 192000) {
            targetRate = DEFAULT_SAMPLE_RATE;
        }
        final int requestedRate = targetRate;

        Class<?> audioMixingRuleClass = Class.forName("android.media.audiopolicy.AudioMixingRule");
        Class<?> audioMixingRuleBuilderClass = Class.forName("android.media.audiopolicy.AudioMixingRule$Builder");
        Class<?> audioMixClass = Class.forName("android.media.audiopolicy.AudioMix");
        Class<?> audioMixBuilderClass = Class.forName("android.media.audiopolicy.AudioMix$Builder");
        Class<?> audioPolicyClass = Class.forName("android.media.audiopolicy.AudioPolicy");
        Class<?> audioPolicyBuilderClass = Class.forName("android.media.audiopolicy.AudioPolicy$Builder");

        // AudioMixingRule.Builder builder = new AudioMixingRule.Builder();
        Object builder = audioMixingRuleBuilderClass.getConstructor().newInstance();

        // builder.setTargetMixRole(AudioMixingRule.MIX_ROLE_PLAYERS);
        int mixRolePlayers = audioMixingRuleClass.getField("MIX_ROLE_PLAYERS").getInt(null);
        Method setTargetMixRole = audioMixingRuleBuilderClass.getMethod("setTargetMixRole", int.class);
        setTargetMixRole.invoke(builder, mixRolePlayers);

        // 匹配所有目标 usage（同一 builder 内多条规则为 OR 关系）
        int ruleMatchAttrUsage = audioMixingRuleClass.getField("RULE_MATCH_ATTRIBUTE_USAGE").getInt(null);
        Method addMixRule = audioMixingRuleBuilderClass.getMethod("addMixRule", int.class, Object.class);
        for (int usage : CAPTURED_USAGES) {
            AudioAttributes attrs = new AudioAttributes.Builder().setUsage(usage).build();
            addMixRule.invoke(builder, ruleMatchAttrUsage, attrs);
        }

        // builder.voiceCommunicationCaptureAllowed(true); —— 允许捕获 VoIP 通话
        try {
            Method vcc = audioMixingRuleBuilderClass.getMethod("voiceCommunicationCaptureAllowed", boolean.class);
            vcc.invoke(builder, true);
        } catch (NoSuchMethodException ignored) {
            // 低版本没有此方法
        }

        // AudioMixingRule rule = builder.build();
        Object rule = audioMixingRuleBuilderClass.getMethod("build").invoke(builder);

        // AudioMix.Builder mixBuilder = new AudioMix.Builder(rule);
        Object mixBuilder = audioMixBuilderClass.getConstructor(audioMixingRuleClass).newInstance(rule);

        // mixBuilder.setFormat(format)
        AudioFormat format = createAudioFormat(requestedRate);
        Method setFormat = mixBuilder.getClass().getMethod("setFormat", AudioFormat.class);
        setFormat.invoke(mixBuilder, format);

        // mixBuilder.setRouteFlags(ROUTE_FLAG_LOOP_BACK[_RENDER])
        String routeFlagName = keepPlayingOnDevice ? "ROUTE_FLAG_LOOP_BACK_RENDER" : "ROUTE_FLAG_LOOP_BACK";
        int routeFlags;
        try {
            routeFlags = audioMixClass.getField(routeFlagName).getInt(null);
        } catch (NoSuchFieldException e) {
            routeFlags = audioMixClass.getField("ROUTE_FLAG_LOOP_BACK").getInt(null);
        }
        Method setRouteFlags = mixBuilder.getClass().getMethod("setRouteFlags", int.class);
        setRouteFlags.invoke(mixBuilder, routeFlags);

        // AudioMix mix = mixBuilder.build();
        Object mix = audioMixBuilderClass.getMethod("build").invoke(mixBuilder);

        // AudioPolicy.Builder policyBuilder = new AudioPolicy.Builder(context);
        Object policyBuilder = audioPolicyBuilderClass.getConstructor(Context.class).newInstance(context);
        Method addMix = audioPolicyBuilderClass.getMethod("addMix", audioMixClass);
        addMix.invoke(policyBuilder, mix);
        Object policy = audioPolicyBuilderClass.getMethod("build").invoke(policyBuilder);

        // AudioManager.registerAudioPolicyStatic(policy)
        Method register = AudioManager.class.getDeclaredMethod("registerAudioPolicyStatic", audioPolicyClass);
        register.setAccessible(true);
        int result = (int) register.invoke(null, policy);
        if (result != 0) {
            throw new RuntimeException("registerAudioPolicyStatic returned " + result);
        }

        // policy.createAudioRecordSink(mix) → AudioRecord
        Method createSink = audioPolicyClass.getMethod("createAudioRecordSink", audioMixClass);
        recorder = (AudioRecord) createSink.invoke(policy, mix);

        if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
            throw new RuntimeException("AudioRecord sink not initialized");
        }
        recorder.startRecording();
        // 读取实际生效采样率（系统可能未按请求值处理）
        sampleRate = recorder.getSampleRate();
        if (sampleRate <= 0) sampleRate = requestedRate;
    }

    private static AudioFormat createAudioFormat(int sampleRate) {
        AudioFormat.Builder b = new AudioFormat.Builder();
        b.setEncoding(ENCODING);
        b.setSampleRate(sampleRate);
        b.setChannelMask(AudioFormat.CHANNEL_IN_STEREO);
        return b.build();
    }

    /** 读取一帧 PCM 数据到 buf，返回实际读取字节数；-1 表示停止 */
    public int read(byte[] buf) {
        if (recorder == null) return -1;
        int n = recorder.read(buf, 0, MAX_READ_BYTES);
        return n;
    }

    public void stop() {
        if (recorder != null) {
            try {
                recorder.release();
            } catch (Throwable ignored) {
            }
            recorder = null;
        }
    }
}
