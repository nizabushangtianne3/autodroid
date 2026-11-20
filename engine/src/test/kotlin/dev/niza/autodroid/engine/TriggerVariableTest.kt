package dev.niza.autodroid.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TriggerVariableTest {
    private class RecordingExecutor : ActionExecutor {
        val executed = mutableListOf<String>()

        override fun execute(action: Action): ActionResult {
            executed += action.id
            return ActionResult(action.id, success = true)
        }
    }

    private fun scheduleRule(conditions: List<Condition> = emptyList()): Rule =
        Rule(
            id = "r1",
            name = "test",
            trigger = ScheduleTrigger("t1", intervalMillis = 5000),
            conditions = conditions,
            actions = listOf(NotifyAction("a1", title = "T", body = "B")),
        )

    @Test
    fun `app launch injects package variable`() {
        val engine = AutomationEngine(executor = { ActionResult(it.id, true) })
        engine.register(scheduleRule(listOf(Condition("{app_package} == com.example.app"))))

        engine.dispatch(AppLaunchedEvent("com.example.app"))

        assertEquals("com.example.app", engine.variable("app_package"))
    }

    @Test
    fun `battery event injects level variable`() {
        val engine = AutomationEngine(executor = { ActionResult(it.id, true) })

        engine.dispatch(BatteryLevelEvent(42))

        assertEquals("42", engine.variable("battery_level"))
    }

    @Test
    fun `link opened injects host variables`() {
        val engine = AutomationEngine(executor = { ActionResult(it.id, true) })

        engine.dispatch(LinkOpenedEvent("https", "example.com", "/page"))

        assertEquals("example.com", engine.variable("link_host"))
        assertEquals("https", engine.variable("link_scheme"))
        assertEquals("/page", engine.variable("link_path"))
    }

    @Test
    fun `sms event injects sender and port variables`() {
        val engine = AutomationEngine(executor = { ActionResult(it.id, true) })

        engine.dispatch(DataSmsReceivedEvent(destinationPort = 1234, sender = "+100"))

        assertEquals("+100", engine.variable("sms_sender"))
        assertEquals("1234", engine.variable("sms_port"))
    }

    @Test
    fun `condition uses injected trigger variable`() {
        val engine = AutomationEngine(executor = { ActionResult(it.id, true) })
        engine.register(
            Rule(
                id = "r1",
                name = "battery low",
                trigger = BatteryLevelTrigger("t1", level = 20, direction = "below"),
                conditions = listOf(Condition("{battery_level} <= 20")),
                actions = listOf(NotifyAction("a1", title = "Low", body = "Charge")),
            ),
        )

        val report = engine.dispatch(BatteryLevelEvent(15))

        assertEquals(1, report.matchedRuleCount)
        assertTrue(report.success)
    }
}
