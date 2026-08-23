# 🎧 WiFiAudio — 无线音频广播（KernelSU / Magisk 模块）

把手机变成 **WiFi 音频发射器**：实时捕获设备播放的所有音频（媒体 / 游戏 / 通知 / 系统音效 / VoIP 通话），通过局域网 WiFi 广播出去，另一台设备（Android 手机/平板 或 PC）即可实时收听。

**✅ 同时支持 KernelSU 与 Magisk**（同一份安装包，安装时自动适配平台）

## ✨ 功能特性

| 特性 | 说明 |
|---|---|
| 🎵 全量音频捕获 | 媒体、游戏、通知、闹钟、系统音效、**VoIP 通话**（微信语音等）全覆盖 |
| 🗜️ **三种编码全可用** | **PCM** 无损直通 / **OPUS** 软解（libopus）/ **AAC** 软解（fdk-aac）——软解绕开 MediaCodec 兼容性问题 |
| 🎮🎵 模式预设 | 游戏=低延迟（UDP+小缓冲），音乐=高音质稳定（TCP+大缓冲） |
| ⚡ UDP / TCP 可选 | UDP 更低延迟；TCP 可靠稳定（发送端决定，接收端自动跟随） |
| 🔬 **高采样率 PCM** | 支持 48k / 96k / 192kHz（自动跟随设备输出能力，Hi-Res 直通） |
| 📡 多端同时接收 | 局域网广播，多个接收端可同时连接 |
| 🌐 WebUI 配置 | KernelSU 原生 WebUI / Magisk 配 MMRL |
| 🎛️ 预设系统 | 4 个系统预设 + 自定义预设（支持中文名） |
| 📊 实时统计 | 协议/编码/采样/码率/缓冲/**实时延迟**/跳帧/下溢（接收端每秒刷新） |
| 🔊 测试音 | 1kHz 正弦波验证扬声器链路 |
| 🌙 夜间模式 | 深色/浅色一键切换 |
| 🔋 默认省电 | 默认关闭，零进程零功耗 |

## 📦 交付物（release/）

```
release/
├── wifiaudio-ksu.zip          ← KernelSU/Magisk 模块（发送端）
├── WifiAudioReceiver.apk      ← 接收端 App（另一台 Android 设备）
├── WifiAudioManager.apk       ← 发射端管理 App（root 直连）
└── WifiAudioTone.apk          ← 发声器（测试音）
```

## 🚀 快速上手

1. 发送端手机刷 `wifiaudio-ksu.zip` → 重启
2. 管理 App 或 WebUI 开启「启用广播」
3. 接收端装 `WifiAudioReceiver.apk` → 自动发现或填 IP → 连接 → 出声
4. PC 播放：VLC 打开 `http://<发送端IP>:47801/stream`

## 🔬 工作原理

```
┌─────────────── 发送端 (KSU/Magisk 模块, root) ───────────────┐
│ app_process 以 root 启动 (SELinux 域自动适配)                 │
│  AudioPolicy loopback (Android 13+ 官方API, root 反射)        │
│   ├─ 匹配所有 usage + VoIP 通话捕获                           │
│   └─ ROUTE_FLAG_LOOP_BACK[_RENDER] → 48k/96k/192k PCM         │
│  Mixer（捕获线程入队 + 分发）                                  │
│        ├─► TCP 流服务器 (47800, 裸 PCM)                       │
│        ├─► UDP 流服务器 (48800, seq + PCM)                    │
│        ├─► HTTP 流 (47801, WAV 头, VLC/浏览器)                │
│        └─► 编码器: MediaCodec OPUS/AAC → 软解接收端            │
└───────────────────────────────────────────────────────────────┘
        │ WiFi 局域网
        ▼
┌─────────────── 接收端 ───────────────────────────┐
│ Kotlin + Compose + miuix (MIUI 风格)             │
│  TCP: PcmJitterBuffer 自适应缓冲（欠载驱动）      │
│  UDP: seq 排序 + 毫秒级抖动队列                    │
│  PCM: AudioTrack 直通 / AAudio exclusive          │
│  OPUS/AAC: 硬解优先 → libopus/fdk-aac 软解兜底    │
└──────────────────────────────────────────────────┘
```

## 🗂️ 目录结构

```
├── wifiaudio/           发送端模块 + 旧版命令行构建（Java）
│   ├── module/          KernelSU/Magisk 模块本体
│   ├── server/          捕获服务 Java 源码（app_process）
│   └── receiver/        旧版接收端（纯 Java View）
├── apps-android/        新版 App（Kotlin + Compose + miuix，Gradle 构建）
│   ├── receiver/        接收端
│   └── manager/         发射端管理器
├── tone/                发声器 App
├── audiotest/           解码链路测试 App
├── release/             发行版安装包
└── build_all.sh         一键构建（命令行链路）
```

## 🛠️ 从源码构建

**命令行链路**（发送端模块 + 旧版）：
```bash
bash build_all.sh    # → wifiaudio/build/wifiaudio-ksu.zip 等
```

**Gradle 链路**（新版 Kotlin App，在 apps-android/ 下）：
```bash
cd apps-android
gradle :receiver:assembleRelease :manager:assembleRelease
```
> 构建依赖：Android SDK（compileSdk 36+）、JDK 17、Gradle 8.13、AGP 8.13.2、Kotlin 2.3.20、Compose Multiplatform 1.10.3、[miuix 0.9.x](https://github.com/compose-miuix-ui/miuix)
> JNI 软解库（libopus / fdk-aac / AAudio）已编译好放于各模块 `jniLibs/`，可用 NDK clang 重新编译

## 📜 致谢

- 音频捕获方案参考 [Genymobile/scrcpy](https://github.com/Genymobile/scrcpy)（Apache-2.0）
- UI 基于 [compose-miuix-ui/miuix](https://github.com/compose-miuix-ui/miuix)（MIUI 风格 Compose 库）
- OPUS 基于 [Xiph.Org libopus](https://opus-codec.org/)（BSD-3-Clause）
- AAC 基于 Fraunhofer FDK AAC

## 📋 版本历史

| 版本 | 里程碑 |
|---|---|
| v4.x | Kotlin + Compose + miuix 重构；自适应缓冲（欠载驱动）；AAudio exclusive；高采样率 PCM；URL/端口转发支持 |
| v3.x | AAC 双模式、fdk-aac 软解、libopus 软解、配置迁移 |
| v2.x | OPUS 软解成功、配置迁移 |
| v1.0 | 首个可用版本（PCM + MediaCodec 编码） |
