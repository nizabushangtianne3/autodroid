package dev.niza.autodroid

import dev.niza.autodroid.engine.ActionResult
import dev.niza.autodroid.engine.AutomationEngine

object Runtime {
    val engine = AutomationEngine { action ->
        ActionResult(action.id, success = true, message = "accepted")
    }
}
