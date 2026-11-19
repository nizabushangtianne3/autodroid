package dev.niza.autodroid.engine

interface TriggerEvent {
    fun matches(trigger: Trigger): Boolean
}

data class LinkOpenedEvent(
    val scheme: String?,
    val host: String?,
    val path: String?,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is LinkOpenedTrigger) return false
        if (trigger.scheme != null && trigger.scheme != scheme) return false
        if (trigger.host != null && trigger.host != host) return false
        if (trigger.pathPrefix != null && (path ?: "").startsWith(trigger.pathPrefix)) return true
        return trigger.pathPrefix == null
    }
}

data class DataSmsReceivedEvent(
    val destinationPort: Int,
    val sender: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is DataSmsReceivedTrigger) return false
        if (trigger.destinationPort != destinationPort) return false
        return trigger.sender == null || trigger.sender == sender
    }
}

data class AppLaunchedEvent(
    val packageName: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is AppLaunchedTrigger && trigger.packageName == packageName
    }
}

class ScheduleFiredEvent : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.ScheduleTrigger
}

data class ScreenOnOffEvent(
    val screenOn: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.ScreenOnOffTrigger && trigger.screenOn == screenOn
    }
}

data class BatteryLevelEvent(
    val level: Int,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.BatteryLevelTrigger) return false
        return if (trigger.direction == "above") {
            level >= trigger.level
        } else {
            level <= trigger.level
        }
    }
}

data class AirplaneModeEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.AirplaneModeTrigger && trigger.enabled == enabled
    }
}

data class BluetoothEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.BluetoothTrigger && trigger.enabled == enabled
    }
}

data class ExternalPowerEvent(
    val connected: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.ExternalPowerTrigger && trigger.connected == connected
    }
}

data class HeadphonesEvent(
    val connected: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.HeadphonesTrigger && trigger.connected == connected
    }
}

data class HotspotEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.HotspotTrigger && trigger.enabled == enabled
    }
}

data class SilentModeEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.SilentModeTrigger && trigger.enabled == enabled
    }
}

data class WifiConnectionEvent(
    val connected: Boolean,
    val ssid: String?,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger is dev.niza.autodroid.engine.WifiConnectionTrigger) {
            return trigger.connected == connected
        }
        if (trigger is dev.niza.autodroid.engine.WifiSSIDTrigger) {
            return connected && ssid == trigger.ssid
        }
        return false
    }
}

data class SignalOnOffEvent(
    val signalOn: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.SignalOnOffTrigger && trigger.signalOn == signalOn
    }
}

data class DockEvent(
    val state: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.DockTrigger && trigger.state == state
    }
}

data class VpnEvent(
    val connected: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.VpnTrigger && trigger.connected == connected
    }
}

data class GPSEnabledEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.GPSEnabledTrigger && trigger.enabled == enabled
    }
}

class AutomationEngine(
    private val executor: ActionExecutor,
) {
    private val rules = mutableListOf<Rule>()

    fun register(rule: Rule) {
        rules += rule
    }

    fun replace(rule: Rule) {
        val index = rules.indexOfFirst { it.id == rule.id }
        if (index >= 0) rules[index] = rule else rules += rule
    }

    fun remove(ruleId: String) {
        rules.removeAll { it.id == ruleId }
    }

    fun rules(): List<Rule> = rules.toList()

    fun dispatch(event: TriggerEvent): ExecutionReport {
        val matched = rules.filter { it.enabled && event.matches(it.trigger) }
        val results = matched.flatMap { rule -> rule.actions.map(executor::execute) }
        return ExecutionReport(matchedRuleCount = matched.size, actionResults = results)
    }
}

data class ExecutionReport(
    val matchedRuleCount: Int,
    val actionResults: List<ActionResult>,
) {
    val success: Boolean
        get() = actionResults.all { it.success }
}

fun interface ActionExecutor {
    fun execute(action: Action): ActionResult
}

data class ActionResult(
    val actionId: String,
    val success: Boolean,
    val message: String? = null,
)
