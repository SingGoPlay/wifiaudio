/*
 * AAudio 播放 JNI（NDK 低延迟音频输出）
 * 编译: NDK clang --target=aarch64-linux-android24，链接 -laaudio
 */
#include <jni.h>
#include <aaudio/AAudio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>

typedef struct {
    AAudioStream *stream;
    int channels;
} AaudioCtx;

JNIEXPORT jlong JNICALL
Java_com_wifiaudio_AaudioNative_open(JNIEnv *env, jclass clazz,
                                          jint rate, jint channels, jint bufferMs) {
    AAudioStreamBuilder *builder = NULL;
    AAudioStream *stream = NULL;
    if (AAudio_createStreamBuilder(&builder) != AAUDIO_OK) return 0;

    AAudioStreamBuilder_setFormat(builder, AAUDIO_FORMAT_PCM_I16);
    AAudioStreamBuilder_setSampleRate(builder, rate);
    AAudioStreamBuilder_setChannelCount(builder, channels);
    AAudioStreamBuilder_setPerformanceMode(builder, AAUDIO_PERFORMANCE_MODE_LOW_LATENCY);
    // 独占模式：绕过混音器，延迟最低（设备不支持时自动回退 shared，AAudio 保证不失败）
    AAudioStreamBuilder_setSharingMode(builder, AAUDIO_SHARING_MODE_EXCLUSIVE);
    if (bufferMs > 0) {
        // 容量留足（防 write 阻塞），实际缓冲稍后压到 burst 级
        AAudioStreamBuilder_setBufferCapacityInFrames(builder, (int32_t) (rate * (bufferMs + 200) / 1000));
    }

    aaudio_result_t r = AAudioStreamBuilder_openStream(builder, &stream);
    AAudioStreamBuilder_delete(builder);
    if (r != AAUDIO_OK || stream == NULL) return 0;

    // 关键：AAudio 不做自动重采样。若设备输出采样率与请求不匹配（如请求 48k、
    // 设备原生 44.1k），消费速率不匹配会导致队列持续积压/丢帧/电音 → 直接回退。
    int32_t actualRate = AAudioStream_getSampleRate(stream);
    if (actualRate != rate) {
        AAudioStream_close(stream);
        return 0;
    }

    // 把实际缓冲压到 burst×4（延迟仍低，但给 write 留足空间，防止 underrun 爆音/电音）
    int32_t burst = AAudioStream_getFramesPerBurst(stream);
    if (burst > 0) {
        int32_t target = burst * 4;
        int32_t cap = AAudioStream_getBufferCapacityInFrames(stream);
        if (target > cap) target = cap;
        if (target > 0) AAudioStream_setBufferSizeInFrames(stream, target);
    }

    r = AAudioStream_requestStart(stream);
    if (r != AAUDIO_OK) {
        AAudioStream_close(stream);
        return 0;
    }

    AaudioCtx *ctx = (AaudioCtx *) calloc(1, sizeof(AaudioCtx));
    if (ctx == NULL) {
        AAudioStream_close(stream);
        return 0;
    }
    ctx->stream = stream;
    ctx->channels = channels;
    return (jlong) (intptr_t) ctx;
}

JNIEXPORT jint JNICALL
Java_com_wifiaudio_AaudioNative_write(JNIEnv *env, jclass clazz,
                                           jlong handle, jshortArray pcmArr, jint frames) {
    AaudioCtx *ctx = (AaudioCtx *) (intptr_t) handle;
    if (ctx == NULL || ctx->stream == NULL) return -1;
    jshort *pcm = (*env)->GetShortArrayElements(env, pcmArr, NULL);
    if (pcm == NULL) return -2;
    // 循环写入直到全部写完（避免超时部分写入导致丢数据/电音）
    int32_t total = 0;
    while (total < frames) {
        int32_t w = AAudioStream_write(ctx->stream, pcm + total, frames - total, 10000000 /* 10ms timeout */);
        if (w <= 0) break; // 流错误/停止
        total += w;
    }
    (*env)->ReleaseShortArrayElements(env, pcmArr, pcm, JNI_ABORT);
    return total;
}

JNIEXPORT jlong JNICALL
Java_com_wifiaudio_AaudioNative_getBufferedFrames(JNIEnv *env, jclass clazz, jlong handle) {
    AaudioCtx *ctx = (AaudioCtx *) (intptr_t) handle;
    if (ctx == NULL || ctx->stream == NULL) return -1;
    // 当前缓冲中的帧数（写入 - 已读）
    int64_t written = AAudioStream_getFramesWritten(ctx->stream);
    int64_t read = AAudioStream_getFramesRead(ctx->stream);
    int64_t buffered = written - read;
    if (buffered < 0) buffered = 0;
    return (jlong) buffered;
}

JNIEXPORT void JNICALL
Java_com_wifiaudio_AaudioNative_close(JNIEnv *env, jclass clazz, jlong handle) {
    AaudioCtx *ctx = (AaudioCtx *) (intptr_t) handle;
    if (ctx == NULL) return;
    if (ctx->stream != NULL) {
        AAudioStream_requestStop(ctx->stream);
        AAudioStream_close(ctx->stream);
    }
    free(ctx);
}
