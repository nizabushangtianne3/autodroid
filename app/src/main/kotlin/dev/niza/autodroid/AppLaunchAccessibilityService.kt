package dev.niza.autodroid

import android.accessibilityservice.AccessibilityService
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import dev.niza.autodroid.engine.AppLaunchedEvent

class AppLaunchAccessibilityService : AccessibilityService() {
    private var lastPackageName: String? = null
    private var lastDispatchAtMs = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString() ?: return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        if (packageName == applicationContext.packageName) return
        val now = SystemClock.elapsedRealtime()
        if (packageName == lastPackageName && now - lastDispatchAtMs < DEBOUNCE_MS) return
        lastPackageName = packageName
        lastDispatchAtMs = now
        Runtime.engine.dispatch(AppLaunchedEvent(packageName))
    }

    override fun onInterrupt() = Unit

    private companion object {
        private const val DEBOUNCE_MS = 1000L
    }
}
