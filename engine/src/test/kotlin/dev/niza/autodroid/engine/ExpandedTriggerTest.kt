package dev.niza.autodroid.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpandedTriggerTest {
    private fun engine(): AutomationEngine = AutomationEngine(executor = { ActionResult(it.id, true) })

    private fun AutomationEngine.rule(trigger: Trigger): Rule =
        Rule(
            id = "r1",
            name = "t",
            trigger = trigger,
            actions = listOf(NotifyAction("a1", "T", "B")),
        )

    @Test
    fun `battery temperature respects direction`() {
        val engine = engine()
        engine.register(engine.rule(BatteryTemperatureTrigger("t1", temperatureCelsius = 40, direction = "above")))

        assertEquals(1, engine.dispatch(BatteryTemperatureEvent(45)).matchedRuleCount)
        assertEquals(1, engine.dispatch(BatteryTemperatureEvent(40)).matchedRuleCount)
        assertEquals(0, engine.dispatch(BatteryTemperatureEvent(35)).matchedRuleCount)
    }

    @Test
    fun `incoming sms matches sender and content filters`() {
        val engine = engine()
        engine.register(engine.rule(IncomingSMSTrigger("t1", sender = "+100", contentMatch = "code")))

        assertEquals(1, engine.dispatch(IncomingSMSEvent("+100", "your code is 42")).matchedRuleCount)
        assertEquals(0, engine.dispatch(IncomingSMSEvent("+200", "your code is 42")).matchedRuleCount)
        assertEquals("your code is 42", engine.variable("sms_message"))
        assertEquals(0, engine.dispatch(IncomingSMSEvent("+100", "nothing here")).matchedRuleCount)
    }

    @Test
    fun `incoming call matches only when number filter agrees`() {
        val engine = engine()
        engine.register(engine.rule(IncomingCallTrigger("t1", phoneNumber = "+100")))

        assertEquals(1, engine.dispatch(IncomingCallEvent("+100")).matchedRuleCount)
        assertEquals(0, engine.dispatch(IncomingCallEvent("+200")).matchedRuleCount)
        assertEquals("incoming", engine.variable("call_direction"))
    }

    @Test
    fun `geofence enter and exit are distinct`() {
        val engine = engine()
        engine.register(engine.rule(GeofenceTrigger("t1", name = "home", radiusMeters = 100, enter = true)))

        assertEquals(1, engine.dispatch(GeofenceEvent("home", inside = true)).matchedRuleCount)
        assertEquals(0, engine.dispatch(GeofenceEvent("home", inside = false)).matchedRuleCount)
        assertEquals("home", engine.variable("geofence_name"))
    }

    @Test
    fun `location trigger uses haversine radius`() {
        val engine = engine()
        engine.register(engine.rule(LocationTrigger("t1", latitude = 31.2304, longitude = 121.4737, radiusMeters = 1000)))

        assertEquals(1, engine.dispatch(LocationEvent(31.2304, 121.4747)).matchedRuleCount)
        assertEquals(0, engine.dispatch(LocationEvent(31.3000, 121.5000)).matchedRuleCount)
    }

    @Test
    fun `logcat trigger matches substring`() {
        val engine = engine()
        engine.register(engine.rule(LogcatTrigger("t1", pattern = "crash")))

        assertEquals(1, engine.dispatch(LogcatEvent("E/AndroidRuntime: crash in module")).matchedRuleCount)
        assertEquals(0, engine.dispatch(LogcatEvent("D/ok: running")).matchedRuleCount)
    }

    @Test
    fun `empty trigger fires immediately`() {
        val engine = engine()
        engine.register(engine.rule(EmptyTrigger("t1")))

        assertEquals(1, engine.dispatch(EmptyEvent()).matchedRuleCount)
    }

    @Test
    fun `variable trigger evaluates expression against injected value`() {
        val engine = engine()
        engine.register(engine.rule(VariableTrigger("t1", variableName = "level", expression = "{level} > 5")))

        assertEquals(1, engine.dispatch(VariableEvent("level", "9")).matchedRuleCount)
        assertEquals("9", engine.variable("level"))
        assertEquals(0, engine.dispatch(VariableEvent("level", "2")).matchedRuleCount)
    }

    @Test
    fun `ui click matches view id or text`() {
        val withId = engine()
        withId.register(withId.rule(UIClickTrigger("t1", viewId = "btn_ok")))
        assertEquals(1, withId.dispatch(UIClickEvent("btn_ok", "OK")).matchedRuleCount)
        assertEquals(0, withId.dispatch(UIClickEvent("btn_cancel", "OK")).matchedRuleCount)

        val withText = engine()
        withText.register(withText.rule(UIClickTrigger("t2", textMatch = "Send")))
        assertEquals(1, withText.dispatch(UIClickEvent("btn_1", "Send now")).matchedRuleCount)
        assertFalse(withText.dispatch(UIClickEvent("btn_1", "Nope")).matchedRuleCount == 1)
    }

    @Test
    fun `intent received filters by action`() {
        val engine = engine()
        engine.register(engine.rule(IntentReceivedTrigger("t1", action = "com.example.PING")))

        assertEquals(1, engine.dispatch(IntentReceivedEvent("com.example.PING")).matchedRuleCount)
        assertEquals(0, engine.dispatch(IntentReceivedEvent("com.example.PONG")).matchedRuleCount)
    }

    @Test
    fun `boot trigger fires once`() {
        val engine = engine()
        engine.register(engine.rule(BootTrigger("t1")))

        assertTrue(engine.dispatch(BootEvent()).matchedRuleCount == 1)
    }

    @Test
    fun `macro finished trigger targets one macro`() {
        val engine = engine()
        engine.register(engine.rule(MacroFinishedTrigger("t1", macroId = "macro-7")))

        assertEquals(1, engine.dispatch(MacroFinishedEvent("macro-7")).matchedRuleCount)
        assertEquals(0, engine.dispatch(MacroFinishedEvent("macro-8")).matchedRuleCount)
    }

    @Test
    fun `shake and flip are simple state toggles`() {
        val engine = engine()
        engine.register(engine.rule(ShakeDeviceTrigger("t1")))
        engine.register(engine.rule(FlipDeviceTrigger("t2", faceDown = true)))

        val report = engine.dispatch(ShakeDeviceEvent())
        assertEquals(1, report.matchedRuleCount)

        val flip = engine.dispatch(FlipDeviceEvent(faceDown = true))
        assertEquals(1, flip.matchedRuleCount)
    }
}
