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
}
