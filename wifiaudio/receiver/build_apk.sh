#!/bin/bash
# 构建 WiFiAudio 接收端 APK（无第三方依赖，纯命令行）
# 说明: 沙箱为 ARM64，SDK 的 aapt/zipalign 为 x86-64，通过 qemu-user 运行。
# 产物: ../build/WifiAudioReceiver.apk
set -e

SDK=/workspace/android-sdk
ANDROID_JAR=$SDK/platforms/android-35/android.jar
D8=$SDK/build-tools/35.0.0/d8
APKSIGNER=$SDK/build-tools/35.0.0/apksigner
BT=$SDK/build-tools/35.0.0
AAPT="qemu-x86_64 $BT/aapt"
ZIPALIGN="qemu-x86_64 $BT/zipalign"

cd "$(dirname "$0")"
OUT=../build
mkdir -p $OUT

echo "[1/5] javac ..."
rm -rf build-classes && mkdir -p build-classes
javac -source 8 -target 8 \
  -bootclasspath "$ANDROID_JAR" \
  -d build-classes \
  $(find src -name "*.java")

echo "[2/5] d8 -> classes.dex ..."
rm -rf build-dex && mkdir -p build-dex
"$D8" --release --lib "$ANDROID_JAR" --min-api 26 --output build-dex $(find build-classes -name "*.class")

echo "[3/5] aapt package ..."
$AAPT package -f -M AndroidManifest.xml -S res -I "$ANDROID_JAR" -F "$OUT/unsigned.apk"
zip -q -j "$OUT/unsigned.apk" build-dex/classes.dex

# 加入 native lib（libopus/fdk-aac 软解，AAudio；64 位 + 32 位）
mkdir -p lib/arm64-v8a lib/armeabi-v7a
# 64 位
cp ../server/jni/libwifiaudio_opus_decode.so lib/arm64-v8a/
cp ../server/jni/libwifiaudio_aac_lld.so lib/arm64-v8a/libwifiaudio_aac_decode.so
cp ../server/jni/libwifiaudio_aaudio.so lib/arm64-v8a/
# 32 位 (armeabi-v7a)
cp ../server/jni/libwifiaudio_opus_decode_v7.so lib/armeabi-v7a/libwifiaudio_opus_decode.so
cp ../server/jni/libwifiaudio_aac_decode_v7.so lib/armeabi-v7a/libwifiaudio_aac_decode.so
cp ../server/jni/libwifiaudio_aaudio_v7.so lib/armeabi-v7a/libwifiaudio_aaudio.so
(cd . && zip -q -r "$OUT/unsigned.apk" lib)

echo "[3.5/5] zipalign ..."
$ZIPALIGN -f 4 "$OUT/unsigned.apk" "$OUT/aligned.apk"

echo "[4/5] keystore ..."
if [ ! -f "$OUT/wifiaudio.keystore" ]; then
  keytool -genkeypair -keystore "$OUT/wifiaudio.keystore" -alias wifiaudio \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -storepass wifiaudio123 -keypass wifiaudio123 \
    -dname "CN=WiFiAudio, OU=WiFiAudio, O=WiFiAudio, C=CN" 2>/dev/null
fi

echo "[5/5] apksigner ..."
"$APKSIGNER" sign --ks "$OUT/wifiaudio.keystore" --ks-key-alias wifiaudio \
  --ks-pass pass:wifiaudio123 --key-pass pass:wifiaudio123 \
  --out "$OUT/WifiAudioReceiver.apk" "$OUT/aligned.apk"

echo "OK -> $OUT/WifiAudioReceiver.apk ($(stat -c%s $OUT/WifiAudioReceiver.apk) bytes)"
rm -f "$OUT/unsigned.apk" "$OUT/aligned.apk"
rm -rf build-classes build-dex
