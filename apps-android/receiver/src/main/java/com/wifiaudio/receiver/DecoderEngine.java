package com.wifiaudio.receiver;

/**
 * 解码引擎抽象：异步硬解（MediaCodec）/ 软解（libopus/fdk-aac）。
 * feed 从读取线程调用，PCM 输出到 AudioOut。
 */
public interface DecoderEngine {
    /** 初始化；失败返回 false（调用方 fallback） */
    boolean init();

    /** 喂入一帧（从读取线程调用） */
    void feed(byte[] data, int off, int len);

    /** 流结束 */
    void eos();

    /** 是否已失败（硬解异常时） */
    boolean failed();

    /** 引擎名称 */
    String name();

    /** 输出帧数统计 */
    long outputFrames();

    void release();
}
