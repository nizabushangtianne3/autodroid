package dev.niza.autodroid.ui

object RuleEditorLogic {
    const val TYPE_LINK = 0
    const val TYPE_APP = 1
    const val TYPE_DATA_SMS = 2

    const val ACTION_NOTIFY = 0
    const val ACTION_OPEN_URL = 1
    const val ACTION_SEND_DATA_SMS = 2

    fun triggerHint(typePosition: Int): String =
        when (typePosition) {
            TYPE_APP -> "App package name"
            TYPE_DATA_SMS -> "Data SMS destination port"
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
            else -> {
                val port = value.toIntOrNull()
                if (port == null || port !in 1..65535) "Data SMS port must be 1-65535" else null
            }
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
}
