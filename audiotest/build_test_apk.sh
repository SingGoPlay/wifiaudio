#!/bin/bash
# 构建 WiFiAudio 解码测试 APK
# 产物: build/WiFiAudioTest.apk
set -e
cd "$(dirname "$0")"

SDK=/workspace/android-sdk
ANDROID_JAR=$SDK/platforms/android-35/android.jar
D8=$SDK/build-tools/35.0.0/d8
APKSIGNER=$SDK/build-tools/35.0.0/apksigner
BT=$SDK/build-tools/35.0.0
AAPT="qemu-x86_64 $BT/aapt"
ZIPALIGN="qemu-x86_64 $BT/zipalign"

echo "[1/5] javac ..."
rm -rf build-classes && mkdir -p build-classes
javac -source 8 -target 8 \
  -bootclasspath "$ANDROID_JAR" \
  -d build-classes \
  $(find app/src -name "*.java")

echo "[2/5] d8 -> classes.dex ..."
rm -rf build-dex && mkdir -p build-dex
"$D8" --release --lib "$ANDROID_JAR" --min-api 26 --output build-dex $(find build-classes -name "*.class")

echo "[3/5] aapt package (assets + res) ..."
cd app
$AAPT package -f -M AndroidManifest.xml -S res -A assets -I "$ANDROID_JAR" -F ../build/unsigned.apk
cd ..
zip -q -j build/unsigned.apk build-dex/classes.dex

# native libs（软解 + AAudio）
mkdir -p lib/arm64-v8a
cp jni/libwifiaudio_aaudio.so lib/arm64-v8a/
cp ../wifiaudio/server/jni/libwifiaudio_opus_decode.so lib/arm64-v8a/
cp ../wifiaudio/server/jni/libwifiaudio_aac_decode.so lib/arm64-v8a/
(cd . && zip -q -r build/unsigned.apk lib)

echo "[3.5/5] zipalign ..."
$ZIPALIGN -f 4 build/unsigned.apk build/aligned.apk

echo "[4/5] keystore ..."
if [ ! -f build/test.keystore ]; then
  keytool -genkeypair -keystore build/test.keystore -alias test \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -storepass test123 -keypass test123 \
    -dname "CN=WiFiAudioTest, O=WiFiAudioTest, C=CN" 2>/dev/null
fi

echo "[5/5] apksigner ..."
"$APKSIGNER" sign --ks build/test.keystore --ks-key-alias test \
  --ks-pass pass:test123 --key-pass pass:test123 \
  --out build/WiFiAudioTest.apk build/aligned.apk

echo "OK -> build/WiFiAudioTest.apk ($(stat -c%s build/WiFiAudioTest.apk) bytes)"
rm -f build/unsigned.apk build/aligned.apk
rm -rf build-classes build-dex
