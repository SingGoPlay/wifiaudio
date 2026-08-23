#!/bin/bash
# 构建发声器 APK（纯命令行，qemu 跑 x86-64 SDK 工具）
# 产物: /workspace/release/WifiAudioTone.apk
set -e
cd "$(dirname "$0")"

SDK=/workspace/android-sdk
ANDROID_JAR=$SDK/platforms/android-35/android.jar
D8=$SDK/build-tools/35.0.0/d8
APKSIGNER=$SDK/build-tools/35.0.0/apksigner
BT=$SDK/build-tools/35.0.0
AAPT="qemu-x86_64 $BT/aapt"
ZIPALIGN="qemu-x86_64 $BT/zipalign"
OUT=/workspace/release

mkdir -p build "$OUT"

echo "[1/4] javac ..."
rm -rf build-classes && mkdir -p build-classes
javac -source 8 -target 8 \
  -bootclasspath "$ANDROID_JAR" \
  -d build-classes \
  $(find src -name "*.java")

echo "[2/4] d8 -> classes.dex ..."
rm -rf build-dex && mkdir -p build-dex
"$D8" --release --lib "$ANDROID_JAR" --min-api 26 --output build-dex $(find build-classes -name "*.class")

echo "[3/4] aapt package + zipalign ..."
$AAPT package -f -M AndroidManifest.xml -S res -I "$ANDROID_JAR" -F build/unsigned.apk
zip -q -j build/unsigned.apk build-dex/classes.dex
$ZIPALIGN -f 4 build/unsigned.apk build/aligned.apk

echo "[4/4] keystore + apksigner ..."
if [ ! -f build/tone.keystore ]; then
  keytool -genkeypair -keystore build/tone.keystore -alias tone \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -storepass tone123 -keypass tone123 \
    -dname "CN=WiFiAudioTone, O=WiFiAudioTone, C=CN" 2>/dev/null
fi
"$APKSIGNER" sign --ks build/tone.keystore --ks-key-alias tone \
  --ks-pass pass:tone123 --key-pass pass:tone123 \
  --out "$OUT/WifiAudioTone.apk" build/aligned.apk

echo "OK -> $OUT/WifiAudioTone.apk ($(stat -c%s "$OUT/WifiAudioTone.apk") bytes)"
rm -f build/unsigned.apk build/aligned.apk
rm -rf build-classes build-dex
