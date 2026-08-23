/*
 * WiFiAudio OPUS 编码 JNI 封装
 * 静态链接 libopus + musl，产出自包含 .so，可在 Android 上直接 dlopen。
 */
#include <jni.h>
#include <opus.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>

typedef struct {
    OpusEncoder *enc;
    int channels;
} EncCtx;

JNIEXPORT jlong JNICALL
Java_com_wifiaudio_OpusNative_init(JNIEnv *env, jclass clazz,
                                   jint sampleRate, jint channels, jint bitrate) {
    int err = 0;
    OpusEncoder *enc = opus_encoder_create(sampleRate, channels,
                                           OPUS_APPLICATION_AUDIO, &err);
    if (err != OPUS_OK || enc == NULL) {
        return 0;
    }
    opus_encoder_ctl(enc, OPUS_SET_BITRATE(bitrate));
    opus_encoder_ctl(enc, OPUS_SET_COMPLEXITY(10));
    opus_encoder_ctl(enc, OPUS_SET_SIGNAL(OPUS_SIGNAL_MUSIC));
    opus_encoder_ctl(enc, OPUS_SET_VBR(1));
    opus_encoder_ctl(enc, OPUS_SET_INBAND_FEC(0));

    EncCtx *ctx = (EncCtx *) calloc(1, sizeof(EncCtx));
    if (ctx == NULL) {
        opus_encoder_destroy(enc);
        return 0;
    }
    ctx->enc = enc;
    ctx->channels = channels;
    return (jlong) (intptr_t) ctx;
}

JNIEXPORT jint JNICALL
Java_com_wifiaudio_OpusNative_encode(JNIEnv *env, jclass clazz,
                                     jlong handle, jshortArray pcmArr,
                                     jint frames, jbyteArray outArr) {
    EncCtx *ctx = (EncCtx *) (intptr_t) handle;
    if (ctx == NULL || ctx->enc == NULL) return -1;

    jshort *pcm = (*env)->GetShortArrayElements(env, pcmArr, NULL);
    if (pcm == NULL) return -2;
    jbyte *out = (*env)->GetByteArrayElements(env, outArr, NULL);
    if (out == NULL) {
        (*env)->ReleaseShortArrayElements(env, pcmArr, pcm, JNI_ABORT);
        return -2;
    }
    jsize outCap = (*env)->GetArrayLength(env, outArr);

    jint len = opus_encode(ctx->enc, (const opus_int16 *) pcm, frames,
                           (unsigned char *) out, outCap);

    (*env)->ReleaseShortArrayElements(env, pcmArr, pcm, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, outArr, out, 0);
    return len;
}

JNIEXPORT void JNICALL
Java_com_wifiaudio_OpusNative_destroy(JNIEnv *env, jclass clazz, jlong handle) {
    EncCtx *ctx = (EncCtx *) (intptr_t) handle;
    if (ctx == NULL) return;
    if (ctx->enc != NULL) opus_encoder_destroy(ctx->enc);
    free(ctx);
}
