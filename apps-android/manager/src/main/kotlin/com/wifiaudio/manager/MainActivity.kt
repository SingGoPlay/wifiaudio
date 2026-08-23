package com.wifiaudio.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController

/**
 * WiFiAudio 管理器 v4.0 —— Kotlin + Compose + miuix 重构
 * root 层（RootShell）保持 Java 原样。
 */
class MainActivity : ComponentActivity() {

    private var uiScope = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(this)
        }
    }
}

@Composable
fun App(activity: ComponentActivity) {
    val controller = remember { ThemeController() }
    MiuixTheme(controller = controller) {
        MainScreen(activity)
    }
}

@Composable
private fun MainScreen(activity: ComponentActivity) {
    val scope = rememberCoroutineScope()
    // 配置
    var config by remember { mutableStateOf(loadConfig()) }
    // 状态
    var running by remember { mutableStateOf(false) }
    var stIp by remember { mutableStateOf("—") }
    var stCodec by remember { mutableStateOf("—") }
    var stRate by remember { mutableStateOf("—") }
    var stBitrate by remember { mutableStateOf("—") }
    var stMode by remember { mutableStateOf("—") }
    var stPort by remember { mutableStateOf("—") }
    var stHttpUrl by remember { mutableStateOf("—") }
    var stClients by remember { mutableStateOf(0) }
    var stFrames by remember { mutableStateOf(0L) }
    var stErr by remember { mutableStateOf("") }
    var logText by remember { mutableStateOf("") }
    var toast by remember { mutableStateOf("") }

    // 修改配置并触发 Compose 重组（JSONObject 内部修改不会自动触发，需重新赋值）
    fun setCfg(key: String, value: Any) {
        try {
            config = JSONObject(config.toString()).apply { put(key, value) }
        } catch (_: Throwable) {
            // 序列化异常保护，避免闪退
        }
    }

    // 读取状态并刷新仪表盘（开关/重启后立即调用，轮询每 3s 也调用）
    fun refreshStatus() {
        val st = runCatching { JSONObject(RootShell.readStatus()) }.getOrNull() ?: return
        running = st.optBoolean("running")
        stIp = st.optString("ip", "—")
        stCodec = st.optString("codec", "—").uppercase()
        val r = st.optInt("sampleRate", 0)
        stRate = if (r > 0) (r / 1000).toString() + " kHz" else "—"
        val b = st.optLong("bitrate", 0)
        stBitrate = if (b >= 1000000) String.format("%.1f Mbps", b / 1000000.0) else if (b > 0) (b / 1000).toString() + " kbps" else "—"
        stMode = st.optString("mode", "—") + " / " + st.optString("transport", "—").uppercase()
        stPort = "T" + st.optInt("tcpPort", 47800) + " U" + st.optInt("udpPort", 48800) + " H" + st.optInt("httpPort", 47801)
        stClients = st.optInt("clients", 0)
        stFrames = st.optLong("encoderFrames", 0)
        stErr = st.optString("lastError", "")
        stHttpUrl = "http://" + st.optString("ip", "—") + ":" + st.optInt("httpPort", 47801) + "/stream"
    }

    // 状态轮询
    LaunchedEffect(Unit) {
        while (true) {
            refreshStatus()
            logText = RootShell.readFileBase64("/storage/emulated/0/WiFiAudio/capture.log")
                ?.split("\n")?.takeLast(25)?.joinToString("\n") ?: "（无日志，服务未运行）"
            delay(3000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) Color(0xFF242424) else Color(0xFFF7F7F7))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = "🎛️ WiFiAudio 管理器", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(text = "root 直连配置（Kotlin + miuix）", fontSize = 12.sp, color = Color(0xFF8C93B0), modifier = Modifier.padding(bottom = 12.dp))

        // ===== 状态卡 =====
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (running) "● 运行中" else "○ 未运行",
                        color = if (running) Color(0xFF2FA85C) else Color(0xFF8C93B0),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.weight(1f))
                    // 重启服务按钮（Card 自绘，避免 miuix Button 默认内边距截断文字）
                    Card(
                        onClick = { scope.launch { RootShell.service("restart"); toast = "已重启服务" } },
                        colors = CardDefaults.defaultColors(color = Color(0xFF3482FF)),
                        cornerRadius = 14.dp
                    ) {
                        Text(
                            text = "↻ 重启服务",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                // 仪表盘网格 2×4
                Row {
                    ManagerStat("IP", stIp, Modifier.weight(1f).padding(end = 4.dp))
                    ManagerStat("编码", stCodec, Modifier.weight(1f).padding(start = 4.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row {
                    ManagerStat("采样率", stRate, Modifier.weight(1f).padding(end = 4.dp))
                    ManagerStat("比特率", stBitrate, Modifier.weight(1f).padding(start = 4.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row {
                    ManagerStat("模式/传输", stMode, Modifier.weight(1f).padding(end = 4.dp))
                    ManagerStat("端口", stPort, Modifier.weight(1f).padding(start = 4.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row {
                    ManagerStat("连接数", stClients.toString(), Modifier.weight(1f).padding(end = 4.dp))
                    ManagerStat("编码输出", stFrames.toString(), Modifier.weight(1f).padding(start = 4.dp))
                }
                if (stHttpUrl != "—" && stHttpUrl.isNotBlank()) {
                    Text(
                        text = "HTTP 串流: $stHttpUrl",
                        fontSize = 11.sp,
                        color = Color(0xFF3482FF),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (stErr.isNotEmpty()) {
                    Text(text = "⚠ $stErr", fontSize = 11.sp, color = Color(0xFFF12522), modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        // ===== 主开关 =====
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(text = "⚡ 启用广播", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "默认关闭（最省电）", fontSize = 11.sp, color = Color(0xFF8C93B0))
                }
                Switch(
                    checked = config.optString("enabled") == "1",
                    onCheckedChange = {
                        setCfg("enabled", if (it) 1 else 0)
                        saveConfig(activity, config)
                        // 开关联动服务启停（改配置后立即生效，无需手动重启）
                        scope.launch { RootShell.service(if (it) "start" else "stop") }
                        toast = if (it) "已开启（服务启动中…）" else "已关闭（服务已停止）"
                        refreshStatus()
                    }
                )
            }
            // 开机自启开关
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(text = "开机自启", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "关闭后仅由本管理器唤起（开机不自动启动）", fontSize = 11.sp, color = Color(0xFF8C93B0))
                }
                Switch(
                    checked = config.optString("auto_start") != "0",
                    onCheckedChange = {
                        setCfg("auto_start", if (it) 1 else 0)
                        saveConfig(activity, config)
                        toast = if (it) "已开启开机自启" else "已关闭开机自启（下次开机需手动启动）"
                    }
                )
            }
        }

        // ===== 音频设置 =====
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(text = "🔊 音频设置", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                MiuixDropdown(
                    label = "编码方式",
                    options = listOf("PCM 原始", "OPUS 压缩", "AAC 压缩"),
                    selected = when (config.optString("codec", "pcm")) { "opus" -> 1; "aac" -> 2; else -> 0 },
                    onSelect = { setCfg("codec", arrayOf("pcm", "opus", "aac")[it]) },
                    modifier = Modifier.fillMaxWidth()
                )
                MiuixDropdown(
                    label = "PCM 采样率（仅 PCM 模式）",
                    options = listOf("自动（跟随设备）", "48 kHz", "96 kHz", "192 kHz"),
                    selected = when (config.optString("pcm_rate", "auto")) { "48000" -> 1; "96000" -> 2; "192000" -> 3; else -> 0 },
                    onSelect = { setCfg("pcm_rate", arrayOf("auto", "48000", "96000", "192000")[it]) },
                    modifier = Modifier.fillMaxWidth()
                )
                Row {
                    ConfigField("OPUS 码率 kbps", config.optString("opus_bitrate", "128"), Modifier.weight(1f)) { setCfg("opus_bitrate", it.toIntOrNull() ?: 128) }
                    Spacer(Modifier.width(8.dp))
                    ConfigField("AAC 码率 kbps", config.optString("aac_bitrate", "192"), Modifier.weight(1f)) { setCfg("aac_bitrate", it.toIntOrNull() ?: 192) }
                }
                MiuixDropdown(
                    label = "本地声音",
                    options = listOf("本地静音（声音只走 WiFi）", "本地照常出声"),
                    selected = if (config.optString("local_render") == "1") 1 else 0,
                    onSelect = { setCfg("local_render", it) },
                    modifier = Modifier.fillMaxWidth()
                )
                MiuixDropdown(
                    label = "蜂窝通话捕获",
                    options = listOf("关闭", "开启（尽力而为）"),
                    selected = if (config.optString("capture_call") == "1") 1 else 0,
                    onSelect = { setCfg("capture_call", it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ===== 模式与网络 =====
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(text = "🌐 模式与网络", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                MiuixDropdown(
                    label = "传输协议",
                    options = listOf("TCP（稳定）", "UDP（低延迟）"),
                    selected = if (config.optString("transport") == "udp") 1 else 0,
                    onSelect = { setCfg("transport", if (it == 1) "udp" else "tcp") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row {
                    ConfigField("TCP 端口", config.optString("tcp_port", "47800"), Modifier.weight(1f)) { setCfg("tcp_port", it.toIntOrNull() ?: 47800) }
                    Spacer(Modifier.width(8.dp))
                    ConfigField("HTTP 端口", config.optString("http_port", "47801"), Modifier.weight(1f)) { setCfg("http_port", it.toIntOrNull() ?: 47801) }
                }

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        saveConfig(activity, config)
                        toast = "配置已保存"
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    cornerRadius = 16.dp
                ) {
                    Text("保存配置", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ===== 日志 =====
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(text = "📄 服务日志", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = logText, fontSize = 10.sp, color = Color(0xFF8C93B0), modifier = Modifier.padding(top = 8.dp))
            }
        }

        if (toast.isNotEmpty()) {
            Text(text = toast, fontSize = 12.sp, color = Color(0xFF3482FF), modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}

@Composable
private fun StatusLine(k: String, v: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(text = k, fontSize = 12.sp, color = Color(0xFF8C93B0), modifier = Modifier.width(90.dp))
        Text(text = v, fontSize = 12.sp)
    }
}

@Composable
private fun ManagerStat(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3482FF), maxLines = 1)
            Text(text = label, fontSize = 11.sp, color = Color(0xFF8C93B0))
        }
    }
}

@Composable
private fun ConfigField(label: String, value: String, modifier: Modifier = Modifier, onChange: (String) -> Unit) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF8C93B0))
        TextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )
    }
}

@Composable
private fun MiuixDropdown(label: String, options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF8C93B0))
        Card(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth().padding(top = 2.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = options.getOrElse(selected) { "" }, fontSize = 14.sp)
                Text(text = "▾", fontSize = 12.sp)
            }
        }
        if (expanded) {
            // 内联展开选项（不用 material3 DropdownMenu，避免主题兼容问题）
            Card(modifier = Modifier.fillMaxWidth().padding(top = 2.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    options.forEachIndexed { i, o ->
                        Card(
                            onClick = { expanded = false; onSelect(i) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = o,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 11.dp),
                                fontSize = 14.sp,
                                color = if (i == selected) Color(0xFF3482FF) else Color.Unspecified
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun loadConfig(): JSONObject {
    return try {
        JSONObject(RootShell.readConfig() ?: "{}")
    } catch (_: Throwable) {
        JSONObject()
    }
}

private fun saveConfig(activity: ComponentActivity, config: JSONObject) {
    val ok = RootShell.writeConfig(config.toString())
    if (!ok) {
        // 失败提示
    }
}
