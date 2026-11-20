package dev.niza.autodroid

import dev.niza.autodroid.engine.Rule
import dev.niza.autodroid.engine.summary
import dev.niza.autodroid.engine.value
import dev.niza.autodroid.ui.RuleEditorLogic

data class MacroUiState(
    val id: String,
    val name: String,
    val enabled: Boolean,
    val lastRun: String,
    val triggerLabel: String,
    val actionLabel: String,
    val constraintLabel: String,
)

fun Rule.toUiState() =
    MacroUiState(
        id = id,
        name = name,
        enabled = enabled,
        lastRun = if (enabled) "已启用" else "已禁用",
        triggerLabel = RuleEditorLogic.triggerLabel(trigger) + (trigger.value?.let { " · $it" } ?: ""),
        actionLabel =
            RuleEditorLogic.actionLabel(actions.firstOrNull()) +
                (actions.firstOrNull()?.summary?.let { " · $it" } ?: ""),
        constraintLabel = if (constraints.isEmpty()) "无约束" else "约束 · ${constraints.size}",
    )
