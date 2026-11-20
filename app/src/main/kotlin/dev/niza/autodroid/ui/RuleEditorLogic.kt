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
    const val TYPE_APP_LIFECYCLE = 23
    const val TYPE_POWER_SAVE_MODE = 24

    const val ACTION_NOTIFY = 0
    const val ACTION_OPEN_URL = 1
    const val ACTION_SEND_DATA_SMS = 2
    const val ACTION_SET_VARIABLE = 3
    const val ACTION_IF_CONDITION = 4
    const val ACTION_ELSE_CONDITION = 5
    const val ACTION_END_IF = 6
    const val ACTION_WAIT = 7
    const val ACTION_REPEAT_START = 8
    const val ACTION_REPEAT_END = 9
    const val ACTION_LAUNCH_APP = 10
    const val ACTION_CLIPBOARD = 11

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
            TYPE_APP_LIFECYCLE -> "installed / removed"
            TYPE_POWER_SAVE_MODE -> "省电模式开启 true / false"
            else -> "链接主机，例如 example.com"
        }

    fun triggerLabels(): List<String> =
        listOf(
            "Open Link", "Application Launched", "Data SMS", "Timer",
            "Screen On / Off", "Battery Level", "Airplane Mode", "Bluetooth", "External Power",
            "Headphones", "Hotspot", "Silent Mode", "WiFi Connection", "WiFi SSID",
            "Signal On / Off", "Dock", "VPN", "GPS Enabled", "Device Unlocked",
            "Clipboard Change", "Dark Theme", "Media Button", "Notification",
            "App Install / Remove", "Battery Saver",
        )

    fun typePosition(trigger: dev.niza.autodroid.engine.Trigger): Int =
        when (trigger) {
            is dev.niza.autodroid.engine.AppLaunchedTrigger -> TYPE_APP
            is dev.niza.autodroid.engine.DataSmsReceivedTrigger -> TYPE_DATA_SMS
            is dev.niza.autodroid.engine.ScheduleTrigger -> TYPE_SCHEDULE
            is dev.niza.autodroid.engine.ScreenOnOffTrigger -> TYPE_SCREEN_ON_OFF
            is dev.niza.autodroid.engine.BatteryLevelTrigger -> TYPE_BATTERY_LEVEL
            is dev.niza.autodroid.engine.AirplaneModeTrigger -> TYPE_AIRPLANE_MODE
            is dev.niza.autodroid.engine.BluetoothTrigger -> TYPE_BLUETOOTH
            is dev.niza.autodroid.engine.ExternalPowerTrigger -> TYPE_EXTERNAL_POWER
            is dev.niza.autodroid.engine.HeadphonesTrigger -> TYPE_HEADPHONES
            is dev.niza.autodroid.engine.HotspotTrigger -> TYPE_HOTSPOT
            is dev.niza.autodroid.engine.SilentModeTrigger -> TYPE_SILENT_MODE
            is dev.niza.autodroid.engine.WifiConnectionTrigger -> TYPE_WIFI_CONNECTION
            is dev.niza.autodroid.engine.WifiSSIDTrigger -> TYPE_WIFI_SSID
            is dev.niza.autodroid.engine.SignalOnOffTrigger -> TYPE_SIGNAL_ON_OFF
            is dev.niza.autodroid.engine.DockTrigger -> TYPE_DOCK
            is dev.niza.autodroid.engine.VpnTrigger -> TYPE_VPN
            is dev.niza.autodroid.engine.GPSEnabledTrigger -> TYPE_GPS_ENABLED
            is dev.niza.autodroid.engine.DeviceUnlockedTrigger -> TYPE_DEVICE_UNLOCKED
            is dev.niza.autodroid.engine.ClipboardChangeTrigger -> TYPE_CLIPBOARD_CHANGE
            is dev.niza.autodroid.engine.DarkThemeTrigger -> TYPE_DARK_THEME
            is dev.niza.autodroid.engine.MediaButtonTrigger -> TYPE_MEDIA_BUTTON
            is dev.niza.autodroid.engine.NotificationTrigger -> TYPE_NOTIFICATION
            is dev.niza.autodroid.engine.AppLifecycleTrigger -> TYPE_APP_LIFECYCLE
            is dev.niza.autodroid.engine.PowerSaveModeTrigger -> TYPE_POWER_SAVE_MODE
            else -> TYPE_LINK
        }

    fun actionLabels(): List<String> =
        listOf(
            "Display Notification", "Open URL", "Send Data SMS", "Set Variable",
            "If", "Else", "End If", "Wait", "Repeat", "End Repeat",
            "Launch App", "Copy To Clipboard",
        )

    fun actionTypePosition(action: dev.niza.autodroid.engine.Action?): Int =
        when (action) {
            is dev.niza.autodroid.engine.OpenUrlAction -> ACTION_OPEN_URL
            is dev.niza.autodroid.engine.SendDataSmsAction -> ACTION_SEND_DATA_SMS
            is dev.niza.autodroid.engine.SetVariableAction -> ACTION_SET_VARIABLE
            is dev.niza.autodroid.engine.IfConditionAction -> ACTION_IF_CONDITION
            is dev.niza.autodroid.engine.ElseConditionAction -> ACTION_ELSE_CONDITION
            is dev.niza.autodroid.engine.EndIfAction -> ACTION_END_IF
            is dev.niza.autodroid.engine.WaitAction -> ACTION_WAIT
            is dev.niza.autodroid.engine.RepeatStartAction -> ACTION_REPEAT_START
            is dev.niza.autodroid.engine.RepeatEndAction -> ACTION_REPEAT_END
            is dev.niza.autodroid.engine.LaunchAppAction -> ACTION_LAUNCH_APP
            is dev.niza.autodroid.engine.ClipboardAction -> ACTION_CLIPBOARD
            else -> ACTION_NOTIFY
        }

    fun actionValue(action: dev.niza.autodroid.engine.Action?): String =
        when (action) {
            is dev.niza.autodroid.engine.NotifyAction -> action.title
            is dev.niza.autodroid.engine.OpenUrlAction -> action.url
            is dev.niza.autodroid.engine.SendDataSmsAction -> action.sender
            is dev.niza.autodroid.engine.SetVariableAction -> action.name
            is dev.niza.autodroid.engine.IfConditionAction -> action.expression
            is dev.niza.autodroid.engine.WaitAction -> action.durationMillis.toString()
            is dev.niza.autodroid.engine.RepeatStartAction -> action.count.toString()
            is dev.niza.autodroid.engine.LaunchAppAction -> action.packageName
            is dev.niza.autodroid.engine.ClipboardAction -> action.text
            else -> ""
        }

    fun actionExtra(action: dev.niza.autodroid.engine.Action?): String =
        when (action) {
            is dev.niza.autodroid.engine.NotifyAction -> action.body
            is dev.niza.autodroid.engine.SendDataSmsAction -> action.payload.joinToString("") { "%02X".format(it) }
            is dev.niza.autodroid.engine.SetVariableAction -> action.value
            else -> ""
        }

    fun buildTrigger(
        triggerId: String,
        typePosition: Int,
        value: String,
        sender: String?,
    ): dev.niza.autodroid.engine.Trigger =
        when (typePosition) {
            TYPE_APP -> dev.niza.autodroid.engine.AppLaunchedTrigger(triggerId, value)
            TYPE_DATA_SMS ->
                dev.niza.autodroid.engine.DataSmsReceivedTrigger(
                    triggerId,
                    destinationPort = value.toIntOrNull() ?: 0,
                    sender = sender,
                )
            TYPE_SCHEDULE -> dev.niza.autodroid.engine.ScheduleTrigger(triggerId, intervalMillis = value.toLongOrNull() ?: 0)
            TYPE_SCREEN_ON_OFF ->
                dev.niza.autodroid.engine.ScreenOnOffTrigger(triggerId, screenOn = value.toBooleanStrictOrNull() ?: true)
            TYPE_BATTERY_LEVEL -> dev.niza.autodroid.engine.BatteryLevelTrigger(triggerId, level = value.toIntOrNull() ?: 0)
            TYPE_AIRPLANE_MODE ->
                dev.niza.autodroid.engine.AirplaneModeTrigger(triggerId, enabled = value.toBooleanStrictOrNull() ?: true)
            TYPE_BLUETOOTH ->
                dev.niza.autodroid.engine.BluetoothTrigger(triggerId, enabled = value.toBooleanStrictOrNull() ?: true)
            TYPE_EXTERNAL_POWER ->
                dev.niza.autodroid.engine.ExternalPowerTrigger(triggerId, connected = value.toBooleanStrictOrNull() ?: true)
            TYPE_HEADPHONES ->
                dev.niza.autodroid.engine.HeadphonesTrigger(triggerId, connected = value.toBooleanStrictOrNull() ?: true)
            TYPE_HOTSPOT ->
                dev.niza.autodroid.engine.HotspotTrigger(triggerId, enabled = value.toBooleanStrictOrNull() ?: true)
            TYPE_SILENT_MODE ->
                dev.niza.autodroid.engine.SilentModeTrigger(triggerId, enabled = value.toBooleanStrictOrNull() ?: true)
            TYPE_WIFI_CONNECTION ->
                dev.niza.autodroid.engine.WifiConnectionTrigger(triggerId, connected = value.toBooleanStrictOrNull() ?: true)
            TYPE_WIFI_SSID -> dev.niza.autodroid.engine.WifiSSIDTrigger(triggerId, ssid = value)
            TYPE_SIGNAL_ON_OFF ->
                dev.niza.autodroid.engine.SignalOnOffTrigger(triggerId, signalOn = value.toBooleanStrictOrNull() ?: true)
            TYPE_DOCK -> dev.niza.autodroid.engine.DockTrigger(triggerId, state = value)
            TYPE_VPN -> dev.niza.autodroid.engine.VpnTrigger(triggerId, connected = value.toBooleanStrictOrNull() ?: true)
            TYPE_GPS_ENABLED ->
                dev.niza.autodroid.engine.GPSEnabledTrigger(triggerId, enabled = value.toBooleanStrictOrNull() ?: true)
            TYPE_DEVICE_UNLOCKED -> dev.niza.autodroid.engine.DeviceUnlockedTrigger(triggerId)
            TYPE_CLIPBOARD_CHANGE -> dev.niza.autodroid.engine.ClipboardChangeTrigger(triggerId)
            TYPE_DARK_THEME ->
                dev.niza.autodroid.engine.DarkThemeTrigger(triggerId, enabled = value.toBooleanStrictOrNull() ?: true)
            TYPE_MEDIA_BUTTON -> dev.niza.autodroid.engine.MediaButtonTrigger(triggerId, button = value)
            TYPE_NOTIFICATION ->
                dev.niza.autodroid.engine.NotificationTrigger(triggerId, packageName = value.ifBlank { null })
            TYPE_APP_LIFECYCLE ->
                dev.niza.autodroid.engine.AppLifecycleTrigger(
                    triggerId,
                    added = value.equals("installed", ignoreCase = true),
                )
            TYPE_POWER_SAVE_MODE ->
                dev.niza.autodroid.engine.PowerSaveModeTrigger(
                    triggerId,
                    enabled = value.toBooleanStrictOrNull() ?: false,
                )
            else -> dev.niza.autodroid.engine.LinkOpenedTrigger(triggerId, host = value)
        }

    fun actionHint(typePosition: Int): String =
        when (typePosition) {
            ACTION_OPEN_URL -> "网址"
            ACTION_SEND_DATA_SMS -> "手机号码"
            ACTION_SET_VARIABLE -> "变量名"
            ACTION_IF_CONDITION -> "条件，例如 {count} > 50"
            ACTION_ELSE_CONDITION -> "可留空"
            ACTION_END_IF -> "可留空"
            ACTION_WAIT -> "等待毫秒"
            ACTION_REPEAT_START -> "重复次数"
            ACTION_REPEAT_END -> "可留空"
            ACTION_LAUNCH_APP -> "应用包名"
            ACTION_CLIPBOARD -> "剪贴板内容"
            else -> "通知标题"
        }

    fun actionExtraHint(typePosition: Int): String =
        when (typePosition) {
            ACTION_OPEN_URL -> "可留空"
            ACTION_SEND_DATA_SMS -> "数据内容 HEX"
            ACTION_SET_VARIABLE -> "变量值"
            ACTION_IF_CONDITION -> "可留空"
            ACTION_ELSE_CONDITION -> "可留空"
            ACTION_END_IF -> "可留空"
            ACTION_WAIT -> "可留空"
            ACTION_REPEAT_START -> "可留空"
            ACTION_REPEAT_END -> "可留空"
            ACTION_LAUNCH_APP -> "可留空"
            ACTION_CLIPBOARD -> "可留空"
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
            ACTION_SET_VARIABLE, ACTION_WAIT, ACTION_REPEAT_START ->
                when {
                    value.toLongOrNull() == null -> "请输入数字"
                    ACTION_WAIT == typePosition && (value.toLongOrNull() ?: 0) < 0 -> "等待时间不能小于 0"
                    ACTION_REPEAT_START == typePosition && (value.toLongOrNull() ?: 0) < 1 -> "重复次数至少 1"
                    ACTION_SET_VARIABLE == typePosition && extra.isBlank() -> "请输入变量值"
                    else -> null
                }
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
                TYPE_APP_LIFECYCLE -> trigger is dev.niza.autodroid.engine.AppLifecycleTrigger
                TYPE_POWER_SAVE_MODE -> trigger is dev.niza.autodroid.engine.PowerSaveModeTrigger
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
                ACTION_SET_VARIABLE -> action is dev.niza.autodroid.engine.SetVariableAction
                ACTION_IF_CONDITION -> action is dev.niza.autodroid.engine.IfConditionAction
                ACTION_ELSE_CONDITION -> action is dev.niza.autodroid.engine.ElseConditionAction
                ACTION_END_IF -> action is dev.niza.autodroid.engine.EndIfAction
                ACTION_WAIT -> action is dev.niza.autodroid.engine.WaitAction
                ACTION_REPEAT_START -> action is dev.niza.autodroid.engine.RepeatStartAction
                ACTION_REPEAT_END -> action is dev.niza.autodroid.engine.RepeatEndAction
                ACTION_LAUNCH_APP -> action is dev.niza.autodroid.engine.LaunchAppAction
                ACTION_CLIPBOARD -> action is dev.niza.autodroid.engine.ClipboardAction
                else -> action is dev.niza.autodroid.engine.NotifyAction
            }

    fun actionLabel(action: dev.niza.autodroid.engine.Action?): String =
        when (action) {
            is dev.niza.autodroid.engine.OpenUrlAction -> "打开网址"
            is dev.niza.autodroid.engine.SendDataSmsAction -> "发送数据短信"
            is dev.niza.autodroid.engine.SetVariableAction -> "设置变量"
            is dev.niza.autodroid.engine.IfConditionAction -> "如果"
            is dev.niza.autodroid.engine.ElseConditionAction -> "否则"
            is dev.niza.autodroid.engine.EndIfAction -> "结束如果"
            is dev.niza.autodroid.engine.WaitAction -> "等待"
            is dev.niza.autodroid.engine.RepeatStartAction -> "重复"
            is dev.niza.autodroid.engine.RepeatEndAction -> "结束重复"
            is dev.niza.autodroid.engine.LaunchAppAction -> "启动应用"
            is dev.niza.autodroid.engine.ClipboardAction -> "复制到剪贴板"
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
            is dev.niza.autodroid.engine.AppLifecycleTrigger ->
                if (trigger.added) "应用安装" else "应用卸载"
            is dev.niza.autodroid.engine.PowerSaveModeTrigger -> "省电模式"
            else -> "打开链接"
        }
}
