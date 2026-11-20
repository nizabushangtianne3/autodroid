package dev.niza.autodroid.engine

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Trigger {
    val id: String
}

val Trigger.value: String?
    get() =
        when (this) {
            is LinkOpenedTrigger -> host
            is DataSmsReceivedTrigger -> destinationPort.toString()
            is AppLaunchedTrigger -> packageName
            is ScheduleTrigger -> "every ${intervalMillis}ms"
            is ScreenOnOffTrigger -> if (screenOn) "screen on" else "screen off"
            is BatteryLevelTrigger -> "battery $level%"
            is AirplaneModeTrigger -> if (enabled) "airplane on" else "airplane off"
            is BluetoothTrigger -> if (enabled) "bluetooth on" else "bluetooth off"
            is ExternalPowerTrigger -> if (connected) "power connected" else "power disconnected"
            is HeadphonesTrigger -> if (connected) "headphones connected" else "headphones disconnected"
            is HotspotTrigger -> if (enabled) "hotspot on" else "hotspot off"
            is SilentModeTrigger -> if (enabled) "silent on" else "silent off"
            is WifiConnectionTrigger -> if (connected) "wifi connected" else "wifi disconnected"
            is WifiSSIDTrigger -> "wifi ssid $ssid"
            is SignalOnOffTrigger -> if (signalOn) "signal on" else "signal off"
            is DockTrigger -> "dock $state"
            is VpnTrigger -> if (connected) "vpn connected" else "vpn disconnected"
            is GPSEnabledTrigger -> if (enabled) "gps on" else "gps off"
            is DeviceUnlockedTrigger -> "device unlocked"
            is ClipboardChangeTrigger -> "clipboard changed"
            is DarkThemeTrigger -> if (enabled) "dark theme on" else "dark theme off"
            is MediaButtonTrigger -> "media button"
            is NotificationTrigger -> "notification received"
        }

val Action.summary: String
    get() =
        when (this) {
            is OpenUrlAction -> url
            is SendDataSmsAction -> "$sender:$destinationPort • ${payload.size} bytes"
            is NotifyAction -> "$title • $body"
            is SetVariableAction -> "$name = $value"
            is IfConditionAction -> "if $expression"
            is ElseConditionAction -> "else"
            is EndIfAction -> "end if"
            is WaitAction -> "wait ${durationMillis}ms"
            is RepeatStartAction -> "repeat ${count}x"
            is RepeatEndAction -> "end repeat"
        }

@Serializable
sealed interface Constraint {
    val id: String

    fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean
}

@Serializable
@SerialName("constraint_battery_level")
data class BatteryLevelConstraint(
    override val id: String,
    val level: Int,
    val direction: String = "above",
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.batteryLevel?.let { if (direction == "below") it <= level else it >= level } ?: false
}

@Serializable
@SerialName("constraint_battery_saver")
data class BatterySaverConstraint(
    override val id: String,
    val enabled: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.batterySaver == enabled
}

@Serializable
@SerialName("constraint_bluetooth")
data class BluetoothConstraint(
    override val id: String,
    val enabled: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.bluetoothEnabled == enabled
}

@Serializable
@SerialName("constraint_cell_connection")
data class CellConnectionConstraint(
    override val id: String,
    val requiredType: String,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.cellConnectionType == requiredType
}

@Serializable
@SerialName("constraint_day_time")
data class DayTimeConstraint(
    override val id: String,
    val startMinuteOfDay: Int,
    val endMinuteOfDay: Int,
    val days: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean {
        val calendar = state.calendar
        if (calendar == null) return true
        val day = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val dayMatches = days.contains(if (day == java.util.Calendar.SUNDAY) 7 else day - 1)
        if (!dayMatches) return false
        val minute = calendar.get(java.util.Calendar.HOUR_OF_DAY) * 60 + calendar.get(java.util.Calendar.MINUTE)
        return if (startMinuteOfDay <= endMinuteOfDay) {
            minute in startMinuteOfDay..endMinuteOfDay
        } else {
            minute >= startMinuteOfDay || minute <= endMinuteOfDay
        }
    }
}

@Serializable
@SerialName("constraint_device_locked")
data class DeviceLockedConstraint(
    override val id: String,
    val locked: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.deviceLocked == locked
}

@Serializable
@SerialName("constraint_external_power")
data class ExternalPowerConstraint(
    override val id: String,
    val connected: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.externalPowerConnected == connected
}

@Serializable
@SerialName("constraint_gps_enabled")
data class GPSEnabledConstraint(
    override val id: String,
    val enabled: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.gpsEnabled == enabled
}

@Serializable
@SerialName("constraint_headphones")
data class HeadphonesConstraint(
    override val id: String,
    val connected: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.headphonesConnected == connected
}

@Serializable
@SerialName("constraint_network_type")
data class NetworkTypeConstraint(
    override val id: String,
    val requiredType: String,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.networkType == requiredType
}

@Serializable
@SerialName("constraint_screen_on_off")
data class ScreenOnOffConstraint(
    override val id: String,
    val screenOn: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.screenOn == screenOn
}

@Serializable
@SerialName("constraint_silent_mode")
data class SilentModeConstraint(
    override val id: String,
    val enabled: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.silentModeEnabled == enabled
}

@Serializable
@SerialName("constraint_trigger_enabled")
data class TriggerEnabledConstraint(
    override val id: String,
    val triggerId: String,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.enabledTriggerIds.contains(triggerId)
}

@Serializable
@SerialName("constraint_variable")
data class VariableConstraint(
    override val id: String,
    val expression: String,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = evaluateBooleanExpression(expression, variables)
}

@Serializable
@SerialName("constraint_wifi")
data class WifiConstraint(
    override val id: String,
    val connected: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.wifiConnected == connected
}

@Serializable
@SerialName("constraint_airplane_mode")
data class AirplaneModeConstraint(
    override val id: String,
    val enabled: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.airplaneModeEnabled == enabled
}

@Serializable
data class DeviceState(
    val batteryLevel: Int? = null,
    val batterySaver: Boolean = false,
    val bluetoothEnabled: Boolean = false,
    val cellConnectionType: String = "unknown",
    @Contextual val calendar: java.util.Calendar? = null,
    val deviceLocked: Boolean = false,
    val externalPowerConnected: Boolean = false,
    val gpsEnabled: Boolean = false,
    val headphonesConnected: Boolean = false,
    val networkType: String = "unknown",
    val screenOn: Boolean = false,
    val silentModeEnabled: Boolean = false,
    val enabledTriggerIds: Set<String> = emptySet(),
    val wifiConnected: Boolean = false,
    val airplaneModeEnabled: Boolean = false,
)

@Serializable
@SerialName("link")
data class LinkOpenedTrigger(
    override val id: String,
    val host: String? = null,
    val scheme: String? = null,
    val pathPrefix: String? = null,
) : Trigger

@Serializable
@SerialName("data_sms")
data class DataSmsReceivedTrigger(
    override val id: String,
    val destinationPort: Int,
    val sender: String? = null,
) : Trigger

@Serializable
@SerialName("app_launched")
data class AppLaunchedTrigger(
    override val id: String,
    val packageName: String,
) : Trigger

@Serializable
@SerialName("schedule")
data class ScheduleTrigger(
    override val id: String,
    val intervalMillis: Long,
    val startAtElapsedRealtime: Long = 0,
) : Trigger

@Serializable
@SerialName("screen_on_off")
data class ScreenOnOffTrigger(
    override val id: String,
    val screenOn: Boolean,
) : Trigger

@Serializable
@SerialName("battery_level")
data class BatteryLevelTrigger(
    override val id: String,
    val level: Int,
    val direction: String = "below",
) : Trigger

@Serializable
@SerialName("airplane_mode")
data class AirplaneModeTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("bluetooth")
data class BluetoothTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("external_power")
data class ExternalPowerTrigger(
    override val id: String,
    val connected: Boolean,
) : Trigger

@Serializable
@SerialName("headphones")
data class HeadphonesTrigger(
    override val id: String,
    val connected: Boolean,
) : Trigger

@Serializable
@SerialName("hotspot")
data class HotspotTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("silent_mode")
data class SilentModeTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("wifi_connection")
data class WifiConnectionTrigger(
    override val id: String,
    val connected: Boolean,
) : Trigger

@Serializable
@SerialName("wifi_ssid")
data class WifiSSIDTrigger(
    override val id: String,
    val ssid: String,
) : Trigger

@Serializable
@SerialName("signal_on_off")
data class SignalOnOffTrigger(
    override val id: String,
    val signalOn: Boolean,
) : Trigger

@Serializable
@SerialName("dock")
data class DockTrigger(
    override val id: String,
    val state: String,
) : Trigger

@Serializable
@SerialName("vpn")
data class VpnTrigger(
    override val id: String,
    val connected: Boolean,
) : Trigger

@Serializable
@SerialName("gps_enabled")
data class GPSEnabledTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("device_unlocked")
data class DeviceUnlockedTrigger(
    override val id: String,
) : Trigger

@Serializable
@SerialName("clipboard_change")
data class ClipboardChangeTrigger(
    override val id: String,
) : Trigger

@Serializable
@SerialName("dark_theme")
data class DarkThemeTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("media_button")
data class MediaButtonTrigger(
    override val id: String,
    val button: String,
) : Trigger

@Serializable
@SerialName("notification")
data class NotificationTrigger(
    override val id: String,
    val packageName: String? = null,
) : Trigger

@Serializable
sealed interface Action {
    val id: String
}

@Serializable
@SerialName("notify")
data class NotifyAction(
    override val id: String,
    val title: String,
    val body: String,
) : Action

@Serializable
@SerialName("send_data_sms")
data class SendDataSmsAction(
    override val id: String,
    val destinationPort: Int,
    val sender: String,
    val payload: ByteArray,
) : Action

@Serializable
@SerialName("open_url")
data class OpenUrlAction(
    override val id: String,
    val url: String,
) : Action

@Serializable
data class Condition(
    val expression: String,
)

@Serializable
@SerialName("set_variable")
data class SetVariableAction(
    override val id: String,
    val name: String,
    val value: String,
) : Action

@Serializable
@SerialName("if_condition")
data class IfConditionAction(
    override val id: String,
    val expression: String,
) : Action

@Serializable
@SerialName("else_condition")
data class ElseConditionAction(
    override val id: String,
) : Action

@Serializable
@SerialName("end_if")
data class EndIfAction(
    override val id: String,
) : Action

@Serializable
@SerialName("wait")
data class WaitAction(
    override val id: String,
    val durationMillis: Long,
) : Action

@Serializable
@SerialName("repeat_start")
data class RepeatStartAction(
    override val id: String,
    val count: Int,
) : Action

@Serializable
@SerialName("repeat_end")
data class RepeatEndAction(
    override val id: String,
) : Action

@Serializable
data class Rule(
    val id: String,
    val name: String,
    val trigger: Trigger,
    val conditions: List<Condition> = emptyList(),
    val constraints: List<Constraint> = emptyList(),
    val actions: List<Action>,
    val enabled: Boolean = true,
)
