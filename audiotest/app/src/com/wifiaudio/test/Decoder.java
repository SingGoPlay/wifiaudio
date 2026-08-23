package com.wifiaudio.test;

/** 解码器统一接口 */
public interface Decoder {
    /** 初始化：解析文件 + 创建解码器；失败返回 false（msg 给出原因） */
    boolean init(String assetName, StringBuilder msg);

    /** 下一帧（null = 结束） */
    byte[] nextFrame();

    /** 解码一帧，返回每声道采样数；<=0 表示错误 */
    int decodeFrame(byte[] frame, int len, short[] out, int outCapacity);

    /** 解码器描述（名称/是否硬件） */
    String info();

    int rate();
    int channels();

    void release();
}
