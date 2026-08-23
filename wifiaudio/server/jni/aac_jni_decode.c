/*
 * WiFiAudio AAC 解码 JNI 封装（接收端软解，fdk-aac）
 * 静态包含 fdk-aac（NDK clang 编译），仅动态依赖 bionic libc/libm/libdl/liblog。
 */
#include <jni.h>
#include <aacdecoder_lib.h>
#include <android/log.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "AacSoft", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "AacSoft", __VA_ARGS__)

typedef struct {
    HANDLE_AACDECODER dec;
    int channels;
} AacCtx;

JNIEXPORT jlong JNICALL
Java_com_wifiaudio_AacDecoder_init(JNIEnv *env, jclass clazz,
                                   jint sampleRate, jint channels, jbyteArray ascArr) {
    LOGI("init: rate=%d ch=%d", sampleRate, channels);
    if (ascArr == NULL) {
        LOGE("init: ascArr is NULL");
        return 0;
    }
    jsize ascLen = (*env)->GetArrayLength(env, ascArr);
    jbyte *asc = (*env)->GetByteArrayElements(env, ascArr, NULL);
    if (asc == NULL) {
        LOGE("init: GetByteArrayElements failed");
        return 0;
    }
    LOGI("init: ascLen=%d asc[0]=%02X asc[1]=%02X", (int) ascLen, (unsigned char) asc[0], (unsigned char) asc[1]);

    HANDLE_AACDECODER dec = aacDecoder_Open(TT_MP4_ADTS, 1);
    LOGI("init: aacDecoder_Open -> %p", (void *) dec);
    if (dec == NULL) {
        (*env)->ReleaseByteArrayElements(env, ascArr, asc, JNI_ABORT);
        return 0;
    }

    UCHAR *csd[1] = { (UCHAR *) asc };
    UINT csdLen[1] = { (UINT) ascLen };
    AAC_DECODER_ERROR err = aacDecoder_ConfigRaw(dec, csd, csdLen);
    LOGI("init: ConfigRaw -> %u (0x%X)", (unsigned) err, (unsigned) err);
    (*env)->ReleaseByteArrayElements(env, ascArr, asc, JNI_ABORT);
    if (err != AAC_DEC_OK) {
        LOGE("init: ConfigRaw failed err=%u", (unsigned) err);
        aacDecoder_Close(dec);
        return 0;
    }

    AacCtx *ctx = (AacCtx *) calloc(1, sizeof(AacCtx));
    if (ctx == NULL) {
        LOGE("init: calloc failed");
        aacDecoder_Close(dec);
        return 0;
    }
    ctx->dec = dec;
    ctx->channels = channels;
    LOGI("init: OK, handle=%lld", (long long) (intptr_t) ctx);
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
        return 0;
    }
    jsize outCap = (*env)->GetArrayLength(env, outArr) / ctx->channels;

    UCHAR *buf = (UCHAR *) in;
    UINT bufSize = (UINT) inLen;
    UINT bytesValid = (UINT) inLen;
    AAC_DECODER_ERROR err = aacDecoder_Fill(ctx->dec, &buf, &bufSize, &bytesValid);
    if (err != AAC_DEC_OK) {
        LOGI("decode: Fill err=%u bytesValid=%u/%u", (unsigned) err, (unsigned) bytesValid, (unsigned) inLen);
    }

    int samples = 0;
    if (err == AAC_DEC_OK) {
        err = aacDecoder_DecodeFrame(ctx->dec, (INT_PCM *) out, outCap * ctx->channels, 0);
        if (err != AAC_DEC_OK) {
            LOGI("decode: DecodeFrame err=%u (0x%X) bytesValid=%u/%u", (unsigned) err, (unsigned) err, (unsigned) bytesValid, (unsigned) inLen);
        }
        if (err == AAC_DEC_OK) {
            CStreamInfo *info = aacDecoder_GetStreamInfo(ctx->dec);
            if (info != NULL) {
                samples = info->frameSize;
                if (info->channelConfig > 0) ctx->channels = info->channelConfig;
            }
        } else {
            samples = -100 - (int) err;
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

/* android_errorWriteLog stub（lpp_tran.cpp 引用，无需真实日志） */
int android_errorWriteLog(int tag, const char* subTag) {
    (void)tag;
    (void)subTag;
    return 0;
}
