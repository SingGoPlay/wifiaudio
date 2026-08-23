#!/system/bin/sh
# WiFiAudio 手动模式（默认）
# 开机时不启动任何进程、不读取配置、不检查任何东西。
# 主进程仅由 WiFiAudio 管理器 / WebUI 手动唤起（wifiaudio.sh start）。
MODDIR=${0%/*}
SDIR=/storage/emulated/0/WiFiAudio
mkdir -p "$MODDIR/log" "$SDIR" 2>/dev/null
echo "[$(date '+%m-%d %H:%M:%S')] 手动模式：开机不自动启动（需要时请在管理器/WebUI 点「启用广播」）" >> "$SDIR/service.log" 2>/dev/null
exit 0
