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
Java_com_wifiaudio_test_AaudioNative_open(JNIEnv *env, jclass clazz,
                                          jint rate, jint channels, jint bufferMs) {
    AAudioStreamBuilder *builder = NULL;
    AAudioStream *stream = NULL;
    if (AAudio_createStreamBuilder(&builder) != AAUDIO_OK) return 0;

    AAudioStreamBuilder_setFormat(builder, AAUDIO_FORMAT_PCM_I16);
    AAudioStreamBuilder_setSampleRate(builder, rate);
    AAudioStreamBuilder_setChannelCount(builder, channels);
    AAudioStreamBuilder_setPerformanceMode(builder, AAUDIO_PERFORMANCE_MODE_LOW_LATENCY);
    if (bufferMs > 0) {
        AAudioStreamBuilder_setBufferCapacityInFrames(builder, (int32_t) (rate * bufferMs / 1000) * 2);
    }

    aaudio_result_t r = AAudioStreamBuilder_openStream(builder, &stream);
    AAudioStreamBuilder_delete(builder);
    if (r != AAUDIO_OK || stream == NULL) return 0;

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
Java_com_wifiaudio_test_AaudioNative_write(JNIEnv *env, jclass clazz,
                                           jlong handle, jshortArray pcmArr, jint frames) {
    AaudioCtx *ctx = (AaudioCtx *) (intptr_t) handle;
    if (ctx == NULL || ctx->stream == NULL) return -1;
    jshort *pcm = (*env)->GetShortArrayElements(env, pcmArr, NULL);
    if (pcm == NULL) return -2;
    jint written = AAudioStream_write(ctx->stream, pcm, frames, 10000000 /* 10ms timeout */);
    (*env)->ReleaseShortArrayElements(env, pcmArr, pcm, JNI_ABORT);
    return written;
}

JNIEXPORT jlong JNICALL
Java_com_wifiaudio_test_AaudioNative_getBufferedFrames(JNIEnv *env, jclass clazz, jlong handle) {
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
Java_com_wifiaudio_test_AaudioNative_close(JNIEnv *env, jclass clazz, jlong handle) {
    AaudioCtx *ctx = (AaudioCtx *) (intptr_t) handle;
    if (ctx == NULL) return;
    if (ctx->stream != NULL) {
        AAudioStream_requestStop(ctx->stream);
        AAudioStream_close(ctx->stream);
    }
    free(ctx);
}
