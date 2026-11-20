package dev.niza.autodroid

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import dev.niza.autodroid.engine.RuleStore
import dev.niza.autodroid.engine.ScheduleTrigger

object ScheduleRegistrar {
    fun refresh(context: Context) {
        val appContext = context.applicationContext
        val alarmManager = ContextCompat.getSystemService(appContext, AlarmManager::class.java) ?: return
        val rules =
            runCatching {
                appContext.openFileInput("rules.json").use { input ->
                    input.bufferedReader().readText()
                }
            }.getOrNull()?.let { RuleStore().decode(it) } ?: emptyList()
        val schedules =
            rules.mapNotNull { rule ->
                val trigger = rule.trigger as? ScheduleTrigger
                if (rule.enabled && trigger != null) rule.id to trigger else null
            }
        val activeIds = schedules.map { it.first }.toSet()
        val existing =
            appContext.getSharedPreferences("schedule_registration", Context.MODE_PRIVATE)
                .getStringSet("rule_ids", emptySet()).orEmpty()
        (existing - activeIds).forEach { cancel(appContext, alarmManager, it) }
        schedules.forEach { (ruleId, trigger) -> schedule(appContext, alarmManager, ruleId, trigger.intervalMillis) }
        appContext.getSharedPreferences("schedule_registration", Context.MODE_PRIVATE)
            .edit()
            .putStringSet("rule_ids", activeIds)
            .apply()
    }

    private fun schedule(
        context: Context,
        alarmManager: AlarmManager,
        ruleId: String,
        intervalMillis: Long,
    ) {
        val pendingIntent = operation(context, ruleId)
        val triggerAt = System.currentTimeMillis() + intervalMillis
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    private fun cancel(
        context: Context,
        alarmManager: AlarmManager,
        ruleId: String,
    ) {
        alarmManager.cancel(operation(context, ruleId))
    }

    private fun operation(
        context: Context,
        ruleId: String,
    ): PendingIntent {
        val intent =
            Intent(context, ScheduleReceiver::class.java)
                .putExtra(ScheduleReceiver.EXTRA_RULE_ID, ruleId)
        return PendingIntent.getBroadcast(
            context,
            ruleId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
