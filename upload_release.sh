#!/bin/bash
# 一键发布：把 release/ 里的安装包上传到 GitHub Releases
# 用法:
#   GH_TOKEN=你的token ./upload_release.sh            # 用 vX.Y 最新 tag
#   GH_TOKEN=你的token ./upload_release.sh v5.0 "描述"
set -e
TOKEN=${GH_TOKEN:?请设置 GH_TOKEN 环境变量（GitHub Personal Access Token）}
REPO="SingGoPlay/wifiaudio"
TAG="${1:-v4.18}"
BODY="${2:-WiFiAudio 发行版}"
cd "$(dirname "$0")"

# 1. 打 tag 并推送（如果还没打）
if ! git rev-parse "$TAG" >/dev/null 2>&1; then
  echo "创建 tag $TAG ..."
  git tag "$TAG"
  git push origin "$TAG"
fi

# 2. 创建 Release（已存在则复用）
echo "创建/获取 Release $TAG ..."
RID=$(curl -s -X POST -H "Authorization: token $TOKEN" -H "Accept: application/vnd.github+json" \
  "https://api.github.com/repos/$REPO/releases" \
  -d "{\"tag_name\":\"$TAG\",\"name\":\"$TAG\",\"body\":\"$BODY\",\"draft\":false,\"prerelease\":false}" \
  | python3 -c "import json,sys; d=json.load(sys.stdin); print(d.get('id', d.get('errors',[{}])[0].get('code','')))" 2>/dev/null)
echo "Release ID: $RID"

# 3. 上传安装包
for f in release/WifiAudioReceiver.apk release/WifiAudioManager.apk \
         release/WifiAudioTone.apk release/wifiaudio-ksu.zip; do
  [ -f "$f" ] || { echo "跳过（不存在）: $f"; continue; }
  echo "上传 $(basename $f) ..."
  curl -s -X POST -H "Authorization: token $TOKEN" -H "Content-Type: application/octet-stream" \
    --data-binary "@$f" \
    "https://uploads.github.com/repos/$REPO/releases/$RID/assets?name=$(basename $f)" \
    | python3 -c "import json,sys; d=json.load(sys.stdin); print('  ✓', d.get('name'), d.get('size',0), 'bytes')" 2>/dev/null \
    || echo "  （可能已存在同名文件，可先删除再传）"
done

echo ""
echo "完成: https://github.com/$REPO/releases/tag/$TAG"
