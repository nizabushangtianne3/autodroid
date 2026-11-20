package dev.niza.autodroid.engine

interface TriggerEvent {
    fun matches(trigger: Trigger): Boolean

    fun injectTriggerVariables(engine: AutomationEngine) {}
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

    override fun injectTriggerVariables(engine: AutomationEngine) {
        host?.let { engine.setVariable("link_host", it) }
        scheme?.let { engine.setVariable("link_scheme", it) }
        path?.let { engine.setVariable("link_path", it) }
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

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("sms_sender", sender)
        engine.setVariable("sms_port", destinationPort.toString())
    }
}

data class AppLaunchedEvent(
    val packageName: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is AppLaunchedTrigger && trigger.packageName == packageName
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("app_package", packageName)
    }
}

data class ScheduleFiredEvent(
    val ruleId: String? = null,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.ScheduleTrigger

    override fun injectTriggerVariables(engine: AutomationEngine) {
        ruleId?.let { engine.setVariable("schedule_rule_id", it) }
    }
}

data class ScreenOnOffEvent(
    val screenOn: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.ScreenOnOffTrigger && trigger.screenOn == screenOn
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("screen_on", screenOn.toString())
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

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("battery_level", level.toString())
    }
}

data class AirplaneModeEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.AirplaneModeTrigger && trigger.enabled == enabled
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("airplane_mode", enabled.toString())
    }
}

data class BluetoothEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.BluetoothTrigger && trigger.enabled == enabled
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("bluetooth_on", enabled.toString())
    }
}

data class ExternalPowerEvent(
    val connected: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.ExternalPowerTrigger && trigger.connected == connected
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("power_connected", connected.toString())
    }
}

data class HeadphonesEvent(
    val connected: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.HeadphonesTrigger && trigger.connected == connected
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("headphones_connected", connected.toString())
    }
}

data class HotspotEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.HotspotTrigger && trigger.enabled == enabled
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("hotspot_on", enabled.toString())
    }
}

data class SilentModeEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.SilentModeTrigger && trigger.enabled == enabled
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("silent_mode", enabled.toString())
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

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("wifi_connected", connected.toString())
        ssid?.let { engine.setVariable("wifi_ssid", it) }
    }
}

data class SignalOnOffEvent(
    val signalOn: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.SignalOnOffTrigger && trigger.signalOn == signalOn
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("signal_on", signalOn.toString())
    }
}

data class DockEvent(
    val state: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.DockTrigger && trigger.state == state
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("dock_state", state)
    }
}

data class VpnEvent(
    val connected: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.VpnTrigger && trigger.connected == connected
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("vpn_connected", connected.toString())
    }
}

data class GPSEnabledEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.GPSEnabledTrigger && trigger.enabled == enabled
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("gps_enabled", enabled.toString())
    }
}

class DeviceUnlockedEvent : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.DeviceUnlockedTrigger
}

data class ClipboardChangeEvent(
    val content: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.ClipboardChangeTrigger
}

data class DarkThemeEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.DarkThemeTrigger && trigger.enabled == enabled
    }
}

data class MediaButtonEvent(
    val button: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.MediaButtonTrigger && trigger.button == button
    }
}

data class NotificationEvent(
    val packageName: String,
    val title: String,
    val content: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.NotificationTrigger) return false
        return trigger.packageName == null || trigger.packageName == packageName
    }
}

class AutomationEngine(
    private val executor: ActionExecutor,
) {
    private val rules = mutableListOf<Rule>()
    private val variables = mutableMapOf<String, String>()
    private val executionLog = mutableListOf<ExecutionLogEntry>()

    private companion object {
        const val MAX_LOG_ENTRIES = 100
    }

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

    fun setVariable(
        name: String,
        value: String,
    ) {
        variables[name] = value
    }

    fun variable(name: String): String? = variables[name]

    fun variables(): Map<String, String> = variables.toMap()

    fun executionLog(): List<ExecutionLogEntry> = executionLog.toList()

    fun conditionsSatisfied(rule: Rule): Boolean =
        rule.conditions.all { condition ->
            evaluateExpression(condition.expression)
        }

    fun constraintsSatisfied(
        rule: Rule,
        deviceState: DeviceState? = null,
    ): Boolean =
        rule.constraints.all { constraint ->
            constraint.satisfied(deviceState ?: DeviceState(), variables())
        }

    fun evaluateExpression(expression: String): Boolean {
        val resolved = variables.entries.fold(expression) { text, (name, value) -> text.replace("{$name}", value) }
        val orParts = resolved.split("||")
        if (orParts.size > 1) {
            return orParts.any { evaluateSingleExpression(it.trim()) }
        }
        val andParts = resolved.split("&&")
        if (andParts.size > 1) {
            return andParts.all { evaluateSingleExpression(it.trim()) }
        }
        return evaluateSingleExpression(resolved)
    }

    private fun evaluateSingleExpression(resolved: String): Boolean {
        val comparisons = listOf("==", "!=", ">=", "<=", ">", "<")
        for (operator in comparisons) {
            val index = resolved.indexOf(operator)
            if (index > 0) {
                val left = resolved.substring(0, index).trim()
                val right = resolved.substring(index + operator.length).trim()
                return compareValues(left, operator, right)
            }
        }
        return resolved.toBooleanStrictOrNull() ?: false
    }

    private fun compareValues(
        left: String,
        operator: String,
        right: String,
    ): Boolean {
        val leftNumber = left.toDoubleOrNull()
        val rightNumber = right.toDoubleOrNull()
        if (leftNumber != null && rightNumber != null) {
            return when (operator) {
                "==" -> leftNumber == rightNumber
                "!=" -> leftNumber != rightNumber
                ">=" -> leftNumber >= rightNumber
                "<=" -> leftNumber <= rightNumber
                ">" -> leftNumber > rightNumber
                "<" -> leftNumber < rightNumber
                else -> false
            }
        }
        return when (operator) {
            "==" -> left == right
            "!=" -> left != right
            else -> false
        }
    }

    fun dispatch(event: TriggerEvent): ExecutionReport {
        event.injectTriggerVariables(this)
        val matched =
            rules.filter {
                it.enabled && event.matches(it.trigger) && conditionsSatisfied(it) && constraintsSatisfied(it)
            }
        val results = matched.flatMap { rule -> executeActionList(rule.actions) }
        val report = ExecutionReport(matchedRuleCount = matched.size, actionResults = results)
        executionLog += ExecutionLogEntry(timestamp = System.currentTimeMillis(), report = report)
        if (executionLog.size > MAX_LOG_ENTRIES) {
            executionLog.removeAt(0)
        }
        return report
    }

    private fun executeActionList(actions: List<Action>): List<ActionResult> {
        val results = mutableListOf<ActionResult>()
        var insideIf = false
        var inElseBranch = false
        var conditionPassed = false
        var insideRepeat = false
        var repeatRemaining = 0
        var repeatIndex = 0
        val repeatActions = mutableListOf<Action>()

        for (action in actions) {
            when (action) {
                is dev.niza.autodroid.engine.RepeatStartAction -> {
                    insideRepeat = true
                    repeatRemaining = action.count
                    repeatIndex = 0
                    repeatActions.clear()
                }
                is dev.niza.autodroid.engine.RepeatEndAction -> {
                    if (insideRepeat) {
                        repeat(repeatRemaining) {
                            repeatIndex++
                            repeatActions.forEach { inner ->
                                when (inner) {
                                    is dev.niza.autodroid.engine.SetVariableAction ->
                                        setVariable(
                                            inner.name,
                                            inner.value.replace("{loop_index}", repeatIndex.toString()),
                                        )
                                    else -> results += executor.execute(inner)
                                }
                            }
                        }
                    }
                    insideRepeat = false
                    repeatActions.clear()
                }
                is dev.niza.autodroid.engine.IfConditionAction -> {
                    insideIf = true
                    inElseBranch = false
                    conditionPassed = evaluateExpression(action.expression)
                }
                is dev.niza.autodroid.engine.ElseConditionAction -> {
                    inElseBranch = insideIf && !conditionPassed
                }
                is dev.niza.autodroid.engine.EndIfAction -> {
                    insideIf = false
                    inElseBranch = false
                    conditionPassed = false
                }
                else -> {
                    val shouldExecute = !insideIf || conditionPassed || inElseBranch
                    if (shouldExecute && !insideRepeat) {
                        results += executor.execute(action)
                    } else if (shouldExecute && insideRepeat) {
                        repeatActions += action
                    }
                }
            }
        }
        return results
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

data class ExecutionLogEntry(
    val timestamp: Long,
    val report: ExecutionReport,
)
