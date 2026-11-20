package dev.niza.autodroid.ui

object RuleEditorLogic {
    private const val FILTER_ALL = 0
    const val TYPE_LINK = 0
    const val TYPE_APP = 1
    const val TYPE_DATA_SMS = 2
    const val TYPE_SCHEDULE = 3
    const val TYPE_SCREEN_ON_OFF = 4
    const val TYPE_BATTERY_LEVEL = 5
    const val TYPE_AIRPLANE_MODE = 6
    const val TYPE_BLUETOOTH = 7
    const val TYPE_EXTERNAL_POWER = 8
    const val TYPE_HEADPHONES = 9
    const val TYPE_HOTSPOT = 10
    const val TYPE_SILENT_MODE = 11
    const val TYPE_WIFI_CONNECTION = 12
    const val TYPE_WIFI_SSID = 13
    const val TYPE_SIGNAL_ON_OFF = 14
    const val TYPE_DOCK = 15
    const val TYPE_VPN = 16
    const val TYPE_GPS_ENABLED = 17
    const val TYPE_DEVICE_UNLOCKED = 18
    const val TYPE_CLIPBOARD_CHANGE = 19
    const val TYPE_DARK_THEME = 20
    const val TYPE_MEDIA_BUTTON = 21
    const val TYPE_NOTIFICATION = 22

    const val ACTION_NOTIFY = 0
    const val ACTION_OPEN_URL = 1
    const val ACTION_SEND_DATA_SMS = 2

    fun triggerHint(typePosition: Int): String =
        when (typePosition) {
            TYPE_APP -> "应用包名"
            TYPE_DATA_SMS -> "数据短信目标端口"
            TYPE_SCHEDULE -> "间隔毫秒"
            TYPE_SCREEN_ON_OFF -> "屏幕开启 true / 关闭 false"
            TYPE_BATTERY_LEVEL -> "电量百分比"
            TYPE_AIRPLANE_MODE -> "飞行模式 true / false"
            TYPE_BLUETOOTH -> "蓝牙开启 true / false"
            TYPE_EXTERNAL_POWER -> "充电连接 true / false"
            TYPE_HEADPHONES -> "耳机连接 true / false"
            TYPE_HOTSPOT -> "热点开启 true / false"
            TYPE_SILENT_MODE -> "静音开启 true / false"
            TYPE_WIFI_CONNECTION -> "WiFi连接 true / false"
            TYPE_WIFI_SSID -> "WiFi 名称"
            TYPE_SIGNAL_ON_OFF -> "信号开启 true / false"
            TYPE_DOCK -> "底座状态"
            TYPE_VPN -> "VPN连接 true / false"
            TYPE_GPS_ENABLED -> "GPS开启 true / false"
            TYPE_DEVICE_UNLOCKED -> "留空即可，解锁时触发"
            TYPE_CLIPBOARD_CHANGE -> "留空即可，剪贴板变化时触发"
            TYPE_DARK_THEME -> "深色模式 true / false"
            TYPE_MEDIA_BUTTON -> "媒体按键名"
            TYPE_NOTIFICATION -> "应用包名，可留空"
            else -> "链接主机，例如 example.com"
        }

    fun actionHint(typePosition: Int): String =
        when (typePosition) {
            ACTION_OPEN_URL -> "网址"
            ACTION_SEND_DATA_SMS -> "手机号码"
            else -> "通知标题"
        }

    fun actionExtraHint(typePosition: Int): String =
        when (typePosition) {
            ACTION_OPEN_URL -> "可留空"
            ACTION_SEND_DATA_SMS -> "数据内容 HEX"
            else -> "通知内容"
        }

    fun validateTrigger(
        typePosition: Int,
        value: String,
    ): String? =
        when (typePosition) {
            TYPE_APP ->
                if (PACKAGE_NAME_PATTERN.matches(value)) {
                    null
                } else {
                    "请输入有效 Android 包名"
                }
            TYPE_LINK -> if (value.isBlank()) "请输入链接主机" else null
            TYPE_DATA_SMS -> {
                val port = value.toIntOrNull()
                if (port == null || port !in 1..65535) "数据短信端口必须是 1-65535" else null
            }
            TYPE_SCHEDULE -> {
                val interval = value.toLongOrNull()
                if (interval == null || interval < 1000) "间隔至少 1000 毫秒" else null
            }
            TYPE_BATTERY_LEVEL -> {
                val level = value.toIntOrNull()
                if (level == null || level !in 0..100) "电量必须是 0-100" else null
            }
            TYPE_SCREEN_ON_OFF,
            TYPE_AIRPLANE_MODE,
            TYPE_BLUETOOTH,
            TYPE_EXTERNAL_POWER,
            TYPE_HEADPHONES,
            TYPE_HOTSPOT,
            TYPE_SILENT_MODE,
            TYPE_WIFI_CONNECTION,
            TYPE_SIGNAL_ON_OFF,
            TYPE_VPN,
            TYPE_GPS_ENABLED,
            -> {
                if (value.toBooleanStrictOrNull() == null) "请输入 true 或 false" else null
            }
            TYPE_WIFI_SSID, TYPE_DOCK -> {
                if (value.isBlank()) "请输入值" else null
            }
            else -> null
        }

    fun validateAction(
        typePosition: Int,
        value: String,
        extra: String,
        port: String,
    ): String? {
        if (value.isBlank()) {
            return when (typePosition) {
                ACTION_OPEN_URL -> "请输入网址"
                ACTION_SEND_DATA_SMS -> "请输入手机号码"
                else -> "请输入通知标题"
            }
        }
        return when (typePosition) {
            ACTION_OPEN_URL -> null
            ACTION_SEND_DATA_SMS ->
                when {
                    port.toIntOrNull() !in 1..65535 -> "动作端口必须是 1-65535"
                    parseHex(extra).isEmpty() -> "请输入有效 HEX 数据"
                    else -> null
                }
            else -> if (extra.isBlank()) "请输入通知内容" else null
        }
    }

    fun parseHex(value: String): ByteArray {
        val clean = value.removePrefix("0x").filterNot { it.isWhitespace() }
        if (clean.isEmpty() || clean.length % 2 != 0) return ByteArray(0)
        return clean.chunked(2).mapNotNull { it.toIntOrNull(16)?.toByte() }.toByteArray()
    }

    private val PACKAGE_NAME_PATTERN = Regex("^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)+$")

    fun ruleMatchesTriggerFilter(
        trigger: dev.niza.autodroid.engine.Trigger,
        filterPosition: Int,
    ): Boolean =
        filterPosition == FILTER_ALL ||
            when (filterPosition) {
                TYPE_APP -> trigger is dev.niza.autodroid.engine.AppLaunchedTrigger
                TYPE_DATA_SMS -> trigger is dev.niza.autodroid.engine.DataSmsReceivedTrigger
                TYPE_SCHEDULE -> trigger is dev.niza.autodroid.engine.ScheduleTrigger
                TYPE_SCREEN_ON_OFF -> trigger is dev.niza.autodroid.engine.ScreenOnOffTrigger
                TYPE_BATTERY_LEVEL -> trigger is dev.niza.autodroid.engine.BatteryLevelTrigger
                TYPE_AIRPLANE_MODE -> trigger is dev.niza.autodroid.engine.AirplaneModeTrigger
                TYPE_BLUETOOTH -> trigger is dev.niza.autodroid.engine.BluetoothTrigger
                TYPE_EXTERNAL_POWER -> trigger is dev.niza.autodroid.engine.ExternalPowerTrigger
                TYPE_HEADPHONES -> trigger is dev.niza.autodroid.engine.HeadphonesTrigger
                TYPE_HOTSPOT -> trigger is dev.niza.autodroid.engine.HotspotTrigger
                TYPE_SILENT_MODE -> trigger is dev.niza.autodroid.engine.SilentModeTrigger
                TYPE_WIFI_CONNECTION -> trigger is dev.niza.autodroid.engine.WifiConnectionTrigger
                TYPE_WIFI_SSID -> trigger is dev.niza.autodroid.engine.WifiSSIDTrigger
                TYPE_SIGNAL_ON_OFF -> trigger is dev.niza.autodroid.engine.SignalOnOffTrigger
                TYPE_DOCK -> trigger is dev.niza.autodroid.engine.DockTrigger
                TYPE_VPN -> trigger is dev.niza.autodroid.engine.VpnTrigger
                TYPE_GPS_ENABLED -> trigger is dev.niza.autodroid.engine.GPSEnabledTrigger
                TYPE_DEVICE_UNLOCKED -> trigger is dev.niza.autodroid.engine.DeviceUnlockedTrigger
                TYPE_CLIPBOARD_CHANGE -> trigger is dev.niza.autodroid.engine.ClipboardChangeTrigger
                TYPE_DARK_THEME -> trigger is dev.niza.autodroid.engine.DarkThemeTrigger
                TYPE_MEDIA_BUTTON -> trigger is dev.niza.autodroid.engine.MediaButtonTrigger
                TYPE_NOTIFICATION -> trigger is dev.niza.autodroid.engine.NotificationTrigger
                else -> trigger is dev.niza.autodroid.engine.LinkOpenedTrigger
            }

    fun ruleMatchesActionFilter(
        action: dev.niza.autodroid.engine.Action?,
        filterPosition: Int,
    ): Boolean =
        filterPosition == FILTER_ALL ||
            when (filterPosition) {
                ACTION_OPEN_URL -> action is dev.niza.autodroid.engine.OpenUrlAction
                ACTION_SEND_DATA_SMS -> action is dev.niza.autodroid.engine.SendDataSmsAction
                else -> action is dev.niza.autodroid.engine.NotifyAction
            }

    fun actionLabel(action: dev.niza.autodroid.engine.Action?): String =
        when (action) {
            is dev.niza.autodroid.engine.OpenUrlAction -> "打开网址"
            is dev.niza.autodroid.engine.SendDataSmsAction -> "发送数据短信"
            else -> "显示通知"
        }

    fun triggerLabel(trigger: dev.niza.autodroid.engine.Trigger): String =
        when (trigger) {
            is dev.niza.autodroid.engine.AppLaunchedTrigger -> "应用启动"
            is dev.niza.autodroid.engine.DataSmsReceivedTrigger -> "数据短信"
            is dev.niza.autodroid.engine.ScheduleTrigger -> "定时器"
            is dev.niza.autodroid.engine.ScreenOnOffTrigger -> "屏幕开关"
            is dev.niza.autodroid.engine.BatteryLevelTrigger -> "电量"
            is dev.niza.autodroid.engine.AirplaneModeTrigger -> "飞行模式"
            is dev.niza.autodroid.engine.BluetoothTrigger -> "蓝牙"
            is dev.niza.autodroid.engine.ExternalPowerTrigger -> "充电"
            is dev.niza.autodroid.engine.HeadphonesTrigger -> "耳机"
            is dev.niza.autodroid.engine.HotspotTrigger -> "热点"
            is dev.niza.autodroid.engine.SilentModeTrigger -> "静音"
            is dev.niza.autodroid.engine.WifiConnectionTrigger -> "WiFi"
            is dev.niza.autodroid.engine.WifiSSIDTrigger -> "WiFi名称"
            is dev.niza.autodroid.engine.SignalOnOffTrigger -> "信号"
            is dev.niza.autodroid.engine.DockTrigger -> "底座"
            is dev.niza.autodroid.engine.VpnTrigger -> "VPN"
            is dev.niza.autodroid.engine.GPSEnabledTrigger -> "GPS"
            is dev.niza.autodroid.engine.DeviceUnlockedTrigger -> "解锁"
            is dev.niza.autodroid.engine.ClipboardChangeTrigger -> "剪贴板"
            is dev.niza.autodroid.engine.DarkThemeTrigger -> "深色模式"
            is dev.niza.autodroid.engine.MediaButtonTrigger -> "媒体按键"
            is dev.niza.autodroid.engine.NotificationTrigger -> "通知"
            else -> "打开链接"
        }
}
