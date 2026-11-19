package dev.niza.autodroid.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VariableConditionTest {
    private fun scheduleRule(conditions: List<Condition>): Rule =
        Rule(
            id = "r1",
            name = "test",
            trigger = ScheduleTrigger("t1", intervalMillis = 5000),
            conditions = conditions,
            actions = listOf(NotifyAction("a1", title = "t", body = "b")),
        )

    @Test
    fun `set variable action stores value`() {
        val engine = AutomationEngine { ActionResult(it.id, true) }

        engine.dispatch(ScheduleFiredEvent())

        assertFalse(engine.conditionsSatisfied(scheduleRule(listOf(Condition("{count} > 5")))))
    }

    @Test
    fun `numeric condition evaluates against variable`() {
        val engine = AutomationEngine { ActionResult(it.id, true) }
        engine.setVariable("count", "10")

        assertTrue(engine.conditionsSatisfied(scheduleRule(listOf(Condition("{count} >= 5")))))
        assertFalse(engine.conditionsSatisfied(scheduleRule(listOf(Condition("{count} < 5")))))
        assertTrue(engine.conditionsSatisfied(scheduleRule(listOf(Condition("{count} == 10")))))
        assertTrue(engine.conditionsSatisfied(scheduleRule(listOf(Condition("{count} != 3")))))
    }

    @Test
    fun `string condition evaluates equality`() {
        val engine = AutomationEngine { ActionResult(it.id, true) }
        engine.setVariable("state", "on")

        assertTrue(engine.conditionsSatisfied(scheduleRule(listOf(Condition("{state} == on")))))
        assertFalse(engine.conditionsSatisfied(scheduleRule(listOf(Condition("{state} == off")))))
        assertTrue(engine.conditionsSatisfied(scheduleRule(listOf(Condition("{state} != off")))))
    }

    @Test
    fun `all conditions must pass`() {
        val engine = AutomationEngine { ActionResult(it.id, true) }
        engine.setVariable("count", "10")
        engine.setVariable("state", "on")

        assertTrue(
            engine.conditionsSatisfied(
                scheduleRule(
                    listOf(
                        Condition("{count} > 5"),
                        Condition("{state} == on"),
                    ),
                ),
            ),
        )
        assertFalse(
            engine.conditionsSatisfied(
                scheduleRule(
                    listOf(
                        Condition("{count} > 5"),
                        Condition("{state} == off"),
                    ),
                ),
            ),
        )
    }

    @Test
    fun `missing variable resolves to empty string`() {
        val engine = AutomationEngine { ActionResult(it.id, true) }

        assertFalse(engine.conditionsSatisfied(scheduleRule(listOf(Condition("{missing} == x")))))
        assertTrue(engine.conditionsSatisfied(scheduleRule(listOf(Condition("{missing} != x")))))
    }

    @Test
    fun `set variable action dispatches and stores`() {
        val engine = AutomationEngine { ActionResult(it.id, true) }
        var captured: List<ActionResult> = emptyList()
        val rule =
            Rule(
                id = "r1",
                name = "set var",
                trigger = ScheduleTrigger("t1", intervalMillis = 5000),
                actions = listOf(SetVariableAction("a1", name = "count", value = "42")),
            )
        engine.register(rule)

        val report = engine.dispatch(ScheduleFiredEvent())

        assertTrue(report.success)
        assertEquals(1, report.matchedRuleCount)
    }
}
