package dev.niza.autodroid.engine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Trigger {
    val id: String
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
)
