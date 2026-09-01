#!/system/bin/sh
# WiFiAudio 控制脚本（供 service.sh 与 WiFiAudio 管理器调用）
# 用法:
#   wifiaudio.sh start|stop|restart|status|get <key>|set <key> <value>|set-all <base64-json>

MODDIR=${0%/*}/..
SDIR=/storage/emulated/0/WiFiAudio
CONFIG=$SDIR/config.json
JAR="$MODDIR/bin/wifiaudio.jar"
PIDFILE="$SDIR/capture.pid"
STATUSFILE="$SDIR/status.json"
LOG="$SDIR/capture.log"
SLOG="$SDIR/service.log"
mkdir -p "$SDIR" "$MODDIR/log" 2>/dev/null

# 默认配置（JSON）
write_default_config() {
    cat > "$CONFIG" <<'EOF'
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
}

# 配置文件不存在或无效时生成默认
if [ ! -f "$CONFIG" ] || ! grep -q '"enabled"' "$CONFIG" 2>/dev/null; then
    write_default_config
fi

get_pid() {
    [ -f "$PIDFILE" ] || return 1
    pid=$(cat "$PIDFILE" 2>/dev/null)
    [ -n "$pid" ] || return 1
    if kill -0 "$pid" 2>/dev/null; then
        echo "$pid"
        return 0
    fi
    rm -f "$PIDFILE"
    return 1
}

is_running() {
    get_pid >/dev/null 2>&1
}

get_ip() {
    ip=$(ip route get 1.1.1.1 2>/dev/null | grep -oE 'src [0-9.]+' | head -1 | awk '{print $2}')
    if [ -z "$ip" ]; then
        ip=$(getprop dhcp.wlan0.ipaddress 2>/dev/null)
    fi
    [ -z "$ip" ] && ip="127.0.0.1"
    echo "$ip"
}

# 从 JSON 提取键值（busybox 兼容，无 jq）
get_cfg() {
    key="$1"
    grep -o "\"${key}\"[[:space:]]*:[[:space:]]*[^,}]*" "$CONFIG" 2>/dev/null | head -1 | cut -d: -f2- | tr -d ' "'
}

start() {
    if is_running; then
        echo "already running (pid $(get_pid))"
        return 0
    fi
    enabled=$(get_cfg enabled)
    if [ "$enabled" != "1" ]; then
        echo "ERROR: enabled!=1 (current=$enabled), 请先在 WiFiAudio 管理器开启广播"
        return 1
    fi
    if [ ! -f "$JAR" ]; then
        echo "ERROR: jar 不存在: $JAR"
        return 1
    fi
    echo "starting app_process ..."
    setsid nohup /system/bin/app_process \
        -Djava.class.path="$JAR" \
        /system/bin \
        --nice-name=wifiaudio-capture \
        com.wifiaudio.Main \
        --config "$CONFIG" \
        >"$LOG" 2>&1 < /dev/null &
    sleep 2
    if is_running; then
        echo "started (pid $(get_pid))"
        return 0
    fi
    echo "启动中，请稍后刷新查看状态；若 10 秒后仍未运行，查看日志: $LOG"
    return 0
}

stop() {
    pid=$(get_pid)
    if [ -z "$pid" ]; then
        echo "not running"
        return 0
    fi
    kill "$pid" 2>/dev/null
    for i in 1 2 3 4 5 6 7 8; do
        sleep 1
        is_running || break
    done
    is_running && kill -9 "$pid" 2>/dev/null
    rm -f "$PIDFILE"
    echo "stopped"
}

status() {
    if is_running; then
        pid=$(get_pid)
        if [ -f "$STATUSFILE" ]; then
            if grep -q '"ip"' "$STATUSFILE" 2>/dev/null; then
                cat "$STATUSFILE"
            else
                sed "s/\"running\":true/\"running\":true,\"ip\":\"$(get_ip)\"/" "$STATUSFILE"
            fi
        else
            echo "{\"running\":true,\"pid\":$pid,\"ip\":\"$(get_ip)\",\"note\":\"status.json not ready yet\"}"
        fi
    else
        echo "{\"running\":false,\"ip\":\"$(get_ip)\"}"
    fi
}

set_cfg() {
    key="$1"
    value="$2"
    if [ -z "$key" ]; then
        echo "usage: set <key> <value>"
        return 1
    fi
    # 简单替换 JSON 键值
    if grep -q "\"${key}\"" "$CONFIG" 2>/dev/null; then
        sed -i "s/\"${key}\"[[:space:]]*:[[:space:]]*[^,}]*/\"${key}\": \"${value}\"/" "$CONFIG"
    else
        sed -i "s/}/, \"${key}\": \"${value}\"}/" "$CONFIG"
    fi
    echo "set ${key}=${value}"
}

read_config() {
    if [ -f "$CONFIG" ]; then
        cat "$CONFIG"
    else
        echo "{}"
    fi
}

set_all() {
    b64="$1"
    if [ -z "$b64" ]; then
        echo "usage: set-all <base64-json>"
        return 1
    fi
    if echo "$b64" | base64 -d > "$CONFIG" 2>/dev/null; then
        sz=$(wc -c < "$CONFIG" 2>/dev/null)
        if [ -n "$sz" ] && [ "$sz" -gt 30 ] && grep -q '"enabled"' "$CONFIG"; then
            echo "OK config written ($sz bytes)"
            return 0
        fi
        echo "FAILED: 写入内容异常"
        return 1
    fi
    echo "FAILED: base64 解码失败"
    return 1
}

case "$1" in
    start) start ;;
    stop) stop ;;
    restart) stop; start ;;
    status) status ;;
    get) get_cfg "$2" ;;
    set) set_cfg "$2" "$3" ;;
    set-all) set_all "$2" ;;
    read-config) read_config ;;
    *) echo "usage: $0 {start|stop|restart|status|get <key>|set <key> <value>|set-all <base64>|read-config}" ;;
esac
