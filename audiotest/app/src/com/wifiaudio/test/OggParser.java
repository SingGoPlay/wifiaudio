package com.wifiaudio.test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Ogg 容器解析器：提取完整帧（正确处理跨页的 lacing 帧）。
 * 用于 opus.opus（Ogg Opus）→ 裸 opus 帧 + OpusHead(csd)。
 * 与 WiFiAudio 的解码输入格式保持一致（裸帧）。
 */
public final class OggParser {

    private OggParser() {}

    /** 解析 Ogg 数据，返回帧列表（第一帧为 OpusHead，其余为音频帧） */
    public static List<byte[]> parse(byte[] data) {
        List<byte[]> frames = new ArrayList<byte[]>();
        ByteArrayOutputStream pending = null; // 跨页未闭合帧
        int pos = 0;
        int n = data.length;
        while (pos + 27 <= n) {
            if (!isOggS(data, pos)) {
                pos++;
                continue;
            }
            int headerType = data[pos + 5] & 0xFF; // 1=continued
            int nSeg = data[pos + 26] & 0xFF;
            int segTableOff = pos + 27;
            if (segTableOff + nSeg > n) break;
            int segTotal = 0;
            for (int i = 0; i < nSeg; i++) segTotal += data[segTableOff + i] & 0xFF;
            int dataOff = segTableOff + nSeg;
            if (dataOff + segTotal > n) break;

            int p = 0;
            for (int i = 0; i < nSeg; i++) {
                int segLen = data[segTableOff + i] & 0xFF;
                if (pending == null) pending = new ByteArrayOutputStream();
                pending.write(data, dataOff + p, segLen);
                p += segLen;
                if (segLen < 255) { // 帧结束
                    frames.add(pending.toByteArray());
                    pending = null;
                }
            }
            pos = dataOff + segTotal;
        }
        return frames;
    }

    private static boolean isOggS(byte[] d, int o) {
        return d[o] == 'O' && d[o + 1] == 'g' && d[o + 2] == 'g' && d[o + 3] == 'S';
    }
}
