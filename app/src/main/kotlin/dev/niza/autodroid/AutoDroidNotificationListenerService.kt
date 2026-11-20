package dev.niza.autodroid

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dev.niza.autodroid.engine.NotificationEvent

class AutoDroidNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification = sbn.notification ?: return
        val extras = notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val content = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        Runtime.initialize(this)
        Runtime.loadRules(this)
        Runtime.dispatchAndReport(NotificationEvent(sbn.packageName, title, content))
    }
}
