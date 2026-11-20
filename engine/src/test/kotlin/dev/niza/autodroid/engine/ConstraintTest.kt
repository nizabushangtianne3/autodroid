package dev.niza.autodroid.engine

import java.util.Calendar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConstraintTest {
    private val state =
        DeviceState(
            batteryLevel = 72,
            batterySaver = true,
            bluetoothEnabled = true,
            cellConnectionType = "5g",
            calendar = Calendar.getInstance().apply { set(2026, 10, 20, 18, 30) },
            deviceLocked = false,
            externalPowerConnected = true,
            gpsEnabled = true,
            headphonesConnected = false,
            networkType = "wifi",
            screenOn = true,
            silentModeEnabled = false,
            enabledTriggerIds = setOf("timer"),
            wifiConnected = true,
            airplaneModeEnabled = false,
        )
    private val variables = mapOf("count" to "10")

    @Test
    fun `device constraints compare state`() {
        assertTrue(BatteryLevelConstraint("c", level = 50).satisfied(state, variables))
        assertFalse(BatteryLevelConstraint("c", level = 50, direction = "below").satisfied(state, variables))
        assertTrue(BatterySaverConstraint("c", enabled = true).satisfied(state, variables))
        assertTrue(BluetoothConstraint("c", enabled = true).satisfied(state, variables))
        assertTrue(CellConnectionConstraint("c", "5g").satisfied(state, variables))
        assertTrue(DeviceLockedConstraint("c", locked = false).satisfied(state, variables))
        assertTrue(ExternalPowerConstraint("c", connected = true).satisfied(state, variables))
        assertTrue(GPSEnabledConstraint("c", enabled = true).satisfied(state, variables))
        assertTrue(HeadphonesConstraint("c", connected = false).satisfied(state, variables))
        assertTrue(NetworkTypeConstraint("c", "wifi").satisfied(state, variables))
        assertTrue(ScreenOnOffConstraint("c", screenOn = true).satisfied(state, variables))
        assertTrue(SilentModeConstraint("c", enabled = false).satisfied(state, variables))
        assertTrue(TriggerEnabledConstraint("c", "timer").satisfied(state, variables))
        assertTrue(WifiConstraint("c", connected = true).satisfied(state, variables))
        assertTrue(AirplaneModeConstraint("c", enabled = false).satisfied(state, variables))
    }

    @Test
    fun `day time constraint supports range and days`() {
        val range = DayTimeConstraint("c", startMinuteOfDay = 18 * 60, endMinuteOfDay = 19 * 60)
        assertTrue(range.satisfied(state, variables))
        assertFalse(range.copy(days = listOf(1)).satisfied(state, variables))
    }

    @Test
    fun `variable constraint supports logic and comparison`() {
        assertTrue(VariableConstraint("c", "{count} > 5").satisfied(state, variables))
        assertTrue(VariableConstraint("c", "{count} < 5 || {count} == 10").satisfied(state, variables))
        assertFalse(VariableConstraint("c", "{count} < 5 && {count} == 10").satisfied(state, variables))
    }

    @Test
    fun `rule constraint gate blocks dispatch`() {
        val executor = RecordingExecutor()
        val engine = AutomationEngine(executor)
        val rule =
            Rule(
                id = "r1",
                name = "gated",
                trigger = ScheduleTrigger("t1", intervalMillis = 5000),
                constraints = listOf(WifiConstraint("c", connected = true)),
                actions = listOf(NotifyAction("a1", title = "T", body = "B")),
            )
        engine.register(rule)

        val report = engine.dispatch(ScheduleFiredEvent())

        assertEquals(0, report.matchedRuleCount)
        assertEquals(0, executor.executed.size)
    }

    private class RecordingExecutor : ActionExecutor {
        val executed = mutableListOf<String>()

        override fun execute(action: Action): ActionResult {
            executed += action.id
            return ActionResult(action.id, success = true)
        }
    }
}
