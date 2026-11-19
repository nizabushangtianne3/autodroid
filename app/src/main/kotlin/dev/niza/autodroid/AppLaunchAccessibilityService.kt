package dev.niza.autodroid

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import dev.niza.autodroid.engine.AppLaunchedEvent

class AppLaunchAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString() ?: return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        Runtime.engine.dispatch(AppLaunchedEvent(packageName))
    }

    override fun onInterrupt() = Unit
}
