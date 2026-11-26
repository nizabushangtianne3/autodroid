package dev.niza.autodroid.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GlobalVariableTest {
    private fun engine(): AutomationEngine = AutomationEngine(executor = { ActionResult(it.id, true) })

    @Test
    fun `scalar global variable resolves in conditions`() {
        val engine = engine()
        engine.setGlobalVariable(name = "count", type = VariableType.NUMBER, value = "12")

        assertEquals("12", engine.variable("count"))
        assertEquals(true, engine.evaluateExpression("{count} > 10"))
        assertEquals(false, engine.evaluateExpression("{count} < 10"))
        assertEquals("12", engine.variables()["count"])
    }

    @Test
    fun `dictionary entry resolves with bracket selector`() {
        val engine = engine()
        engine.setDictionaryEntry(name = "settings", key = "mode", value = "night")

        assertEquals("night", engine.dictionaryEntry("settings", "mode"))
        assertEquals("night", engine.variable("settings[mode]"))
        assertEquals(true, engine.evaluateExpression("{settings[mode]} == night"))
        assertEquals("night", engine.variables()["settings[mode]"])
    }

    @Test
    fun `removing dictionary entry drops the selector`() {
        val engine = engine()
        engine.setDictionaryEntry("settings", "mode", "night")
        engine.clearDictionaryEntry("settings", "mode")

        assertNull(engine.dictionaryEntry("settings", "mode"))
        assertNull(engine.variable("settings[mode]"))
    }

    @Test
    fun `array entry resolves by index selector`() {
        val engine = engine()
        engine.setArrayEntry("queue", 0, "first")
        engine.setArrayEntry("queue", 1, "second")

        assertEquals("first", engine.variable("queue[0]"))
        assertEquals("second", engine.variable("queue[1]"))
        assertEquals(2, engine.globalVariable("queue")?.entries?.size)
    }

    @Test
    fun `iterate dictionary action loops over entries in order`() {
        val engine = engine()
        engine.setDictionaryEntry("settings", "one", "1")
        engine.setDictionaryEntry("settings", "two", "2")
        engine.setGlobalVariable("seen", VariableType.STRING, "")

        engine.register(
            Rule(
                id = "r1",
                name = "iterate",
                trigger = ScheduleTrigger("t1", intervalMillis = 1000),
                actions =
                    listOf(
                        IterateDictionaryAction("i1", "settings", "dict_key", "dict_value"),
                        SetVariableAction("v1", "seen", "{seen}{dict_key}={dict_value};"),
                        IterateDictionaryEndAction("i2", "settings"),
                    ),
            ),
        )

        val report = engine.dispatch(ScheduleFiredEvent())

        assertEquals(1, report.matchedRuleCount)
        assertEquals("one=1;two=2;", engine.variable("seen"))
        assertEquals("two", engine.variable("dict_key"))
        assertEquals("2", engine.variable("dict_value"))
    }

    @Test
    fun `set dictionary entry action writes resolved values`() {
        val engine = engine()
        engine.setVariable("target", "language")
        engine.setGlobalVariable("settings", VariableType.DICTIONARY, "")

        engine.register(
            Rule(
                id = "r1",
                name = "write",
                trigger = ScheduleTrigger("t1", intervalMillis = 1000),
                actions =
                    listOf(
                        SetDictionaryEntryAction("d1", "settings", "{target}", "zh-CN"),
                    ),
            ),
        )

        engine.dispatch(ScheduleFiredEvent())

        assertEquals("zh-CN", engine.dictionaryEntry("settings", "language"))
    }

    @Test
    fun `entries survive encode and decode round trip`() {
        val variable = GlobalVariable(name = "map", type = VariableType.DICTIONARY, entries = decodeEntries("a=1|b=2"))

        assertEquals(listOf(VariableEntry("a", "1"), VariableEntry("b", "2")), variable.entries)
        assertEquals("a=1|b=2", variable.encodeEntries())
    }

    @Test
    fun `clear variables action removes local variables`() {
        val engine = engine()
        engine.setVariable("temp", "x")
        engine.register(
            Rule(
                id = "r1",
                name = "clear",
                trigger = ScheduleTrigger("t1", intervalMillis = 1000),
                actions = listOf(ClearVariablesAction("c1")),
            ),
        )

        engine.dispatch(ScheduleFiredEvent())

        assertNull(engine.variable("temp"))
    }

    @Test
    fun `global variables list is sorted by name`() {
        val engine = engine()
        engine.setGlobalVariable("zeta", VariableType.STRING, "z")
        engine.setGlobalVariable("alpha", VariableType.STRING, "a")

        assertEquals(listOf("alpha", "zeta"), engine.globalVariables().map { it.name })
    }
}
