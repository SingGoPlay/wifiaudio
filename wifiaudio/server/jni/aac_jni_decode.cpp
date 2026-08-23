/*
 * WiFiAudio AAC 解码 JNI 封装（接收端软解，fdk-aac）
 * 静态包含 fdk-aac，仅动态依赖 bionic libc/libm，Android 可 dlopen。
 * 绕开损坏的 MediaCodec AAC 解码器。
 */
#include <jni.h>
#include <aacdecoder_lib.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>

typedef struct {
    HANDLE_AACDECODER dec;
    int channels;
} AacCtx;

JNIEXPORT jlong JNICALL
Java_com_wifiaudio_AacDecoder_init(JNIEnv *env, jclass clazz,
                                   jint sampleRate, jint channels, jbyteArray ascArr) {
    if (ascArr == NULL) return 0;
    jsize ascLen = (*env)->GetArrayLength(env, ascArr);
    jbyte *asc = (*env)->GetByteArrayElements(env, ascArr, NULL);
    if (asc == NULL) return 0;

    HANDLE_AACDECODER dec = aacDecoder_Open(TT_MP4_ADTS, 1);
    if (dec == NULL) {
        (*env)->ReleaseByteArrayElements(env, ascArr, asc, JNI_ABORT);
        return 0;
    }
    // 配置 AudioSpecificConfig (csd-0)
    UCHAR *csd[1] = { (UCHAR *) asc };
    UINT csdLen[1] = { (UINT) ascLen };
    AAC_DECODER_ERROR err = aacDecoder_ConfigRaw(dec, csd, csdLen);
    (*env)->ReleaseByteArrayElements(env, ascArr, asc, JNI_ABORT);
    if (err != AAC_DEC_OK) {
        aacDecoder_Close(dec);
        return 0;
    }

    AacCtx *ctx = (AacCtx *) calloc(1, sizeof(AacCtx));
    if (ctx == NULL) {
        aacDecoder_Close(dec);
        return 0;
    }
    ctx->dec = dec;
    ctx->channels = channels;
    return (jlong) (intptr_t) ctx;
}

JNIEXPORT jint JNICALL
Java_com_wifiaudio_AacDecoder_decode(JNIEnv *env, jclass clazz,
                                     jlong handle, jbyteArray inArr,
                                     jint inLen, jshortArray outArr) {
    AacCtx *ctx = (AacCtx *) (intptr_t) handle;
    if (ctx == NULL || ctx->dec == NULL) return -1;

    jbyte *in = (*env)->GetByteArrayElements(env, inArr, NULL);
    if (in == NULL) return -2;
    jshort *out = (*env)->GetShortArrayElements(env, outArr, NULL);
    if (out == NULL) {
        (*env)->ReleaseByteArrayElements(env, inArr, in, JNI_ABORT);
        return -2;
    }
    jsize outCap = (*env)->GetArrayLength(env, outArr) / ctx->channels;

    // Fill：喂入 ADTS 数据
    UCHAR *buf = (UCHAR *) in;
    UINT bufSize = (UINT) inLen;
    UINT bytesValid = (UINT) inLen;
    AAC_DECODER_ERROR err = aacDecoder_Fill(ctx->dec, &buf, &bufSize, &bytesValid);

    int samples = 0;
    if (err == AAC_DEC_OK) {
        // DecodeFrame
        err = aacDecoder_DecodeFrame(ctx->dec, (INT_PCM *) out, outCap * ctx->channels, 0);
        if (err == AAC_DEC_OK) {
            CStreamInfo *info = aacDecoder_GetStreamInfo(ctx->dec);
            if (info != NULL) {
                samples = info->frameSize;
                ctx->channels = info->channelConfig > 0 ? info->channelConfig : ctx->channels;
            }
        } else {
            samples = -100 - (int) err; // 编码为负值
        }
    } else {
        samples = -200 - (int) err;
    }

    (*env)->ReleaseByteArrayElements(env, inArr, in, JNI_ABORT);
    (*env)->ReleaseShortArrayElements(env, outArr, out, 0);
    return samples;
}

JNIEXPORT void JNICALL
Java_com_wifiaudio_AacDecoder_destroy(JNIEnv *env, jclass clazz, jlong handle) {
    AacCtx *ctx = (AacCtx *) (intptr_t) handle;
    if (ctx == NULL) return;
    if (ctx->dec != NULL) aacDecoder_Close(ctx->dec);
    free(ctx);
}
