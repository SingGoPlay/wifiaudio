#!/system/bin/sh
# WiFiAudio 卸载脚本
MODDIR=${0%/*}
"$MODDIR/bin/wifiaudio.sh" stop 2>/dev/null
rm -f "$MODDIR/capture.pid" "$MODDIR/status.json"
exit 0
