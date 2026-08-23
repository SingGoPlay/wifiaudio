#!/system/bin/sh
# WiFiAudio 安装脚本
# 同时适配 KernelSU 与 Magisk：
#   - KernelSU: 环境变量 KSU=true，SELinux 域 u:r:ksu:s0（permissive），支持 WebUI
#   - Magisk:   环境变量 MAGISK_VER，SELinux 域 u:r:magisk:s0，无官方 WebUI（可手动编辑配置）
# 配置统一存放在 /storage/emulated/0/WiFiAudio/config.json（用户可直接访问）
# 安装时按平台动态生成 sepolicy.rule（避免不存在的域导致策略编译失败）

ui_print "- WiFiAudio 安装中..."

# 显示模块版本信息
MODVER=$(grep '^version=' "$MODPATH/module.prop" 2>/dev/null | head -1 | cut -d= -f2)
MODNAME=$(grep '^name=' "$MODPATH/module.prop" 2>/dev/null | head -1 | cut -d= -f2)
ui_print "- 模块: ${MODNAME:-WiFiAudio} 版本: ${MODVER:-?}"

# 环境自检
if [ -w "$MODPATH" ]; then
    ui_print "- 模块目录可写 ✓"
else
    ui_print "- 警告: 模块目录不可写! 安装后可能无法运行"
fi

# 平台检测（Magisk v30 起不再注入 MAGISK_VER；KSU 注入 KSU=true）
if [ -n "$KSU" ]; then
    ui_print "- 平台: KernelSU (版本 $KSU_VER)"
    PLATFORM=ksu
else
    ui_print "- 平台: Magisk"
    PLATFORM=magisk
fi

# 设置脚本权限
chmod 0755 "$MODPATH/bin/wifiaudio.sh"
chmod 0755 "$MODPATH/service.sh"
chmod 0755 "$MODPATH/customize.sh"
chmod 0755 "$MODPATH/uninstall.sh"
# OPUS 编码库

# 按平台生成 sepolicy.rule（只含当前平台的域，避免另一平台编译报错）
if [ "$PLATFORM" = "ksu" ]; then
    # KernelSU: ksu 域本身 permissive + allow-all，以下为冗余保险
    cat > "$MODPATH/sepolicy.rule" <<'EOF'
# WiFiAudio SELinux 规则 (KernelSU)
allow ksu audioserver:binder call
allow ksu audioserver:fd use
allow ksu audio_service:binder call
allow ksu system_server:binder call
allow ksu servicemanager:binder call
allow ksu shell:process sigchld
EOF
else
    # Magisk: magisk 域非 permissive，必须显式放行
    cat > "$MODPATH/sepolicy.rule" <<'EOF'
# WiFiAudio SELinux 规则 (Magisk)
allow magisk audioserver:binder call
allow magisk audioserver:fd use
allow magisk audio_service:binder call
allow magisk system_server:binder call
allow magisk servicemanager:binder call
allow magisk shell:process sigchld
EOF
fi
ui_print "- 已生成 $PLATFORM 平台的 sepolicy 规则"

# 默认配置（若安装包带默认配置则保留；不带则服务启动时自动生成）
if [ -f "$MODPATH/config.conf" ]; then
    ui_print "- 已包含配置文件"
else
    ui_print "- 配置文件将在首次启动时自动生成（默认关闭，最省电）"
    ui_print "- 配置文件位置: /storage/emulated/0/WiFiAudio/config.json"
fi

if [ "$PLATFORM" = "magisk" ]; then
    ui_print "- 提示: Magisk 无官方 WebUI"
    ui_print "- 配置方法: 编辑 /storage/emulated/0/WiFiAudio/config.json 后执行"
    ui_print "-   su -c 'sh $MODPATH/bin/wifiaudio.sh start'"
fi
ui_print "- 安装完成！默认关闭。启用前请确认两设备处于同一 WiFi 网络"
