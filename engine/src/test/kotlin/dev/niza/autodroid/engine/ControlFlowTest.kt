package dev.niza.autodroid.engine

import kotlin.test.Test
import kotlin.test.assertEquals

class ControlFlowTest {
    private class RecordingExecutor : ActionExecutor {
        val executed = mutableListOf<String>()

        override fun execute(action: Action): ActionResult {
            executed += action.id
            return ActionResult(action.id, success = true)
        }
    }

    private fun scheduleRule(actions: List<Action>): Rule =
        Rule(
            id = "r1",
            name = "control flow",
            trigger = ScheduleTrigger("t1", intervalMillis = 5000),
            actions = actions,
        )

    @Test
    fun `if true branch executes when condition passes`() {
        val executor = RecordingExecutor()
        val engine = AutomationEngine(executor)
        engine.setVariable("state", "on")
        engine.register(
            scheduleRule(
                listOf(
                    IfConditionAction("if1", "{state} == on"),
                    NotifyAction("a1", "T", "B"),
                    EndIfAction("end1"),
                ),
            ),
        )

        engine.dispatch(ScheduleFiredEvent())

        assertEquals(listOf("a1"), executor.executed)
    }

    @Test
    fun `if true branch skipped and else executes when condition fails`() {
        val executor = RecordingExecutor()
        val engine = AutomationEngine(executor)
        engine.setVariable("state", "off")
        engine.register(
            scheduleRule(
                listOf(
                    IfConditionAction("if1", "{state} == on"),
                    NotifyAction("a1", "T", "true branch"),
                    ElseConditionAction("else1"),
                    NotifyAction("a2", "T", "false branch"),
                    EndIfAction("end1"),
                ),
            ),
        )

        engine.dispatch(ScheduleFiredEvent())

        assertEquals(listOf("a2"), executor.executed)
    }

    @Test
    fun `actions outside if always execute`() {
        val executor = RecordingExecutor()
        val engine = AutomationEngine(executor)
        engine.setVariable("state", "off")
        engine.register(
            scheduleRule(
                listOf(
                    NotifyAction("before", "T", "B"),
                    IfConditionAction("if1", "{state} == on"),
                    NotifyAction("inside", "T", "B"),
                    EndIfAction("end1"),
                    NotifyAction("after", "T", "B"),
                ),
            ),
        )

        engine.dispatch(ScheduleFiredEvent())

        assertEquals(listOf("before", "after"), executor.executed)
    }

    @Test
    fun `wait action executes like normal action`() {
        val executor = RecordingExecutor()
        val engine = AutomationEngine(executor)
        engine.register(scheduleRule(listOf(WaitAction("w1", durationMillis = 100))))

        engine.dispatch(ScheduleFiredEvent())

        assertEquals(listOf("w1"), executor.executed)
    }
}
