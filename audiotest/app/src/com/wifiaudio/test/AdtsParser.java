package com.wifiaudio.test;

import java.util.ArrayList;
import java.util.List;

/**
 * ADTS 流解析器：切出完整 AAC 帧（含 7 字节 ADTS 头）。
 * 用于 aac.aac（AAC-LC ADTS 流），与 WiFiAudio 服务端输出格式一致。
 */
public final class AdtsParser {

    private AdtsParser() {}

    /** 解析 ADTS 流为帧列表 */
    public static List<byte[]> parse(byte[] data) {
        List<byte[]> frames = new ArrayList<byte[]>();
        int pos = 0;
        int n = data.length;
        while (pos + 7 <= n) {
            // 找同步字
            if ((data[pos] & 0xFF) != 0xFF || (data[pos + 1] & 0xF6) != 0xF0) {
                pos++;
                continue;
            }
            int b2 = data[pos + 2] & 0xFF;
            int b3 = data[pos + 3] & 0xFF;
            int b4 = data[pos + 4] & 0xFF;
            int b5 = data[pos + 5] & 0xFF;
            int frameLen = ((b3 & 0x03) << 11) | (b4 << 3) | ((b5 >> 5) & 0x07);
            if (frameLen < 7 || pos + frameLen > n) break;
            byte[] frame = new byte[frameLen];
            System.arraycopy(data, pos, frame, 0, frameLen);
            frames.add(frame);
            pos += frameLen;
        }
        return frames;
    }

    /** 从 ADTS 头提取 AudioSpecificConfig（csd-0，5 字节标准格式） */
    public static byte[] buildCsd(byte[] adts) {
        int b2 = adts[2] & 0xFF;
        int b3 = adts[3] & 0xFF;
        int profile = (b2 >> 6) & 0x03;
        int sfIdx = (b2 >> 2) & 0x0F;
        int chanCfg = ((b2 & 0x03) << 2) | ((b3 >> 6) & 0x03);
        int objectType = profile + 1;
        byte[] csd = new byte[5];
        csd[0] = (byte) ((objectType << 3) | (sfIdx >> 1));
        csd[1] = (byte) (((sfIdx & 1) << 7) | (chanCfg << 3));
        return csd;
    }

    /** 从 ADTS 头提取采样率索引对应的采样率 */
    public static int sampleRate(byte[] adts) {
        int b2 = adts[2] & 0xFF;
        int sfIdx = (b2 >> 2) & 0x0F;
        int[] rates = {96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050,
                16000, 12000, 11025, 8000, 7350, 0, 0, 0};
        return sfIdx < rates.length ? rates[sfIdx] : 0;
    }
}
