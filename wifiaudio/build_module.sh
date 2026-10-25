#!/bin/bash
# 打包发送端 KernelSU/Magisk 刷机包（还原自 v3.9 产物结构）
# 结构：install.sh + common/service.sh + META-INF/update-binary（Magisk 标准刷机包）
set -e
cd "$(dirname "$0")"

[ -f module/bin/wifiaudio.jar ] || { echo "缺少 module/bin/wifiaudio.jar，请先运行 build_server.sh"; exit 1; }

# 修正 CRLF 与权限
find module -type f -name "*.sh" -exec sed -i 's/\r$//' {} \;
find module/META-INF -type f -exec sed -i 's/\r$//' {} \; 2>/dev/null || true
chmod 0755 module/bin/wifiaudio.sh module/common/service.sh module/install.sh module/uninstall.sh

OUT=wifiaudio-ksu-v3.9.zip
rm -f "$OUT"
( cd module && zip -r -9 "../$OUT" . > /dev/null )

echo "OK -> $OUT ($(stat -c%s "$OUT") bytes)"
unzip -l "$OUT"
