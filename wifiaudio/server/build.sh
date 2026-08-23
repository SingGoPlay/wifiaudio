#!/bin/bash
# 编译 WiFiAudio 捕获服务：javac -> jar -> d8(dex) -> 最终 jar(含 classes.dex)
# 产物: ../module/bin/wifiaudio.jar
set -e

SDK=/workspace/android-sdk
ANDROID_JAR=$SDK/platforms/android-35/android.jar
D8=$SDK/build-tools/35.0.0/d8

cd "$(dirname "$0")"
rm -rf build && mkdir -p build/classes build/dex

echo "[1/3] javac ..."
javac -source 8 -target 8 \
  -bootclasspath "$ANDROID_JAR" \
  -d build/classes \
  $(find src -name "*.java")

echo "[2/3] jar (class) ..."
jar --create --file build/wifiaudio-classes.jar -C build/classes .

echo "[3/3] d8 -> dex -> jar ..."
"$D8" --release --lib "$ANDROID_JAR" --min-api 26 --output build/dex build/wifiaudio-classes.jar
jar --create --file ../module/bin/wifiaudio.jar -C build/dex classes.dex

echo "OK -> ../module/bin/wifiaudio.jar ($(stat -c%s ../module/bin/wifiaudio.jar) bytes)"
