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
        }

val Action.summary: String
    get() =
        when (this) {
            is OpenUrlAction -> url
            is SendDataSmsAction -> "$sender:$destinationPort • ${payload.size} bytes"
            is NotifyAction -> "$title • $body"
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
data class Rule(
    val id: String,
    val name: String,
    val trigger: Trigger,
    val conditions: List<Condition> = emptyList(),
    val actions: List<Action>,
    val enabled: Boolean = true,
)
