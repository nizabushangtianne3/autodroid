package dev.niza.autodroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.niza.autodroid.engine.ScheduleFiredEvent

class ScheduleReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val ruleId = intent.getStringExtra(EXTRA_RULE_ID)
        Runtime.initialize(context)
        Runtime.loadRules(context)
        Runtime.dispatchAndReport(ScheduleFiredEvent(ruleId))
        ScheduleRegistrar.refresh(context)
    }

    companion object {
        const val EXTRA_RULE_ID = "rule_id"
    }
}
