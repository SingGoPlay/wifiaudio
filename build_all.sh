#!/bin/bash
# WiFiAudio 全家桶一键构建
# 产物:
#   wifiaudio/build/wifiaudio-ksu.zip        (发送端模块)
#   wifiaudio/build/WifiAudioReceiver.apk    (接收端)
#   wifiaudio-manager/build/WifiAudioManager.apk (管理 App)
set -e
cd "$(dirname "$0")"

echo "════════ 1/3 构建发送端模块 ════════"
cd wifiaudio && bash build_module.sh

echo "════════ 2/3 构建接收端 APK ════════"
cd receiver && bash build_apk.sh

echo "════════ 3/3 构建管理 App ════════"
cd ../.. && cd wifiaudio-manager && bash build_manager_apk.sh

echo ""
echo "════════ 全部完成 ════════"
ls -la /workspace/wifiaudio/build/wifiaudio-ksu.zip \
       /workspace/wifiaudio/build/WifiAudioReceiver.apk \
       /workspace/wifiaudio-manager/build/WifiAudioManager.apk
