package dev.niza.autodroid.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpressionLogicTest {
    private fun engine(vararg vars: Pair<String, String>): AutomationEngine {
        val engine = AutomationEngine { ActionResult(it.id, true) }
        vars.forEach { (name, value) -> engine.setVariable(name, value) }
        return engine
    }

    @Test
    fun `or expression passes when any side is true`() {
        val engine = engine("a" to "1", "b" to "0")

        assertTrue(engine.evaluateExpression("{a} == 1 || {b} == 1"))
        assertTrue(engine.evaluateExpression("{a} == 2 || {b} == 0"))
        assertFalse(engine.evaluateExpression("{a} == 2 || {b} == 1"))
    }

    @Test
    fun `and expression passes only when both sides are true`() {
        val engine = engine("a" to "1", "b" to "1")

        assertTrue(engine.evaluateExpression("{a} == 1 && {b} == 1"))
        assertFalse(engine.evaluateExpression("{a} == 1 && {b} == 2"))
        assertFalse(engine.evaluateExpression("{a} == 2 && {b} == 1"))
    }

    @Test
    fun `or has lower precedence than and`() {
        val engine = engine("a" to "1", "b" to "1", "c" to "0")

        assertTrue(engine.evaluateExpression("{c} == 1 && {a} == 1 || {b} == 1"))
        assertFalse(engine.evaluateExpression("{c} == 0 && {a} == 1 || {b} == 0"))
    }

    @Test
    fun `execution log records dispatches and caps size`() {
        val executor = RecordingExecutor()
        val engine = AutomationEngine(executor)
        val rule =
            Rule(
                id = "r1",
                name = "log",
                trigger = ScheduleTrigger("t1", intervalMillis = 5000),
                actions = listOf(NotifyAction("a1", title = "T", body = "B")),
            )
        engine.register(rule)

        repeat(5) { engine.dispatch(ScheduleFiredEvent()) }

        assertEquals(5, engine.executionLog().size)
        assertTrue(engine.executionLog().all { it.report.matchedRuleCount == 1 })
    }

    private class RecordingExecutor : ActionExecutor {
        override fun execute(action: Action): ActionResult = ActionResult(action.id, success = true)
    }
}
