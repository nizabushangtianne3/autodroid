package dev.niza.autodroid.engine

sealed interface Trigger {
    val id: String
}

data class LinkOpenedTrigger(
    override val id: String,
    val host: String? = null,
    val scheme: String? = null,
    val pathPrefix: String? = null,
) : Trigger

data class DataSmsReceivedTrigger(
    override val id: String,
    val destinationPort: Int,
    val sender: String? = null,
) : Trigger

data class AppLaunchedTrigger(
    override val id: String,
    val packageName: String,
) : Trigger

sealed interface Action {
    val id: String
}

data class NotifyAction(
    override val id: String,
    val title: String,
    val body: String,
) : Action

data class SendDataSmsAction(
    override val id: String,
    val destinationPort: Int,
    val sender: String,
    val payload: ByteArray,
) : Action

data class OpenUrlAction(
    override val id: String,
    val url: String,
) : Action

data class Condition(
    val expression: String,
)

data class Rule(
    val id: String,
    val name: String,
    val trigger: Trigger,
    val conditions: List<Condition> = emptyList(),
    val actions: List<Action>,
)
