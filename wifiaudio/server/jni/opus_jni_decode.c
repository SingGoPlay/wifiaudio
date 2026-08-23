/*
 * WiFiAudio OPUS 解码 JNI 封装（接收端软解）
 * 静态包含 libopus，仅动态依赖 bionic libc/libm，Android 可 dlopen。
 * 绕开损坏的 MediaCodec 解码器。
 */
#include <jni.h>
#include <opus.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>

typedef struct {
    OpusDecoder *dec;
    int channels;
} DecCtx;

JNIEXPORT jlong JNICALL
Java_com_wifiaudio_OpusDecoder_init(JNIEnv *env, jclass clazz,
                                    jint sampleRate, jint channels) {
    int err = 0;
    OpusDecoder *dec = opus_decoder_create(sampleRate, channels, &err);
    if (err != OPUS_OK || dec == NULL) {
        return 0;
    }
    DecCtx *ctx = (DecCtx *) calloc(1, sizeof(DecCtx));
    if (ctx == NULL) {
        opus_decoder_destroy(dec);
        return 0;
    }
    ctx->dec = dec;
    ctx->channels = channels;
    return (jlong) (intptr_t) ctx;
}

JNIEXPORT jint JNICALL
Java_com_wifiaudio_OpusDecoder_decode(JNIEnv *env, jclass clazz,
                                      jlong handle, jbyteArray inArr,
                                      jint inLen, jshortArray outArr) {
    DecCtx *ctx = (DecCtx *) (intptr_t) handle;
    if (ctx == NULL || ctx->dec == NULL) return -1;

    jbyte *in = (*env)->GetByteArrayElements(env, inArr, NULL);
    if (in == NULL) return -2;
    jshort *out = (*env)->GetShortArrayElements(env, outArr, NULL);
    if (out == NULL) {
        (*env)->ReleaseByteArrayElements(env, inArr, in, JNI_ABORT);
        return -2;
    }
    jsize outCap = (*env)->GetArrayLength(env, outArr) / ctx->channels;

    int samples = opus_decode(ctx->dec, (const unsigned char *) in, inLen,
                              (opus_int16 *) out, outCap, 0);

    (*env)->ReleaseByteArrayElements(env, inArr, in, JNI_ABORT);
    (*env)->ReleaseShortArrayElements(env, outArr, out, 0);
    return samples; // 每声道采样数
}

JNIEXPORT jint JNICALL
Java_com_wifiaudio_OpusDecoder_decodeFec(JNIEnv *env, jclass clazz,
                                         jlong handle, jbyteArray inArr,
                                         jint inLen, jshortArray outArr) {
    DecCtx *ctx = (DecCtx *) (intptr_t) handle;
    if (ctx == NULL || ctx->dec == NULL) return -1;

    jbyte *in = (*env)->GetByteArrayElements(env, inArr, NULL);
    if (in == NULL) return -2;
    jshort *out = (*env)->GetShortArrayElements(env, outArr, NULL);
    if (out == NULL) {
        (*env)->ReleaseByteArrayElements(env, inArr, in, JNI_ABORT);
        return -2;
    }
    jsize outCap = (*env)->GetArrayLength(env, outArr) / ctx->channels;

    int samples = opus_decode(ctx->dec, (const unsigned char *) in, inLen,
                              (opus_int16 *) out, outCap, 1);

    (*env)->ReleaseByteArrayElements(env, inArr, in, JNI_ABORT);
    (*env)->ReleaseShortArrayElements(env, outArr, out, 0);
    return samples;
}

JNIEXPORT void JNICALL
Java_com_wifiaudio_OpusDecoder_destroy(JNIEnv *env, jclass clazz, jlong handle) {
    DecCtx *ctx = (DecCtx *) (intptr_t) handle;
    if (ctx == NULL) return;
    if (ctx->dec != NULL) opus_decoder_destroy(ctx->dec);
    free(ctx);
}
