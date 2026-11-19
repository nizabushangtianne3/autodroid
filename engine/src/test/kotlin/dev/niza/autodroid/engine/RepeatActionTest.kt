package dev.niza.autodroid.engine

import kotlin.test.Test
import kotlin.test.assertEquals

class RepeatActionTest {
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
            name = "repeat",
            trigger = ScheduleTrigger("t1", intervalMillis = 5000),
            actions = actions,
        )

    @Test
    fun `repeat block executes actions count times`() {
        val executor = RecordingExecutor()
        val engine = AutomationEngine(executor)
        engine.register(
            scheduleRule(
                listOf(
                    RepeatStartAction("rs", count = 3),
                    NotifyAction("a1", title = "T", body = "B"),
                    RepeatEndAction("re"),
                ),
            ),
        )

        engine.dispatch(ScheduleFiredEvent())

        assertEquals(listOf("a1", "a1", "a1"), executor.executed)
    }

    @Test
    fun `repeat with set variable uses loop index`() {
        val engine = AutomationEngine { ActionResult(it.id, true) }
        engine.register(
            scheduleRule(
                listOf(
                    RepeatStartAction("rs", count = 3),
                    SetVariableAction("sv", name = "index", value = "{loop_index}"),
                    RepeatEndAction("re"),
                ),
            ),
        )

        engine.dispatch(ScheduleFiredEvent())

        assertEquals("3", engine.variable("index"))
    }

    @Test
    fun `actions outside repeat execute once`() {
        val executor = RecordingExecutor()
        val engine = AutomationEngine(executor)
        engine.register(
            scheduleRule(
                listOf(
                    NotifyAction("before", title = "T", body = "B"),
                    RepeatStartAction("rs", count = 2),
                    NotifyAction("inside", title = "T", body = "B"),
                    RepeatEndAction("re"),
                    NotifyAction("after", title = "T", body = "B"),
                ),
            ),
        )

        engine.dispatch(ScheduleFiredEvent())

        assertEquals(listOf("before", "inside", "inside", "after"), executor.executed)
    }

    @Test
    fun `zero count executes nothing`() {
        val executor = RecordingExecutor()
        val engine = AutomationEngine(executor)
        engine.register(
            scheduleRule(
                listOf(
                    RepeatStartAction("rs", count = 0),
                    NotifyAction("inside", title = "T", body = "B"),
                    RepeatEndAction("re"),
                ),
            ),
        )

        engine.dispatch(ScheduleFiredEvent())

        assertEquals(emptyList(), executor.executed)
    }
}
