package dev.niza.autodroid.engine

import kotlinx.serialization.Serializable

/**
 * Persisted global variable. [value] holds the scalar text for string/number/boolean types,
 * and the serialized entry list for [VariableType.DICTIONARY] and [VariableType.ARRAY].
 */
@Serializable
data class GlobalVariable(
    val name: String,
    val type: VariableType = VariableType.STRING,
    val value: String = "",
    val entries: List<VariableEntry> = emptyList(),
)

@Serializable
enum class VariableType {
    STRING,
    NUMBER,
    BOOLEAN,
    DICTIONARY,
    ARRAY,
}

@Serializable
data class VariableEntry(
    val key: String = "",
    val value: String = "",
)

/** Escapes a literal so it can be used as a dictionary key or array index inside an expression. */
fun GlobalVariable.lookup(path: String): String? {
    val selector = parseSelector(path, name) ?: return value
    return when (this.type) {
        VariableType.DICTIONARY, VariableType.ARRAY -> entries.firstOrNull { it.key == selector }?.value
        else -> value
    }
}

internal fun parseSelector(
    expression: String,
    name: String,
): String? {
    val pattern = "\\{$name\\[([^]]*)\\]\\}"
    return Regex(pattern).find(expression)?.groupValues?.get(1)
}

internal fun GlobalVariable.encodeEntries(): String = entries.joinToString("|") { "${it.key.escapeEntry()}=${it.value.escapeEntry()}" }

internal fun decodeEntries(payload: String): List<VariableEntry> =
    if (payload.isBlank()) {
        emptyList()
    } else {
        payload.split('|').mapNotNull { segment ->
            if (segment.isBlank()) {
                null
            } else {
                val index = segment.indexOf('=')
                if (index <= 0) {
                    null
                } else {
                    VariableEntry(
                        key = segment.substring(0, index).unescapeEntry(),
                        value = segment.substring(index + 1).unescapeEntry(),
                    )
                }
            }
        }
    }

private fun String.escapeEntry(): String = replace("\\", "\\\\").replace("|", "\\p").replace("=", "\\e")

private fun String.unescapeEntry(): String = replace("\\e", "=").replace("\\p", "|").replace("\\\\", "\\")
