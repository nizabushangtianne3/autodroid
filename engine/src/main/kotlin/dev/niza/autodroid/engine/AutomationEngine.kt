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

class AutomationEngine(
    private val executor: ActionExecutor,
) {
    private val rules = mutableListOf<Rule>()

    fun register(rule: Rule) {
        rules += rule
    }

    fun rules(): List<Rule> = rules.toList()

    fun dispatch(event: TriggerEvent): ExecutionReport {
        val matched = rules.filter { event.matches(it.trigger) }
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

interface ActionExecutor {
    fun execute(action: Action): ActionResult
}

data class ActionResult(
    val actionId: String,
    val success: Boolean,
    val message: String? = null,
)
