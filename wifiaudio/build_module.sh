#!/bin/bash
# 打包 WiFiAudio KernelSU 模块
set -e
cd "$(dirname "$0")"

rm -rf build/module && mkdir -p build/module
cp -r module/* build/module/
rm -rf build/module/log

# 确保服务 jar 存在
[ -f build/module/bin/wifiaudio.jar ] || { echo "缺少 wifiaudio.jar，请先运行 server/build.sh"; exit 1; }

# 修正换行与权限
find build/module -type f -name "*.sh" -exec sed -i 's/\r$//' {} \;
chmod 0755 build/module/bin/wifiaudio.sh build/module/service.sh build/module/customize.sh build/module/uninstall.sh

cd build/module
rm -f ../wifiaudio-ksu.zip
zip -r -9 ../wifiaudio-ksu.zip . > /dev/null
cd ..
echo "OK -> build/wifiaudio-ksu.zip ($(stat -c%s wifiaudio-ksu.zip) bytes)"
unzip -l wifiaudio-ksu.zip
