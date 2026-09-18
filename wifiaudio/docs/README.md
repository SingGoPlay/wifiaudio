# 🎧 WiFiAudio — 无线音频广播 (KernelSU / Magisk 模块)

把手机变成 **WiFi 音频发射器**：实时捕获设备播放的所有音频（媒体 / 游戏 / 通知 / 系统音效 / VoIP 网络通话），通过局域网 WiFi 广播出去，另一台设备（Android 手机/平板 或 PC）即可实时收听。

**✅ 同时支持 KernelSU 与 Magisk**（同一份安装包，安装时自动适配平台）

## ✨ 功能特性

| 特性 | 说明 |
|---|---|
| 🎵 全量音频捕获 | 媒体、游戏、通知、闹钟、系统音效、**VoIP 通话**（微信语音等）全覆盖 |
| 🗜️ **三种编码全可用** | **PCM** 无损直通 / **OPUS** 软解（libopus）/ **AAC** 软解（fdk-aac）—— 软解绕开 MediaCodec 兼容性问题，任何 ROM 都可靠 |
| 🎮🎵 游戏/音乐双模式 | 一键切换：游戏=低延迟（UDP+小缓冲），音乐=高音质稳定（TCP+大缓冲） |
| ⚡ UDP / TCP 可选 | UDP 更低延迟；TCP 可靠稳定 |
| 📡 多端同时接收 | 局域网广播，多个接收端可同时连接 |
| 📱 管理器配置 | 发送端所有配置用 **WiFiAudio 管理器 App** 完成（root 直连，KernelSU/Magisk 通用） |
| 🎛️ 预设系统 | 4 个系统预设 + 自定义预设（支持中文名） |
| 📊 接收端实时统计 | 协议/编码/采样/码率/缓冲/**实时延迟**/解码输出（每秒刷新） |
| 🔍 接收端诊断 | 一键查询服务端状态（编码器输出、连接设备、错误） |
| 🔊 测试音功能 | 1kHz 正弦波验证扬声器链路 |
| 🌙 夜间模式 | 接收端 App 深色/浅色一键切换 |
| 🔋 默认省电 | 默认关闭，零进程零功耗 |

## 📦 交付物

```
build/
├── wifiaudio-ksu.zip          ← KernelSU/Magisk 模块（发送端）
└── WifiAudioReceiver.apk      ← 接收端 App（另一台 Android 设备）
```

## 🔧 安装

### 1. 发送端（需 KernelSU 或 Magisk）

**KernelSU 用户**：
1. KernelSU 管理器 → 模块 → 从本地安装 → `wifiaudio-ksu.zip`
2. 重启手机
3. 安装并打开 **WiFiAudio 管理器** App → 开启「启用广播」→ 状态变「运行中」
4. 记下 IP 与端口（TCP/UDP/HTTP）

**Magisk 用户**（同样使用 WiFiAudio 管理器，或手动编辑配置）：
1. Magisk 管理器 → 模块 → 安装同一份 zip → 重启
2. 安装 WiFiAudio 管理器 App 开启广播；或编辑 `/storage/emulated/0/WiFiAudio/config.json`（`"enabled": 1`）
3. `su -c "sh /data/adb/modules/wifiaudio/bin/wifiaudio.sh start"`

> 配置统一存放在 **`/storage/emulated/0/WiFiAudio/config.json`**（用户可直接查看/编辑）
> 日志：`/storage/emulated/0/WiFiAudio/capture.log`（服务）、`service.log`（启动）

### 2. 接收端（另一台 Android 设备）
1. 安装 `WifiAudioReceiver.apk`（图标与发送端同款）
2. 填写发送端 **IP** 和 **TCP 端口**（默认 47800）
3. 传输方式：**自动**（跟随发送端）；缓冲档位：听音乐「高音质」、打游戏「低延迟」、可**自定义毫秒**
4. **连接预设**：保存/加载多个连接方案；自动记住最近连接
5. 连接 → 状态卡片显示**实时统计**（协议/编码/码率/缓冲/延迟/解码输出）

### 3. PC 播放
- VLC 打开网络串流：
  - **PCM 模式**：`http://<发送端IP>:47801/stream`
  - **AAC 模式**：`http://<发送端IP>:47801/stream.aac`
- 命令行：`ffplay http://<发送端IP>:47801/stream`

## 🎛️ 预设与配置

### 🎛️ 预设
**系统预设**：🎵 音乐高音质（TCP+PCM）/ 🎮 游戏低延迟（UDP+PCM）/ 🗜️ 弱网省流量（OPUS 64kbps）/ 📺 观影同播（本地照常出声）
**自定义预设**：保存当前任意配置组合，随时一键恢复

### ⚙️ 配置项
| 配置 | 默认 | 说明 |
|---|---|---|
| 启用广播 | 关 | 主开关，关闭时服务完全退出（最省电） |
| 模式 | 音乐 | 游戏=UDP+小缓冲 / 音乐=TCP+大缓冲 |
| 传输协议 | TCP | TCP 可靠 / UDP 低延迟 |
| 编码 | PCM | PCM 无损 / **OPUS**（libopus 软解）/ **AAC**（fdk-aac 软解） |
| OPUS/AAC 码率 | 128/192kbps | 可调 |
| 本地声音 | 静音 | 静音=声音只走 WiFi / 照常=本地也出声 |
| 蜂窝通话捕获 | 关 | 尽力而为，依赖设备支持 |
| TCP/UDP/HTTP 端口 | 47800/48800/47801 | UDP=TCP+1000 |

## ⚡ 延迟与音质指南

| 模式 | 配置 | 预期延迟 | 适用 |
|---|---|---|---|
| 🎮 游戏 | PCM + UDP + 低缓冲 | **~50-100ms** | 吃鸡听脚步、音游、直播 |
| 🎵 音乐 | PCM + TCP + 高缓冲 | ~150-250ms | 听歌看片，稳定不卡顿 |
| 🗜️ 弱网 | OPUS 64kbps + TCP | ~150-250ms | 弱 WiFi 也流畅 |

**实时延迟显示**：接收端 App 每秒实测（AudioTrack 缓冲 + UDP 抖动 + 固定开销），调缓冲档位立即可见变化。
**硬件极限**：Android loopback 捕获约 40ms 下限（系统混音周期），无法再低。

## 🔬 工作原理

```
┌─────────────── 发送端 (KSU/Magisk) ───────────────┐
│ app_process 以 root 启动 (SELinux 域自动适配)      │
│  AudioPolicy loopback (Android 13+ 官方API)        │
│   ├─ 匹配所有 usage + VoIP 通话捕获                │
│   └─ ROUTE_FLAG_LOOP_BACK[_RENDER]                │
│        │ 48kHz/16bit/stereo PCM                   │
│  Mixer（可选混入 VOICE_CALL 蜂窝通话）             │
│        ├─► TCP 流服务器 (多客户端, 压缩帧带长度前缀)│
│        ├─► UDP 流服务器 (低延迟, 端口+1000)        │
│        ├─► HTTP 流 (VLC/浏览器)                   │
│        └─► 编码器: MediaCodec OPUS/AAC → 软解接收端│
└───────────────────────────────────────────────────┘
        │ WiFi 局域网
        ▼
┌─────────────── 接收端 ───────────────┐
│ Android App:                        │
│  ├─ PCM: AudioTrack 直通            │
│  ├─ OPUS: libopus 软解 (JNI)        │
│  ├─ AAC: fdk-aac 软解 (JNI)         │
│  └─ 实时统计/诊断/测试音/夜间模式     │
└─────────────────────────────────────┘
```

### 🗜️ 为什么用软解
部分 ROM（尤其定制 ROM）的 **MediaCodec 解码器存在损坏**（配置成功但一提交数据就进入 Released 状态），导致硬解 AAC/OPUS 无声。本项目**将 libopus / fdk-aac 软解库打包进 App**（NDK clang 交叉编译，bionic 兼容，16KB 页对齐），**完全绕开系统解码器，任何 ROM 行为一致**。

## 🐛 故障排查

| 现象 | 排查 |
|---|---|
| 启用后未运行 | 确认 `/storage/emulated/0/WiFiAudio/config.json` 的 `enabled=1`；查看 `capture.log` |
| 配置刷新后变回默认 | 管理器「保存并应用」后看提示是否 `OK config written`；config.json 是配置源 |
| 接收端连不上 | 确认 IP/端口；`ping`；关 AP 隔离 |
| 无声音 | 先点「🔊 测试音」验证扬声器；看统计「解码输出」是否增长；「🔍 诊断」查服务端编码器输出 |
| OPUS/AAC 无声音 | 检查统计「解码输出」；App 日志（`Android/data/com.wifiaudio.receiver/files/wifiaudio.log`）；JNI 日志 `adb logcat -s AacSoft -d` / `-s OpusSoft` |
| 声音断续 | 加大缓冲（音乐模式/自定义毫秒）；检查 WiFi |
| PC（VLC）打不开 `/stream` | v4.18 起 HTTP 流已带标准响应头（`Content-Type: audio/wav` + `Connection: close`）；若为老版本请升级模块。编码须为 **PCM** 才能用 `/stream`，AAC 用 `/stream.aac`（不匹配会返回 409 提示） |
| 蜂窝通话无声音 | 通话捕获需在配置中开启且设备 HAL 支持（尽力而为） |

## 🛠 从源码构建

```bash
# 1. 编译捕获服务（Java → dex）
bash server/build.sh                # → module/bin/wifiaudio.jar

# 2. 编译 JNI 软解库（NDK clang + lld，bionic 兼容）
#    见 server/jni/（libopus / fdk-aac 已编译好，可直接用）

# 3. 打包模块
bash build_module.sh                # → build/wifiaudio-ksu.zip

# 4. 构建接收端 APK（含软解库，ARM64 需 qemu 跑 x86-64 工具）
bash receiver/build_apk.sh          # → build/WifiAudioReceiver.apk
```

## 📜 致谢

- 音频捕获与系统上下文方案参考 [Genymobile/scrcpy](https://github.com/Genymobile/scrcpy)（Apache-2.0）
- OPUS 基于 [Xiph.Org libopus](https://opus-codec.org/)（BSD-3-Clause）
- AAC 基于 [Fraunhofer FDK AAC](https://www.iis.fraunhofer.de/en/ff/amm/impl/fdkaac.html)（见 fdk-aac 许可证）
- Android NDK 交叉编译链路（clang + lld）

## 📋 版本历史

| 版本 | 里程碑 |
|---|---|
| v4.18 | **HTTP 流响应头修复**（`/stream` 直接发裸 WAV → VLC 识别不了）；**Mixer 线程解耦**（编码器/UDP 不再阻塞音频捕获）；编码不匹配改报 409；队列溢出计数 `mixerDrops`；三端版本号 4.18/418 |
| v2.19 | AAC 双模式兼容（长度前缀/ADTS 自动识别） |
| v2.18 | AAC TCP 流加长度前缀（解决 ADTS 假同步） |
| v2.17 | fdk-aac 软解接入成功（NDK clang 编译） |
| v2.14 | AAC 解码库修复缺失符号（lppTransposer） |
| v2.7 | OPUS libopus 软解成功 |
| v2.0 | 配置迁移 /storage/emulated/0/WiFiAudio/ |
| v1.0 | 首个可用版本（PCM + MediaCodec 编码） |
