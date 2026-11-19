package dev.niza.autodroid.engine

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
        }

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
data class Rule(
    val id: String,
    val name: String,
    val trigger: Trigger,
    val conditions: List<Condition> = emptyList(),
    val actions: List<Action>,
    val enabled: Boolean = true,
)
