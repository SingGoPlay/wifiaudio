# WiFiAudio 项目说明书（给新 AI / 新对话的快速上手）

> 阅读本文件 + 关键源码后即可继续开发。**不要随意重构、不要改协议、构建前确认环境。**

---

## 1. 这是什么

**WiFiAudio —— 无线音频广播系统**：把 Android 手机变成"系统级音频发射器"。

- **发送端**（KernelSU/Magisk 模块，需 root）：捕获手机播放的**全部音频**（媒体/游戏/通知/闹钟/系统音效/**VoIP 通话**如微信语音），经 WiFi 局域网广播
- **接收端**（Android App）：另一台设备实时收听；**PC 用 VLC** 打开 HTTP 流也能听
- 当前版本：**v4.15**（接收端/管理器/发声器三端版本号统一，versionCode 415）

**技术亮点**：全量捕获（含 VoIP）、三编码（PCM/OPUS/AAC 软解兜底）、高采样率 PCM（48k/96k/192k 跟随设备）、UDP/TCP 可选（发射端决定、接收端自动跟随）、多端同时接收、低延迟游戏模式（~70-100ms）、MIUI 风格 UI（Kotlin + Compose + miuix）、自适应缓冲（欠载驱动）、AAudio exclusive 低延迟输出、GitHub 自动更新检测。

---

## 2. 工作区目录结构（/workspace）

| 路径 | 内容 | 构建方式 |
|---|---|---|
| `wifiaudio/` | **发送端模块** + 旧版命令行构建（Java） | 命令行 |
| ├─ `module/` | KernelSU/Magisk 模块本体（service.sh、wifiaudio.sh、WebUI、预设） | — |
| ├─ `server/` | 捕获服务 Java 源码（`com.wifiaudio.*`）+ JNI 软解库 .so | `server/build.sh` |
| ├─ `receiver/` | **旧版**接收端（纯 Java View，已被 apps-android 取代，保留备用） | `receiver/build_apk.sh` |
| └─ `docs/README.md` | 完整功能文档 | — |
| `apps-android/` | **新版 App（Kotlin + Compose + miuix）**，Gradle 多模块 | Gradle |
| ├─ `receiver/` | 新版接收端（Java 播放引擎 + Kotlin UI） | `gradle :receiver:assembleRelease` |
| └─ `manager/` | 发射端管理器（root 直连，RootShell.java + Kotlin UI） | `gradle :manager:assembleRelease` |
| `tone/` | 发声器 App（测试音，命令行构建） | `tone/build_apk.sh` |
| `audiotest/` | 解码链路测试 App | 命令行 |
| `release/` | **发行版产物**：wifiaudio-ksu.zip + 3 个 APK + 使用说明 | — |
| `wifiaudio-github/` | **GitHub 仓库副本**（git 已初始化，remote 指向 SingGoPlay/wifiaudio） | git |
| `toolchain/` | Gradle 8.13、NDK r27c、musl、fdk-aac、opus 源码、**aapt2 wrapper** | — |
| `android-sdk/` | SDK（build-tools 35、platforms android-35/36/37.0） | — |
| `build_all.sh` | 命令行一键构建（模块 + 旧版接收端 + 管理器） | — |
| `PROJECT.md` | 本文件 | — |

---

## 3. 架构

```
发送端 (root, app_process 守护进程 wifiaudio-capture)
  AudioLoopbackCapture (AudioPolicy loopback 反射捕获, 48k/96k/192k PCM16)
    → Mixer 分发（同一份 PCM 广播给所有 Sink）
      ├─ StreamServer TCP 47800（裸 PCM 流，每客户端队列 + 发送线程）
      ├─ UdpServer   UDP 48800（4 字节 seq + PCM 包，心跳保活）
      ├─ HttpServer  HTTP 47801（WAV 头 + PCM 流，VLC/浏览器用）
      └─ OpusEncoder / AacEncoder（MediaCodec 编码，可选项）

接收端 App
  TCP: socket 读裸流 → PcmJitterBuffer（预缓冲 + 欠载驱动自适应 + 漂移伺服）
  UDP: 收包按 seq 排序 → 毫秒级抖动队列（下限 20ms）
  → PCM: AudioTrack 直通 / AAudio exclusive（采样率校验匹配才用）
  → OPUS/AAC: 硬解优先，失败自动切 libopus/fdk-aac 软解（JNI）
```

**协议要点**：16 字节格式头（`WFAU` + codec + sampleRate + channels + mode + transport）；TCP 裸字节流无帧界；UDP 每包 4 字节大端 seq；OPUS/AAC 帧带 2 字节长度前缀。**传输协议（TCP/UDP）由发射端决定**（config transport），StreamServer 的 header[13] 反映实际配置，接收端固定 `auto` 跟随。

---

## 4. 构建方法

### 命令行链路（发送端模块 + 旧版）
```bash
bash /workspace/build_all.sh          # 全量（模块 zip + 旧接收端 + 旧管理器）
cd /workspace/wifiaudio/server && bash build.sh      # 仅 server jar
cd /workspace/wifiaudio && bash build_module.sh      # 仅模块 zip
```
产物 → `wifiaudio/build/`、`wifiaudio-manager/build/`、`tone/build/`；**构建完手动 cp 到 `/workspace/release/`**。

### Gradle 链路（新版 Kotlin App）
```bash
cd /workspace/apps-android
/workspace/toolchain/gradle-8.13/bin/gradle :receiver:assembleRelease :manager:assembleRelease -x lintVitalRelease
# 产物: apps-android/{receiver,manager}/build/outputs/apk/release/*-release.apk → cp 到 release/
```

### ⚠️ 构建环境硬性要求（沙箱特有，勿改）
- **aapt2 是 x86_64 二进制，沙箱是 ARM64**：必须用 ELF wrapper `/workspace/toolchain/aapt2`（C 编译，内部 exec qemu + 真 aapt2）。`gradle.properties` 已配置 `android.aapt2FromMavenOverride=/workspace/toolchain/aapt2`
- **miuix 要求 compileSdk 37**：沙箱伪造了 `/workspace/android-sdk/platforms/android-37.0`（基于官方 android-36 复制改 ApiLevel），`gradle.properties` 有 `android.suppressUnsupportedCompileSdk=37.0`
- 依赖版本锁定：**Gradle 8.13 + AGP 8.13.2 + Kotlin 2.3.20 + Compose Multiplatform 1.10.3 + miuix 0.9.0 + material3（仅 Dropdown 已移除，可去掉）**
- 构建时用 `nohup ... &` 后台跑 + 轮询日志（沙箱前台长命令会被终止）；**/tmp 会被清理**，一切工具放 /workspace

### JNI 软解库重新编译（改 aaudio_jni.c 等后）
```bash
NDK=/workspace/toolchain/android-ndk-r27c
BIN=$NDK/toolchains/llvm/prebuilt/linux-x86_64/bin
# arm64: clang --target=aarch64-linux-android26 -c + ld.lld -m aarch64linux -shared
# v7:   clang --target=armv7a-linux-androideabi26 -c + ld.lld -m armelf_linux_eabi -shared
# 产物 cp 到 apps-android/receiver/src/main/jniLibs/{arm64-v8a,armeabi-v7a}/ 和 wifiaudio/server/jni/
# 源码在 wifiaudio/server/jni/aaudio_jni.c
```

---

## 5. 发布（GitHub）

- 仓库：**https://github.com/SingGoPlay/wifiaudio**（公开）
- 本地副本：`/workspace/wifiaudio-github/`（git remote 已配好，含 token）
- 更新检测：App 启动时查 `releases/latest` 的 tag_name 对比自身 versionName（**三端版本号必须统一**，当前 4.15/415）
- 发布新版本：
```bash
cd /workspace/wifiaudio-github
# 1. 同步代码: tar 复制 /workspace 相关目录到副本（排除 build/.gradle/*.keystore）
# 2. git add -A && git commit && git push
# 3. GH_TOKEN=xxx ./upload_release.sh v4.16 "说明"   （打 tag + 建 Release + 上传 release/ 4 个产物）
```

---

## 6. 关键技术实现（改动前必读）

### 发送端
- **捕获**：`AudioLoopbackCapture.java` 反射 `AudioPolicy`/`AudioMix`/`AudioMixingRule`（scrcpy 同款，root 全 usage + VoIP），`MAX_READ_FRAMES=512`（48k 时 ~10.7ms/块）
- **采样率**：`pcm_rate` 配置 auto/48000/96000/192000，auto=检测设备输出能力（48k 系列）；编码模式强制 48k
- **UDP 常开**：三个服务器始终启动（TCP/UDP/HTTP），UDP 客户端计数已同步 `Status.clients`
- **StreamServer header[13]** = `useUdp ? 1 : 0`（反映发射端实际传输配置，接收端 auto 跟随）
- **Mixer**：捕获线程同步分发（`mixLoopback` 直接调各 Sink `onPcm`）——已知可优化点：UDP send/HTTP write 可能阻塞捕获线程（见第 8 节）

### 接收端（apps-android/receiver）
- **PcmJitterBuffer**（PlayerService 内部类）：网络线程入队 + 播放线程按 AudioTrack/AAudio 阻塞节流；预缓冲；**欠载驱动自适应**（3 秒内有欠载才加深，TCP 攒包不误判）；漂移伺服（水位持续偏高丢 5ms）；`currentMs = 队列 + 输出缓冲`
- **AAudio**（JNI）：exclusive + burst×4 缓冲 + write 循环写完 + **采样率校验**（实际≠请求立即回退 AudioTrack）
- **已知修过的坑**（别再犯）：`AudioOut.writePcmBytes` 传**帧数**必须是 `采样数/声道数`（stereo 传错会电音）；STATS/STATE 事件走 **StateListener 内存回调**（广播收不到，UI 用回调）
- **协议**：`EXTRA_TRANSPORT` 固定 `"auto"`（跟随发射端），传输控件已删除
- **URL 解析**：IP 输入支持 `host` / `host:port` / `http://host:port/path`（端口转发/公网穿透场景）

### 管理器（apps-android/manager）
- **RootShell.java**：`su` + base64 交互（writeConfig 用 `wifiaudio.sh set-all`）
- **config 修改必须用 `setCfg()`**（JSONObject 内部修改不触发 Compose 重组，需重新赋值）
- 开关「启用广播」= 写配置 + `wifiaudio.sh start/stop` 联动
- 状态轮询 3 秒（`refreshStatus()`）；`status.json` 含 sampleRate/bitrate/httpPort/clients 等

### 模块脚本
- **手动模式**：`service.sh` 开机不启动任何进程、不读配置（纯手动唤起）
- `wifiaudio.sh`：start/stop/restart/status/get/set/set-all/read-config；默认配置含 `enabled=0`（零进程）

---

## 7. 配置文件

- 发送端配置：`/storage/emulated/0/WiFiAudio/config.json`（enabled/mode/transport/codec/pcm_rate/端口等）
- 状态：同目录 `status.json`、日志 `capture.log`、PID `capture.pid`
- 端口：TCP 47800 / UDP 48800（=TCP+1000）/ HTTP 47801

---

## 8. 已知待办 / 可优化方向（按性价比）

1. **Mixer 线程解耦**（最优先）：捕获线程只入队，独立分发线程——防止 UDP send/HTTP write 阻塞 `AudioRecord.read()` 导致爆音
2. **丢帧 Crossfade**：PcmJitterBuffer 丢帧衔接处 2-3ms 交叉淡化，消除咔哒
3. **对象池**：减少 `onPcm` 频繁 `new byte[]`
4. TCP 音乐模式攒批 write（低成本，减少 write 次数）
5. 已评估**不做**的：SOX 变速重采样（漂移极小）、PCM 双发 FEC（带宽翻倍不值）、接收端采样率反馈（现代设备 48k 直通为主）

---

## 9. 常见操作（真机）

```bash
# 查看发送端进程/状态
ps -A | grep wifiaudio
cat /storage/emulated/0/WiFiAudio/status.json
# 手动启停
su -c "sh /data/adb/modules/wifiaudio/bin/wifiaudio.sh start|stop|restart|status"
# VLC 播放
http://<发送端IP>:47801/stream
```

---

## 10. 给新 AI 的提醒

1. **先读本文件 + `wifiaudio/docs/README.md` + 目标模块源码**再动手
2. **构建环境是沙箱特制的**：aapt2 wrapper、伪造 android-37.0、Gradle 路径、后台构建模式——不要"优化"这些配置
3. **协议/版本号/三端一致性**是踩过坑的：传输跟随发射端、三端统一版本、事件走内存回调
4. 用户会用真机验证；改完构建后把 APK/zip 放进 `/workspace/release/` 并告知用户
5. 有 95% 把握再动工，没把握先问
