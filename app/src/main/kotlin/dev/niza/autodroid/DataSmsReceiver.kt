package dev.niza.autodroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import dev.niza.autodroid.engine.DataSmsReceivedEvent

class DataSmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION) return
        val port = intent.data?.port?.let { runCatching { it.toInt() }.getOrNull() } ?: return
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent).orEmpty()
        val message = messages.firstOrNull() ?: return
        Runtime.loadRules(context)
        Runtime.engine.dispatch(
            DataSmsReceivedEvent(
                destinationPort = port,
                sender = message.originatingAddress ?: return,
            ),
        )
    }
}
