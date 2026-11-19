package dev.niza.autodroid.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AutomationEngineTest {
    private class RecordingExecutor : ActionExecutor {
        val executed = mutableListOf<Action>()

        override fun execute(action: Action): ActionResult {
            executed += action
            return ActionResult(action.id, success = true)
        }
    }

    @Test
    fun `dispatch runs matching link opened rules`() {
        val executor = RecordingExecutor()
        val engine = AutomationEngine(executor)
        val action = NotifyAction("notify", "Title", "Body")
        engine.register(
            Rule(
                id = "r1",
                name = "tracking link",
                trigger = LinkOpenedTrigger("t1", host = "example.com", pathPrefix = "/x"),
                actions = listOf(action),
            ),
        )
        engine.register(
            Rule(
                id = "r2",
                name = "other host",
                trigger = LinkOpenedTrigger("t2", host = "other.com"),
                actions = listOf(action),
            ),
        )

        val report = engine.dispatch(LinkOpenedEvent("https", "example.com", "/x/test"))

        assertEquals(1, report.matchedRuleCount)
        assertEquals(listOf<Action>(action), executor.executed)
        assertTrue(report.success)
    }

    @Test
    fun `dispatch filters data sms by port and sender`() {
        val executor = RecordingExecutor()
        val engine = AutomationEngine(executor)
        engine.register(
            Rule(
                id = "r1",
                name = "sms",
                trigger = DataSmsReceivedTrigger("t1", destinationPort = 1234, sender = "+100"),
                actions = listOf(NotifyAction("a1", "SMS", "received")),
            ),
        )

        assertEquals(0, engine.dispatch(DataSmsReceivedEvent(4321, "+100")).matchedRuleCount)
        assertEquals(0, engine.dispatch(DataSmsReceivedEvent(1234, "+200")).matchedRuleCount)
        assertEquals(1, engine.dispatch(DataSmsReceivedEvent(1234, "+100")).matchedRuleCount)
    }

    @Test
    fun `replace updates existing rule and remove deletes it`() {
        val engine = AutomationEngine { action -> ActionResult(action.id, true) }
        engine.register(
            Rule(
                id = "r1",
                name = "old",
                trigger = LinkOpenedTrigger("t1", host = "example.com"),
                actions = listOf(NotifyAction("a1", "Old", "Old")),
            ),
        )

        engine.replace(
            Rule(
                id = "r1",
                name = "new",
                trigger = LinkOpenedTrigger("t1", host = "example.com"),
                actions = listOf(NotifyAction("a2", "New", "New")),
            ),
        )

        assertEquals(1, engine.rules().size)
        assertEquals("new", engine.rules().single().name)

        engine.remove("r1")

        assertEquals(0, engine.rules().size)
    }

    @Test
    fun `disabled rules do not dispatch actions`() {
        val executor = RecordingExecutor()
        val engine = AutomationEngine(executor)
        engine.register(
            Rule(
                id = "r1",
                name = "disabled",
                trigger = AppLaunchedTrigger("t1", packageName = "example.app"),
                actions = listOf(NotifyAction("a1", "Title", "Body")),
                enabled = false,
            ),
        )

        val report = engine.dispatch(AppLaunchedEvent("example.app"))

        assertEquals(0, report.matchedRuleCount)
        assertEquals(0, executor.executed.size)
    }
}
