package dev.niza.autodroid

import android.content.Context
import dev.niza.autodroid.engine.ActionResult
import dev.niza.autodroid.engine.AutomationEngine
import dev.niza.autodroid.engine.RuleStore

object Runtime {
    private const val RULES_FILE = "rules.json"

    val engine = AutomationEngine { action ->
        ActionResult(action.id, success = true, message = "accepted")
    }

    fun loadRules(context: Context) {
        val payload = context.openFileInput(RULES_FILE).use { input ->
            input.bufferedReader().readText()
        }
        loadRules(RuleStore().decode(payload))
    }

    fun loadRules(rules: List<dev.niza.autodroid.engine.Rule>) {
        rules.forEach(engine::register)
    }

    fun saveRules(context: Context) {
        context.openFileOutput(RULES_FILE, Context.MODE_PRIVATE).use { output ->
            output.write(RuleStore().encode(engine.rules()).toByteArray())
        }
    }
}
