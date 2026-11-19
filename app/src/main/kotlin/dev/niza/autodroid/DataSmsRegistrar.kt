package dev.niza.autodroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Telephony
import androidx.core.content.ContextCompat
import dev.niza.autodroid.engine.DataSmsReceivedTrigger
import dev.niza.autodroid.engine.RuleStore

object DataSmsRegistrar {
    private val receivers = mutableMapOf<Int, BroadcastReceiver>()
    private var appContext: Context? = null

    fun registeredPortsForTest(): Set<Int> = receivers.keys.toSet()

    fun resetForTest(context: Context) {
        receivers.keys.toList().forEach(::unregister)
        appContext = null
    }

    fun refresh(context: Context) {
        val appContext = context.applicationContext
        this.appContext = appContext
        val enabledPorts = readEnabledPorts(appContext)

        receivers.keys.filter { it !in enabledPorts }.forEach(::unregister)
        enabledPorts.filter { it !in receivers.keys }.forEach(::register)
    }

    private fun readEnabledPorts(context: Context): Set<Int> {
        val payload =
            runCatching {
                context.openFileInput("rules.json").use { it.bufferedReader().readText() }
            }.getOrNull() ?: return emptySet()
        return RuleStore().decode(payload)
            .filter { it.enabled }
            .mapNotNull { it.trigger as? DataSmsReceivedTrigger }
            .map { it.destinationPort }
            .filter { it > 0 }
            .toSet()
    }

    private fun register(port: Int) {
        val appContext = appContext ?: return
        val receiver =
            object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context,
                    intent: Intent,
                ) {
                    DataSmsReceiver().onReceive(context, intent)
                }
            }
        ContextCompat.registerReceiver(
            appContext,
            receiver,
            IntentFilter(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION, "sms://localhost:$port"),
            ContextCompat.RECEIVER_EXPORTED,
        )
        receivers[port] = receiver
    }

    private fun unregister(port: Int) {
        val receiver = receivers.remove(port) ?: return
        appContext?.unregisterReceiver(receiver)
    }
}
