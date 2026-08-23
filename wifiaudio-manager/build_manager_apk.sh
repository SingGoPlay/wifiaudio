#!/bin/bash
# 构建 WiFiAudio 管理器 APK
# 产物: build/WifiAudioManager.apk
set -e
cd "$(dirname "$0")"

SDK=/workspace/android-sdk
ANDROID_JAR=$SDK/platforms/android-35/android.jar
D8=$SDK/build-tools/35.0.0/d8
APKSIGNER=$SDK/build-tools/35.0.0/apksigner
BT=$SDK/build-tools/35.0.0
AAPT="qemu-x86_64 $BT/aapt"
ZIPALIGN="qemu-x86_64 $BT/zipalign"

mkdir -p build
echo "[1/5] javac ..."
rm -rf build-classes && mkdir -p build-classes
javac -source 8 -target 8 \
  -bootclasspath "$ANDROID_JAR" \
  -d build-classes \
  $(find app/src -name "*.java")

echo "[2/5] d8 -> classes.dex ..."
rm -rf build-dex && mkdir -p build-dex
"$D8" --release --lib "$ANDROID_JAR" --min-api 26 --output build-dex $(find build-classes -name "*.class")

echo "[3/5] aapt package ..."
cd app
$AAPT package -f -M AndroidManifest.xml -S res -I "$ANDROID_JAR" -F ../build/unsigned.apk
cd ..
zip -q -j build/unsigned.apk build-dex/classes.dex

echo "[3.5/5] zipalign ..."
$ZIPALIGN -f 4 build/unsigned.apk build/aligned.apk

echo "[4/5] keystore ..."
if [ ! -f build/manager.keystore ]; then
  keytool -genkeypair -keystore build/manager.keystore -alias mgr \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -storepass mgr123 -keypass mgr123 \
    -dname "CN=WiFiAudioManager, O=WiFiAudioManager, C=CN" 2>/dev/null
fi

echo "[5/5] apksigner ..."
"$APKSIGNER" sign --ks build/manager.keystore --ks-key-alias mgr \
  --ks-pass pass:mgr123 --key-pass pass:mgr123 \
  --out build/WifiAudioManager.apk build/aligned.apk

echo "OK -> build/WifiAudioManager.apk ($(stat -c%s build/WifiAudioManager.apk) bytes)"
rm -f build/unsigned.apk build/aligned.apk
rm -rf build-classes build-dex
