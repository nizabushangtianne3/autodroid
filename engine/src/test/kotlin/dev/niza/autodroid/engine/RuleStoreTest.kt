package dev.niza.autodroid.engine

import kotlin.test.Test
import kotlin.test.assertEquals

class RuleStoreTest {
    @Test
    fun `encode and decode preserves rules`() {
        val store = RuleStore()
        val rules =
            listOf(
                Rule(
                    id = "r1",
                    name = "Sample",
                    trigger = DataSmsReceivedTrigger("t1", destinationPort = 1234, sender = "+100"),
                    actions = listOf(OpenUrlAction("a1", "https://example.com")),
                ),
            )

        assertEquals(rules, store.decode(store.encode(rules)))
    }

    @Test
    fun `decode invalid payload returns empty list`() {
        val store = RuleStore()

        assertEquals(emptyList(), store.decode("{"))
        assertEquals(emptyList(), store.decode("[]"))
    }

    @Test
    fun `decode unknown trigger type returns empty list`() {
        val store = RuleStore()
        val payload =
            """
            [
              {
                "id": "r1",
                "name": "Sample",
                "trigger": {
                  "type": "unknown",
                  "id": "t1"
                },
                "actions": []
              }
            ]
            """.trimIndent()

        assertEquals(emptyList(), store.decode(payload))
    }
}
