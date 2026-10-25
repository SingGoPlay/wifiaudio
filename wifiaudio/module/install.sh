SKIPMOUNT=false
LATESTARTSERVICE=true
POSTFSDATA=false
PROPFILE=false
print_modname() {
 ui_print "*******************************"
 ui_print "     	Magisk Module        "
 ui_print "Make By 小白杨（爱玩机工具箱）"
 ui_print "*******************************"
}
on_install() {
 ui_print "- 正在释放文件"
 unzip -o "$ZIPFILE" 'bin/wifiaudio.jar' -d $MODPATH >&2
 unzip -o "$ZIPFILE" 'bin/wifiaudio.sh' -d $MODPATH >&2
 unzip -o "$ZIPFILE" 'config.conf' -d $MODPATH >&2
 unzip -o "$ZIPFILE" 'icon.png' -d $MODPATH >&2
 unzip -o "$ZIPFILE" 'presets/movie.conf' -d $MODPATH >&2
 unzip -o "$ZIPFILE" 'presets/music.conf' -d $MODPATH >&2
 unzip -o "$ZIPFILE" 'presets/game.conf' -d $MODPATH >&2
 unzip -o "$ZIPFILE" 'sepolicy.rule' -d $MODPATH >&2
 unzip -o "$ZIPFILE" 'uninstall.sh' -d $MODPATH >&2
 unzip -o "$ZIPFILE" 'webroot/index.html' -d $MODPATH >&2
}
set_permissions() {
 set_perm_recursive $MODPATH 0 0 0755 0644
#设置权限，基本不要去动
}