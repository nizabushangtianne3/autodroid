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

    const val ACTION_NOTIFY = 0
    const val ACTION_OPEN_URL = 1
    const val ACTION_SEND_DATA_SMS = 2

    fun triggerHint(typePosition: Int): String =
        when (typePosition) {
            TYPE_APP -> "App package name"
            TYPE_DATA_SMS -> "Data SMS destination port"
            TYPE_SCHEDULE -> "Interval milliseconds"
            TYPE_SCREEN_ON_OFF -> "Screen on (true/false)"
            TYPE_BATTERY_LEVEL -> "Battery level percentage"
            TYPE_AIRPLANE_MODE -> "Airplane enabled (true/false)"
            TYPE_BLUETOOTH -> "Bluetooth enabled (true/false)"
            TYPE_EXTERNAL_POWER -> "Power connected (true/false)"
            TYPE_HEADPHONES -> "Headphones connected (true/false)"
            TYPE_HOTSPOT -> "Hotspot enabled (true/false)"
            TYPE_SILENT_MODE -> "Silent mode enabled (true/false)"
            TYPE_WIFI_CONNECTION -> "WiFi connected (true/false)"
            TYPE_WIFI_SSID -> "WiFi SSID"
            TYPE_SIGNAL_ON_OFF -> "Signal on (true/false)"
            TYPE_DOCK -> "Dock state"
            TYPE_VPN -> "VPN connected (true/false)"
            TYPE_GPS_ENABLED -> "GPS enabled (true/false)"
            else -> "Link host"
        }

    fun actionHint(typePosition: Int): String =
        when (typePosition) {
            ACTION_OPEN_URL -> "URL"
            ACTION_SEND_DATA_SMS -> "Destination phone number"
            else -> "Notification title"
        }

    fun actionExtraHint(typePosition: Int): String =
        when (typePosition) {
            ACTION_OPEN_URL -> "Unused"
            ACTION_SEND_DATA_SMS -> "Payload hex"
            else -> "Notification body"
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
                    "Invalid Android package name"
                }
            TYPE_LINK -> if (value.isBlank()) "Link host is required" else null
            TYPE_DATA_SMS -> {
                val port = value.toIntOrNull()
                if (port == null || port !in 1..65535) "Data SMS port must be 1-65535" else null
            }
            TYPE_SCHEDULE -> {
                val interval = value.toLongOrNull()
                if (interval == null || interval < 1000) "Interval must be at least 1000ms" else null
            }
            TYPE_BATTERY_LEVEL -> {
                val level = value.toIntOrNull()
                if (level == null || level !in 0..100) "Battery level must be 0-100" else null
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
                if (value.toBooleanStrictOrNull() == null) "Value must be true or false" else null
            }
            TYPE_WIFI_SSID, TYPE_DOCK -> {
                if (value.isBlank()) "Value is required" else null
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
                ACTION_OPEN_URL -> "URL is required"
                ACTION_SEND_DATA_SMS -> "Destination phone number is required"
                else -> "Notification title is required"
            }
        }
        return when (typePosition) {
            ACTION_OPEN_URL -> null
            ACTION_SEND_DATA_SMS ->
                when {
                    port.toIntOrNull() !in 1..65535 -> "Action port must be 1-65535"
                    parseHex(extra).isEmpty() -> "Payload hex is required"
                    else -> null
                }
            else -> if (extra.isBlank()) "Notification body is required" else null
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
            is dev.niza.autodroid.engine.OpenUrlAction -> "Open URL"
            is dev.niza.autodroid.engine.SendDataSmsAction -> "Send SMS"
            else -> "Notify"
        }

    fun triggerLabel(trigger: dev.niza.autodroid.engine.Trigger): String =
        when (trigger) {
            is dev.niza.autodroid.engine.AppLaunchedTrigger -> "App"
            is dev.niza.autodroid.engine.DataSmsReceivedTrigger -> "SMS"
            is dev.niza.autodroid.engine.ScheduleTrigger -> "Schedule"
            is dev.niza.autodroid.engine.ScreenOnOffTrigger -> "Screen"
            is dev.niza.autodroid.engine.BatteryLevelTrigger -> "Battery"
            is dev.niza.autodroid.engine.AirplaneModeTrigger -> "Airplane"
            is dev.niza.autodroid.engine.BluetoothTrigger -> "Bluetooth"
            is dev.niza.autodroid.engine.ExternalPowerTrigger -> "Power"
            is dev.niza.autodroid.engine.HeadphonesTrigger -> "Headphones"
            is dev.niza.autodroid.engine.HotspotTrigger -> "Hotspot"
            is dev.niza.autodroid.engine.SilentModeTrigger -> "Silent"
            is dev.niza.autodroid.engine.WifiConnectionTrigger -> "WiFi"
            is dev.niza.autodroid.engine.WifiSSIDTrigger -> "WiFi SSID"
            is dev.niza.autodroid.engine.SignalOnOffTrigger -> "Signal"
            is dev.niza.autodroid.engine.DockTrigger -> "Dock"
            is dev.niza.autodroid.engine.VpnTrigger -> "VPN"
            is dev.niza.autodroid.engine.GPSEnabledTrigger -> "GPS"
            else -> "Link"
        }
}
