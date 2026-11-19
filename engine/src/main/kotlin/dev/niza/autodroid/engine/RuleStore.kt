package dev.niza.autodroid.engine

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RuleStore {
    private val json = Json { ignoreUnknownKeys = true }

    fun encode(rules: List<Rule>): String = json.encodeToString(rules)

    fun decode(payload: String): List<Rule> =
        runCatching { json.decodeFromString<List<Rule>>(payload) }.getOrDefault(emptyList())
}
