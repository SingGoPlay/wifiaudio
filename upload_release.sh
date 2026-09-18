#!/bin/bash
# 一键发布：把 release/ 里的安装包上传到 GitHub Releases
# 用法:
#   GH_TOKEN=你的token ./upload_release.sh                  # 用最新 tag
#   GH_TOKEN=你的token ./upload_release.sh v4.18 "描述"
#
# 安全特性（v4.18 增加，起因是曾误传旧版 APK 到 Release）：
#   1) 上传前校验每个 APK 的 versionName 是否与 tag 一致（有 aapt 时）；不一致直接中止
#   2) 复用已存在的 Release（不重建），同名资产先删后传（可安全重跑）
#   3) 上传后用 GitHub 返回的 sha256 digest 与本地 sha256 比对，逐项报 ✓/✗
set -e
TOKEN=${GH_TOKEN:?请设置 GH_TOKEN 环境变量（GitHub Personal Access Token）}
REPO="SingGoPlay/wifiaudio"
TAG="${1:-v4.18}"
BODY="${2:-WiFiAudio 发行版}"
cd "$(dirname "$0")"

FILES="release/WifiAudioReceiver.apk release/WifiAudioManager.apk release/WifiAudioTone.apk release/wifiaudio-ksu.zip"

# ---------- 0. 版本号校验（防呆：release/ 里可能残留旧版 APK）----------
VER="${TAG#v}"
AAPT=""
# 探测可用的 aapt（沙箱里 build-tools 是 x86_64 二进制，需 qemu 接管）
try_aapt() {
  [ -x "$1" ] || return 1
  if "$1" version >/dev/null 2>&1; then echo "$1"; return 0; fi
  if command -v qemu-x86_64 >/dev/null 2>&1 && qemu-x86_64 "$1" version >/dev/null 2>&1; then
    echo "qemu-x86_64 $1"; return 0
  fi
  return 1
}
for c in $(command -v aapt 2>/dev/null) /workspace/android-sdk/build-tools/*/aapt; do
  if AAPT=$(try_aapt "$c"); then break; fi
done
echo "【版本校验】目标版本 $VER"
if [ -n "$AAPT" ]; then
  for f in release/WifiAudioReceiver.apk release/WifiAudioManager.apk release/WifiAudioTone.apk; do
    [ -f "$f" ] || { echo "✗ 缺少 $f"; exit 1; }
    got=$($AAPT dump badging "$f" 2>/dev/null | head -1 | sed -n "s/.*versionName='\([^']*\)'.*/\1/p")
    if [ "$got" != "$VER" ]; then
      echo "✗ $f 版本为 $got，与目标 $VER 不一致 —— 中止！"
      echo "  （请先重新构建并 cp 到 release/，勿发布旧包）"
      exit 1
    fi
    echo "  ✓ $(basename "$f") = $got"
  done
else
  echo "  ⚠ 未找到 aapt，跳过版本校验。将上传的文件指纹如下，请自行确认："
  for f in release/*.apk release/*.zip; do [ -f "$f" ] && echo "    $(sha256sum "$f")"; done
fi

# ---------- 1. 打 tag 并推送 ----------
if ! git rev-parse "$TAG" >/dev/null 2>&1; then
  echo "创建 tag $TAG ..."
  git tag "$TAG"
  git push origin "$TAG"
else
  echo "tag $TAG 已存在，跳过创建"
fi

# ---------- 2. 获取（或创建）Release ----------
echo "获取/创建 Release $TAG ..."
RID=$(curl -s -H "Authorization: token $TOKEN" -H "Accept: application/vnd.github+json" \
  "https://api.github.com/repos/$REPO/releases/tags/$TAG" \
  | python3 -c "import json,sys;print(json.load(sys.stdin).get('id',''))" 2>/dev/null || echo "")
if [ -z "$RID" ]; then
  echo "  Release 不存在，创建中 ..."
  RID=$(curl -s -X POST -H "Authorization: token $TOKEN" -H "Accept: application/vnd.github+json" \
    "https://api.github.com/repos/$REPO/releases" \
    -d "{\"tag_name\":\"$TAG\",\"name\":\"$TAG\",\"body\":\"$BODY\",\"draft\":false,\"prerelease\":false}" \
    | python3 -c "import json,sys; d=json.load(sys.stdin); print(d.get('id',''))" 2>/dev/null || echo "")
fi
[ -n "$RID" ] || { echo "✗ 无法获取 Release ID"; exit 1; }
echo "Release ID: $RID"

# ---------- 3. 逐个：删同名旧资产 -> 上传 -> 校验 sha256 ----------
for f in $FILES; do
  [ -f "$f" ] || { echo "跳过（不存在）: $f"; continue; }
  NAME=$(basename "$f")
  LOCAL_SHA=$(sha256sum "$f" | cut -d' ' -f1)
  OLD_ID=$(curl -s -H "Authorization: token $TOKEN" \
    "https://api.github.com/repos/$REPO/releases/$RID/assets?per_page=100" \
    | python3 -c "
import json,sys
for a in json.load(sys.stdin):
    if a['name']=='$NAME': print(a['id'])
" 2>/dev/null || echo "")
  if [ -n "$OLD_ID" ]; then
    echo "删除同名旧资产 $NAME (id=$OLD_ID) ..."
    curl -s -o /dev/null -X DELETE -H "Authorization: token $TOKEN" \
      "https://api.github.com/repos/$REPO/releases/assets/$OLD_ID"
  fi
  echo "上传 $NAME ($(stat -c%s "$f") bytes) ..."
  RESP=$(curl -s -X POST -H "Authorization: token $TOKEN" -H "Content-Type: application/octet-stream" \
    --data-binary "@$f" \
    "https://uploads.github.com/repos/$REPO/releases/$RID/assets?name=$NAME")
  echo "$RESP" | python3 -c "
import json,sys
d=json.load(sys.stdin)
name=d.get('name'); size=d.get('size',0); dig=(d.get('digest') or '').replace('sha256:','')
local='$LOCAL_SHA'
if not name:
    print('  ✗ 上传失败:', d.get('errors') or d); sys.exit(1)
ok = (dig == local) if dig else None
print('  ✓', name, size, 'bytes', 'sha256 一致' if ok else ('sha256 校验失败! 远端=%s 本地=%s' % (dig, local) if ok is False else '(无 digest)'))
sys.exit(0 if ok is not False else 1)
"
done

echo ""
echo "完成: https://github.com/$REPO/releases/tag/$TAG"
