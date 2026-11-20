package dev.niza.autodroid

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dev.niza.autodroid.engine.ActionResult
import dev.niza.autodroid.engine.AutomationEngine
import dev.niza.autodroid.engine.ClipboardAction
import dev.niza.autodroid.engine.NotifyAction
import dev.niza.autodroid.engine.LaunchAppAction
import dev.niza.autodroid.engine.OpenUrlAction
import dev.niza.autodroid.engine.RuleStore
import dev.niza.autodroid.engine.SendDataSmsAction

object Runtime {
    private const val RULES_FILE = "rules.json"
    private const val ACTION_CHANNEL = "automation_actions"
    const val EXTRA_RULE_ID = "rule_id"

    private val variables = mutableMapOf<String, String>()

    internal var deviceStateProvider: ((Context) -> dev.niza.autodroid.engine.DeviceState)? = null

    val engine: AutomationEngine =
        AutomationEngine(
            executor = { action ->
                when (action) {
                    is dev.niza.autodroid.engine.SetVariableAction -> {
                        variables[action.name] = action.value
                        ActionResult(action.id, success = true, message = "variable set")
                    }
                    else ->
                        runCatching { execute(action) }.fold(
                            onSuccess = { ActionResult(action.id, success = true, message = it) },
                            onFailure = { ActionResult(action.id, success = false, message = it.message) },
                        )
                }
            },
        )

    fun dispatchAndReport(event: dev.niza.autodroid.engine.TriggerEvent) {
        val state = deviceStateProvider?.invoke(appContext ?: return)
        engine.deviceState = state
        val report = engine.dispatch(event)
        reportExecution(report)
    }

    private fun reportExecution(report: dev.niza.autodroid.engine.ExecutionReport) {
        val appContext = appContext ?: return
        if (report.matchedRuleCount == 0) return
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val manager = NotificationManagerCompat.from(appContext)
        val title = if (report.success) "宏执行成功" else "宏执行失败"
        val text =
            buildString {
                append("匹配宏：${report.matchedRuleCount}")
                report.actionResults.forEach { result ->
                    append('\n')
                    append("${result.actionId}：${if (result.success) "成功" else "失败"}")
                    result.message?.let { append(" • $it") }
                }
            }
        manager.notify(
            "execution-report".hashCode(),
            NotificationCompat
                .Builder(appContext, ACTION_CHANNEL)
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(
                    android.app.PendingIntent.getActivity(
                        appContext,
                        0,
                        Intent(appContext, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE,
                    ),
                )
                .build(),
        )
    }

    fun loadRules(context: Context) {
        val payload =
            runCatching {
                context.openFileInput(RULES_FILE).use { input ->
                    input.bufferedReader().readText()
                }
            }.getOrNull() ?: return
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
            is NotifyAction -> notify(action)
            is OpenUrlAction -> open(action)
            is SendDataSmsAction -> sendDataSms(action)
            is LaunchAppAction -> launchApp(action)
            is ClipboardAction -> copyToClipboard(action)
            else -> "accepted"
        }
    }

    private fun notify(action: NotifyAction): String {
        val appContext = appContext ?: throw IllegalStateException("Application context unavailable")
        val manager = NotificationManagerCompat.from(appContext)
        manager.createNotificationChannel(
            NotificationChannelCompat.Builder(ACTION_CHANNEL, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName("Automation actions")
                .build(),
        )
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException("POST_NOTIFICATIONS not granted")
        }
        val notification =
            NotificationCompat.Builder(appContext, ACTION_CHANNEL)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(action.title)
                .setContentText(action.body)
                .setContentIntent(
                    android.app.PendingIntent.getActivity(
                        appContext,
                        action.id.hashCode(),
                        Intent(appContext, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(EXTRA_RULE_ID, action.id.removePrefix("action-")),
                        android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE,
                    ),
                )
                .build()
        manager.notify(action.id.hashCode(), notification)
        return "posted"
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
            val manager =
                ContextCompat.getSystemService(appContext, SmsManager::class.java)
                    ?: throw IllegalStateException("SmsManager unavailable")
            manager.sendDataMessage(action.sender, null, action.destinationPort.toShort(), action.payload, null, null)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault().sendDataMessage(action.sender, null, action.destinationPort.toShort(), action.payload, null, null)
        }
        return "sent"
    }

    private fun launchApp(action: LaunchAppAction): String {
        val appContext = appContext ?: throw IllegalStateException("Application context unavailable")
        val intent = appContext.packageManager.getLaunchIntentForPackage(action.packageName)
            ?: throw IllegalStateException("No launchable activity for ${action.packageName}")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)
        return "requested"
    }

    private fun copyToClipboard(action: ClipboardAction): String {
        val appContext = appContext ?: throw IllegalStateException("Application context unavailable")
        val manager = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
            ?: throw IllegalStateException("ClipboardManager unavailable")
        manager.setPrimaryClip(android.content.ClipData.newPlainText(action.id, action.text))
        return "copied"
    }

    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }
}
