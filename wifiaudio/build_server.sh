#!/bin/bash
# 由 server/src 编译出发送端 module/bin/wifiaudio.jar（内含 classes.dex）
# 需要：JDK 17、Android SDK（android.jar + d8）
#   ANDROID_HOME=/path/to/Android/sdk ./build_server.sh
set -e
cd "$(dirname "$0")"

ANDROID_HOME=${ANDROID_HOME:-/opt/android-sdk}
ANDROID_JAR=${ANDROID_JAR:-$(ls $ANDROID_HOME/platforms/android-3*/android.jar 2>/dev/null | tail -1)}
D8=${D8:-$(ls $ANDROID_HOME/build-tools/*/d8 2>/dev/null | tail -1)}

[ -n "$ANDROID_JAR" ] || { echo "找不到 android.jar，请设置 ANDROID_HOME"; exit 1; }
[ -n "$D8" ] || { echo "找不到 d8，请设置 D8"; exit 1; }

rm -rf build/classes build/dex && mkdir -p build/classes build/dex

echo "1/3 javac ..."
javac -nowarn -source 17 -target 17 -bootclasspath "$ANDROID_JAR" \
      -d build/classes server/src/com/wifiaudio/*.java

echo "2/3 d8 -> classes.dex ..."
"$D8" --lib "$ANDROID_JAR" --output build/dex build/classes

echo "3/3 打包 jar ..."
mkdir -p module/bin
rm -f module/bin/wifiaudio.jar
jar cf module/bin/wifiaudio.jar -C build/dex classes.dex

echo "OK -> module/bin/wifiaudio.jar ($(stat -c%s module/bin/wifiaudio.jar) bytes)"
unzip -l module/bin/wifiaudio.jar
