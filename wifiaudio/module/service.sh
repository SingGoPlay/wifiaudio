#!/system/bin/sh
# WiFiAudio late_start 服务脚本
# 默认不启动（最省电）；仅当配置 enabled=1 时启动捕获服务
# 所有配置请在 KernelSU/Magisk 模块 WebUI 中修改（配置存 /storage/emulated/0/WiFiAudio/config.json）

MODDIR=${0%/*}
SDIR=/storage/emulated/0/WiFiAudio
mkdir -p "$MODDIR/log" "$SDIR" 2>/dev/null
SLOG="$SDIR/service.log"

# 平台检测（KSU 注入 KSU=true；Magisk v30 不注入版本变量）
if [ -n "$KSU" ]; then
    echo "[$(date '+%m-%d %H:%M:%S')] WiFiAudio: running on KernelSU" >> "$SLOG" 2>/dev/null
else
    echo "[$(date '+%m-%d %H:%M:%S')] WiFiAudio: running on Magisk" >> "$SLOG" 2>/dev/null
fi

# 等待系统启动完成，确保系统服务可用
for i in 1 2 3 4 5 6 7 8 9 10; do
    [ "$(getprop sys.boot_completed)" = "1" ] && break
    sleep 3
done

# 默认配置不存在时写入默认配置（JSON，用户可直接查看/编辑）
if [ ! -f "$SDIR/config.json" ] || ! grep -q '"enabled"' "$SDIR/config.json" 2>/dev/null; then
    cat > "$SDIR/config.json" <<'EOF'
{
  "enabled": 0,
  "mode": "music",
  "local_render": 0,
  "transport": "tcp",
  "tcp_port": 47800,
  "http_port": 47801,
  "codec": "pcm",
  "pcm_rate": "auto",
  "aac_bitrate": 192,
  "opus_bitrate": 128,
  "capture_call": 0
}
EOF
    echo "[$(date '+%m-%d %H:%M:%S')] config.json created (default)" >> "$SLOG" 2>/dev/null
fi

# 依据配置决定是否启动
enabled=$(grep -o '"enabled"[[:space:]]*:[[:space:]]*[^,}]*' "$SDIR/config.json" 2>/dev/null | head -1 | cut -d: -f2- | tr -d ' "')
if [ "$enabled" = "1" ]; then
    echo "[$(date '+%m-%d %H:%M:%S')] enabled=1, starting service" >> "$SLOG" 2>/dev/null
    "$MODDIR/bin/wifiaudio.sh" start >> "$SLOG" 2>&1
else
    echo "[$(date '+%m-%d %H:%M:%S')] enabled=0, service stays off (省电)" >> "$SLOG" 2>/dev/null
fi
