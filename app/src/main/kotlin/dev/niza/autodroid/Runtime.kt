package dev.niza.autodroid

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dev.niza.autodroid.engine.ActionResult
import dev.niza.autodroid.engine.AutomationEngine
import dev.niza.autodroid.engine.OpenUrlAction
import dev.niza.autodroid.engine.RuleStore
import dev.niza.autodroid.engine.SendDataSmsAction

object Runtime {
    private const val RULES_FILE = "rules.json"
    private const val ACTION_CHANNEL = "automation_actions"

    val engine = AutomationEngine { action ->
        runCatching { execute(action) }.fold(
            onSuccess = { ActionResult(action.id, success = true, message = it) },
            onFailure = { ActionResult(action.id, success = false, message = it.message) },
        )
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

    private fun execute(action: dev.niza.autodroid.engine.Action): String {
        return when (action) {
            is OpenUrlAction -> open(action)
            is SendDataSmsAction -> sendDataSms(action)
            else -> "accepted"
        }
    }

    private fun open(action: OpenUrlAction): String {
        val appContext = appContext ?: throw IllegalStateException("Application context unavailable")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(action.url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)
        return "requested"
    }

    private fun sendDataSms(action: SendDataSmsAction): String {
        val appContext = appContext ?: throw IllegalStateException("Application context unavailable")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = ContextCompat.getSystemService(appContext, SmsManager::class.java)
                ?: throw IllegalStateException("SmsManager unavailable")
            manager.sendDataMessage(action.sender, null, action.destinationPort.toShort(), action.payload, null, null)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault().sendDataMessage(action.sender, null, action.destinationPort.toShort(), action.payload, null, null)
        }
        return "sent"
    }

    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }
}
