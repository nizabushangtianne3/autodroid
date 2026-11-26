package dev.niza.autodroid.engine

interface TriggerEvent {
    fun matches(trigger: Trigger): Boolean

    fun injectTriggerVariables(engine: AutomationEngine) {}
}

data class LinkOpenedEvent(
    val scheme: String?,
    val host: String?,
    val path: String?,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is LinkOpenedTrigger) return false
        if (trigger.scheme != null && trigger.scheme != scheme) return false
        if (trigger.host != null && trigger.host != host) return false
        if (trigger.pathPrefix != null && (path ?: "").startsWith(trigger.pathPrefix)) return true
        return trigger.pathPrefix == null
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        host?.let { engine.setVariable("link_host", it) }
        scheme?.let { engine.setVariable("link_scheme", it) }
        path?.let { engine.setVariable("link_path", it) }
    }
}

data class DataSmsReceivedEvent(
    val destinationPort: Int,
    val sender: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is DataSmsReceivedTrigger) return false
        if (trigger.destinationPort != destinationPort) return false
        return trigger.sender == null || trigger.sender == sender
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("sms_sender", sender)
        engine.setVariable("sms_port", destinationPort.toString())
    }
}

data class AppLaunchedEvent(
    val packageName: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is AppLaunchedTrigger && trigger.packageName == packageName
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("app_package", packageName)
    }
}

data class ScheduleFiredEvent(
    val ruleId: String? = null,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.ScheduleTrigger

    override fun injectTriggerVariables(engine: AutomationEngine) {
        ruleId?.let { engine.setVariable("schedule_rule_id", it) }
    }
}

data class ScreenOnOffEvent(
    val screenOn: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.ScreenOnOffTrigger && trigger.screenOn == screenOn
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("screen_on", screenOn.toString())
    }
}

data class BatteryLevelEvent(
    val level: Int,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.BatteryLevelTrigger) return false
        return if (trigger.direction == "above") {
            level >= trigger.level
        } else {
            level <= trigger.level
        }
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("battery_level", level.toString())
    }
}

data class AirplaneModeEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.AirplaneModeTrigger && trigger.enabled == enabled
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("airplane_mode", enabled.toString())
    }
}

data class BluetoothEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.BluetoothTrigger && trigger.enabled == enabled
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("bluetooth_on", enabled.toString())
    }
}

data class ExternalPowerEvent(
    val connected: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.ExternalPowerTrigger && trigger.connected == connected
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("power_connected", connected.toString())
    }
}

data class HeadphonesEvent(
    val connected: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.HeadphonesTrigger && trigger.connected == connected
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("headphones_connected", connected.toString())
    }
}

data class HotspotEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.HotspotTrigger && trigger.enabled == enabled
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("hotspot_on", enabled.toString())
    }
}

data class SilentModeEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.SilentModeTrigger && trigger.enabled == enabled
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("silent_mode", enabled.toString())
    }
}

data class WifiConnectionEvent(
    val connected: Boolean,
    val ssid: String?,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger is dev.niza.autodroid.engine.WifiConnectionTrigger) {
            return trigger.connected == connected
        }
        if (trigger is dev.niza.autodroid.engine.WifiSSIDTrigger) {
            return connected && ssid == trigger.ssid
        }
        return false
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("wifi_connected", connected.toString())
        ssid?.let { engine.setVariable("wifi_ssid", it) }
    }
}

data class SignalOnOffEvent(
    val signalOn: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.SignalOnOffTrigger && trigger.signalOn == signalOn
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("signal_on", signalOn.toString())
    }
}

data class DockEvent(
    val state: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.DockTrigger && trigger.state == state
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("dock_state", state)
    }
}

data class VpnEvent(
    val connected: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.VpnTrigger && trigger.connected == connected
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("vpn_connected", connected.toString())
    }
}

data class GPSEnabledEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.GPSEnabledTrigger && trigger.enabled == enabled
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("gps_enabled", enabled.toString())
    }
}

class DeviceUnlockedEvent : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.DeviceUnlockedTrigger
}

data class ClipboardChangeEvent(
    val content: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.ClipboardChangeTrigger
}

data class DarkThemeEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.DarkThemeTrigger && trigger.enabled == enabled
    }
}

data class MediaButtonEvent(
    val button: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.MediaButtonTrigger && trigger.button == button
    }
}

data class NotificationEvent(
    val packageName: String,
    val title: String,
    val content: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.NotificationTrigger) return false
        return trigger.packageName == null || trigger.packageName == packageName
    }
}

data class AppLifecycleEvent(
    val packageName: String,
    val added: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.AppLifecycleTrigger) return false
        if (trigger.added != added) return false
        return true
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("app_lifecycle_package", packageName)
        engine.setVariable("app_lifecycle_added", added.toString())
    }
}

data class PowerSaveModeEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        return trigger is dev.niza.autodroid.engine.PowerSaveModeTrigger && trigger.enabled == enabled
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("power_save_mode", enabled.toString())
    }
}

class BootEvent : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.BootTrigger

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("boot_completed", "true")
    }
}

data class BatteryTemperatureEvent(
    val temperatureCelsius: Int,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.BatteryTemperatureTrigger) return false
        val threshold = trigger.temperatureCelsius
        return if (trigger.direction == "below") {
            temperatureCelsius <= threshold
        } else {
            temperatureCelsius >= threshold
        }
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("battery_temperature", temperatureCelsius.toString())
    }
}

data class CellConnectionTypeEvent(
    val connectionType: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.CellConnectionTypeTrigger && trigger.connectionType == connectionType

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("cell_connection", connectionType)
    }
}

data class DataOnOffEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.DataOnOffTrigger && trigger.enabled == enabled

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("mobile_data", enabled.toString())
    }
}

data class AutoRotateEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.AutoRotateTrigger && trigger.enabled == enabled
}

class ShakeDeviceEvent : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.ShakeDeviceTrigger
}

data class FlipDeviceEvent(
    val faceDown: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.FlipDeviceTrigger && trigger.faceDown == faceDown
}

data class ProximityEvent(
    val near: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.ProximityTrigger && trigger.near == near
}

data class LightSensorEvent(
    val lux: Int,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.LightSensorTrigger) return false
        return if (trigger.direction == "below") lux <= trigger.lux else lux >= trigger.lux
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("light_level", lux.toString())
    }
}

data class OrientationEvent(
    val orientation: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.OrientationTrigger && trigger.orientation == orientation
}

data class VolumeButtonEvent(
    val button: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.VolumeButtonTrigger && trigger.button == button

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("volume_button", button)
    }
}

data class VolumeLongPressEvent(
    val button: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.VolumeLongPressTrigger && trigger.button == button
}

data class PowerButtonEvent(
    val event: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.PowerButtonTrigger && trigger.event == event
}

class HomeButtonLongPressEvent : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.HomeButtonLongPressTrigger
}

class RecentAppsEvent : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.RecentAppsTrigger
}

data class IncomingCallEvent(
    val phoneNumber: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.IncomingCallTrigger) return false
        return trigger.phoneNumber == null || trigger.phoneNumber == phoneNumber
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("call_number", phoneNumber)
        engine.setVariable("call_direction", "incoming")
    }
}

data class OutgoingCallEvent(
    val phoneNumber: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.OutgoingCallTrigger) return false
        return trigger.phoneNumber == null || trigger.phoneNumber == phoneNumber
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("call_number", phoneNumber)
        engine.setVariable("call_direction", "outgoing")
    }
}

data class CallMissedEvent(
    val phoneNumber: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.CallMissedTrigger) return false
        return trigger.phoneNumber == null || trigger.phoneNumber == phoneNumber
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("call_number", phoneNumber)
    }
}

data class CallEndedEvent(
    val phoneNumber: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.CallEndedTrigger) return false
        return trigger.phoneNumber == null || trigger.phoneNumber == phoneNumber
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("call_number", phoneNumber)
    }
}

data class CallActiveEvent(
    val direction: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.CallActiveTrigger && trigger.direction == direction
}

data class IncomingSMSEvent(
    val sender: String,
    val message: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.IncomingSMSTrigger) return false
        if (trigger.sender != null && trigger.sender != sender) return false
        return trigger.contentMatch == null || message.contains(trigger.contentMatch)
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("sms_sender", sender)
        engine.setVariable("sms_message", message)
    }
}

data class SMSSentEvent(
    val recipient: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.SMSSentTrigger) return false
        return trigger.recipient == null || trigger.recipient == recipient
    }
}

data class ToastEvent(
    val packageName: String,
    val text: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.ToastTrigger) return false
        if (trigger.packageName != null && trigger.packageName != packageName) return false
        return trigger.textMatch == null || text.contains(trigger.textMatch)
    }
}

data class CalendarEvent(
    val title: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.CalendarTrigger

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("calendar_title", title)
    }
}

data class ShortcutEvent(
    val shortcutName: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.ShortcutTrigger && trigger.shortcutName == shortcutName
}

data class WidgetPressedEvent(
    val widgetLabel: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.WidgetPressedTrigger && trigger.widgetLabel == widgetLabel
}

data class QuickSettingsTileEvent(
    val tileState: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.QuickSettingsTileTrigger && trigger.tileState == tileState
}

data class IntentReceivedEvent(
    val action: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.IntentReceivedTrigger && trigger.action == action
}

data class ShareTextEvent(
    val content: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.ShareTextTrigger) return false
        return trigger.contentMatch == null || content.contains(trigger.contentMatch)
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("shared_text", content)
    }
}

data class ShareFileEvent(
    val path: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.ShareFileTrigger) return false
        return true
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("shared_file", path)
    }
}

data class MacroFinishedEvent(
    val macroId: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.MacroFinishedTrigger && trigger.macroId == macroId
}

data class LogcatEvent(
    val line: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.LogcatTrigger) return false
        return line.contains(trigger.pattern)
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("logcat_line", line)
    }
}

data class SystemSettingEvent(
    val settingKey: String,
    val value: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.SystemSettingTrigger) return false
        return trigger.value == null || trigger.value == value
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("setting_key", settingKey)
        engine.setVariable("setting_value", value)
    }
}

data class UsbDeviceConnectionEvent(
    val connected: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.UsbDeviceConnectionTrigger && trigger.connected == connected
}

data class UsbTetheringEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.UsbTetheringTrigger && trigger.enabled == enabled
}

class IpAddressChangeEvent : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.IpAddressChangeTrigger
}

data class NetworkTransportChangeEvent(
    val transport: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.NetworkTransportChangeTrigger && trigger.transport == transport
}

data class NetworkRoamingEvent(
    val roaming: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.NetworkRoamingTrigger && trigger.roaming == roaming
}

data class SimChangeEvent(
    val state: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.SimChangeTrigger && trigger.state == state
}

data class SleepEvent(
    val asleep: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.SleepTrigger && trigger.asleep == asleep
}

data class NfcStateEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.NfcStateTrigger && trigger.enabled == enabled
}

data class NfcTagEvent(
    val tagId: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.NfcTagTrigger) return false
        return trigger.tagId == null || trigger.tagId == tagId
    }
}

data class GeofenceEvent(
    val name: String,
    val inside: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.GeofenceTrigger) return false
        return trigger.name == name && trigger.enter == inside
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("geofence_name", name)
        engine.setVariable("geofence_state", if (inside) "enter" else "exit")
    }
}

data class LocationEvent(
    val latitude: Double,
    val longitude: Double,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.LocationTrigger) return false
        val distance = haversineMeters(latitude, longitude, trigger.latitude, trigger.longitude)
        return distance <= trigger.radiusMeters
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("location_lat", latitude.toString())
        engine.setVariable("location_lon", longitude.toString())
    }
}

data class CellTowerEvent(
    val cellId: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.CellTowerTrigger && trigger.cellId == cellId
}

data class WeatherEvent(
    val condition: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.WeatherTrigger && trigger.condition == condition

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("weather_condition", condition)
    }
}

data class SunriseSunsetEvent(
    val event: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.SunriseSunsetTrigger && trigger.event == event
}

data class DayOfMonthEvent(
    val day: Int,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.DayOfMonthTrigger && trigger.day == day
}

data class SystemTimeEvent(
    val changed: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.SystemTimeTrigger && trigger.changed == changed
}

data class StopwatchEvent(
    val durationMillis: Long,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.StopwatchTrigger) return false
        return durationMillis >= trigger.durationMillis
    }
}

data class WebhookEvent(
    val url: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.WebhookTrigger && trigger.url == url
}

data class FloatingTextEvent(
    val text: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.FloatingTextTrigger && trigger.text == text
}

data class FloatingButtonEvent(
    val label: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.FloatingButtonTrigger && trigger.label == label
}

data class UIClickEvent(
    val viewId: String?,
    val text: String?,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.UIClickTrigger) return false
        if (trigger.viewId != null && trigger.viewId != viewId) return false
        return trigger.textMatch == null || (text != null && text.contains(trigger.textMatch))
    }
}

data class SwipeEvent(
    val direction: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.SwipeTrigger && trigger.direction == direction
}

data class ScreenContentEvent(
    val text: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.ScreenContentTrigger) return false
        return trigger.textMatch == null || text.contains(trigger.textMatch)
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("screen_text", text)
    }
}

data class ScreenshotContentEvent(
    val text: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.ScreenshotContentTrigger) return false
        return trigger.textMatch == null || text.contains(trigger.textMatch)
    }
}

data class AccessibilityServiceStateEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.AccessibilityServiceStateTrigger && trigger.enabled == enabled
}

class EmptyEvent : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.EmptyTrigger
}

data class VariableEvent(
    val variableName: String,
    val value: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.VariableTrigger) return false
        if (trigger.variableName != variableName) return false
        return evaluateBooleanExpression(trigger.expression, mapOf(variableName to value))
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable(variableName, value)
    }
}

data class MacroEnabledEvent(
    val macroId: String,
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.MacroEnabledTrigger && trigger.macroId == macroId && trigger.enabled == enabled
}

data class ApplicationInvokedEvent(
    val packageName: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.ApplicationInvokedTrigger && trigger.packageName == packageName
}

data class ActivityLaunchedEvent(
    val packageName: String,
    val activityClass: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.ActivityLaunchedTrigger) return false
        if (trigger.packageName != null && trigger.packageName != packageName) return false
        return trigger.activityClass == null || trigger.activityClass == activityClass
    }
}

data class AppEnabledEvent(
    val packageName: String,
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.AppEnabledTrigger && trigger.packageName == packageName && trigger.enabled == enabled
}

data class PriorityModeEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.PriorityModeTrigger && trigger.enabled == enabled
}

data class AutoSyncEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.AutoSyncTrigger && trigger.enabled == enabled
}

data class DayDreamEvent(
    val active: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.DayDreamTrigger && trigger.active == active
}

data class DrawerOpenCloseEvent(
    val opened: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.DrawerOpenCloseTrigger && trigger.opened == opened
}

data class KeyboardOpenCloseEvent(
    val opened: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.KeyboardOpenCloseTrigger && trigger.opened == opened
}

data class MediaTrackChangedEvent(
    val packageName: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.MediaTrackChangedTrigger) return false
        return trigger.packageName == null || trigger.packageName == packageName
    }
}

data class MusicPlayingEvent(
    val playing: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.MusicPlayingTrigger && trigger.playing == playing
}

data class MicrophoneRecordingEvent(
    val recording: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.MicrophoneRecordingTrigger && trigger.recording == recording
}

data class CameraInUseEvent(
    val inUse: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.CameraInUseTrigger && trigger.inUse == inUse
}

data class CameraFlashlightEvent(
    val enabled: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.CameraFlashlightTrigger && trigger.enabled == enabled
}

data class BluetoothBeaconEvent(
    val beaconId: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.BluetoothBeaconTrigger && trigger.beaconId == beaconId
}

data class AssistantActivatedEvent(
    val assistant: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.AssistantActivatedTrigger && trigger.assistant == assistant
}

data class FailedLoginEvent(
    val attempts: Int,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.FailedLoginTrigger) return false
        return attempts >= trigger.attempts
    }
}

data class FingerprintGestureEvent(
    val direction: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.FingerprintGestureTrigger && trigger.direction == direction
}

data class ActivityRecognitionEvent(
    val activity: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.ActivityRecognitionTrigger && trigger.activity == activity
}

data class FoldAngleEvent(
    val angle: Int,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.FoldAngleTrigger) return false
        return if (trigger.direction == "below") angle <= trigger.angle else angle >= trigger.angle
    }
}

data class FileChangedEvent(
    val path: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.FileChangedTrigger && trigger.path == path
}

data class FileOpenedEvent(
    val path: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.FileOpenedTrigger) return false
        return true
    }
}

data class PhotoTakenEvent(
    val packageName: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.PhotoTakenTrigger) return false
        return trigger.packageName == null || trigger.packageName == packageName
    }
}

data class ModeEnterExitEvent(
    val modeName: String,
    val enter: Boolean,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean =
        trigger is dev.niza.autodroid.engine.ModeEnterExitTrigger && trigger.modeName == modeName && trigger.enter == enter
}

data class HttpServerEvent(
    val path: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean = trigger is dev.niza.autodroid.engine.HttpServerTrigger
}

data class EmailReceivedEvent(
    val sender: String,
    val subject: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.EmailReceivedTrigger) return false
        if (trigger.senderMatch != null && !sender.contains(trigger.senderMatch)) return false
        return true
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("email_sender", sender)
        engine.setVariable("email_subject", subject)
    }
}

data class DialNumberEvent(
    val phoneNumber: String,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.DialNumberTrigger) return false
        return trigger.phoneNumber == null || trigger.phoneNumber == phoneNumber
    }
}

data class DataUsedEvent(
    val bytes: Long,
) : TriggerEvent {
    override fun matches(trigger: Trigger): Boolean {
        if (trigger !is dev.niza.autodroid.engine.DataUsedTrigger) return false
        return if (trigger.direction == "below") bytes <= trigger.bytes else bytes >= trigger.bytes
    }

    override fun injectTriggerVariables(engine: AutomationEngine) {
        engine.setVariable("data_used", bytes.toString())
    }
}

class AutomationEngine(
    private val executor: ActionExecutor,
    @Volatile var deviceState: DeviceState? = null,
) {
    private val rules = mutableListOf<Rule>()
    private val variables = mutableMapOf<String, String>()
    private val globalVariables = mutableMapOf<String, GlobalVariable>()
    private val executionLog = mutableListOf<ExecutionLogEntry>()

    private companion object {
        const val MAX_LOG_ENTRIES = 100
    }

    fun register(rule: Rule) {
        rules += rule
    }

    fun replace(rule: Rule) {
        val index = rules.indexOfFirst { it.id == rule.id }
        if (index >= 0) rules[index] = rule else rules += rule
    }

    fun remove(ruleId: String) {
        rules.removeAll { it.id == ruleId }
    }

    fun rules(): List<Rule> = rules.toList()

    fun setVariable(
        name: String,
        value: String,
    ) {
        variables[name] = value
    }

    fun setGlobalVariable(
        name: String,
        type: VariableType,
        value: String,
        entries: List<VariableEntry> = emptyList(),
    ) {
        globalVariables[name] = GlobalVariable(name = name, type = type, value = value, entries = entries)
    }

    fun globalVariable(name: String): GlobalVariable? = globalVariables[name]

    fun globalVariables(): List<GlobalVariable> = globalVariables.values.sortedBy { it.name.lowercase() }

    fun deleteGlobalVariable(name: String) {
        globalVariables.remove(name)
    }

    fun clearGlobalVariables() {
        globalVariables.clear()
    }

    fun setDictionaryEntry(
        name: String,
        key: String,
        value: String,
    ) {
        val variable = globalVariables[name] ?: GlobalVariable(name = name, type = VariableType.DICTIONARY)
        val entries = variable.entries.filterNot { it.key == key }.toMutableList()
        entries += VariableEntry(key = key, value = value)
        globalVariables[name] = variable.copy(type = VariableType.DICTIONARY, entries = entries)
    }

    fun dictionaryEntry(
        name: String,
        key: String,
    ): String? = globalVariables[name]?.entries?.firstOrNull { it.key == key }?.value

    fun clearDictionaryEntry(
        name: String,
        key: String,
    ) {
        val variable = globalVariables[name] ?: return
        globalVariables[name] = variable.copy(entries = variable.entries.filterNot { it.key == key })
    }

    fun setArrayEntry(
        name: String,
        index: Int,
        value: String,
    ) {
        val variable = globalVariables[name] ?: GlobalVariable(name = name, type = VariableType.ARRAY)
        val entries = variable.entries.filterNot { it.key == index.toString() }.toMutableList()
        entries += VariableEntry(key = index.toString(), value = value)
        globalVariables[name] = variable.copy(type = VariableType.ARRAY, entries = entries)
    }

    /** Local variables win; global variables and their dictionary/array entries are the fallback. */
    fun variable(name: String): String? =
        variables[name] ?: run {
            val selector = name.indexOf('[')
            if (selector > 0 && name.endsWith("]")) {
                val base = name.substring(0, selector)
                val key = name.substring(selector + 1, name.length - 1)
                globalVariables[base]?.entries?.firstOrNull { it.key == key }?.value
            } else {
                globalVariables[name]?.takeIf { it.type != VariableType.DICTIONARY && it.type != VariableType.ARRAY }?.value
            }
        }

    /** Flat view used by constraint evaluation: local variables plus each global variable's resolved entries. */
    fun variables(): Map<String, String> {
        val merged = variables.toMutableMap()
        globalVariables.values.forEach { variable ->
            if (variable.type == VariableType.DICTIONARY || variable.type == VariableType.ARRAY) {
                variable.entries.forEach { entry ->
                    merged["${variable.name}[${entry.key}]"] = entry.value
                }
            } else {
                merged[variable.name] = variable.value
            }
        }
        return merged
    }

    fun executionLog(): List<ExecutionLogEntry> = executionLog.toList()

    fun conditionsSatisfied(rule: Rule): Boolean =
        rule.conditions.all { condition ->
            evaluateExpression(condition.expression)
        }

    fun constraintsSatisfied(
        rule: Rule,
        deviceState: DeviceState? = null,
    ): Boolean =
        rule.constraints.all { constraint ->
            constraint.satisfied(deviceState ?: DeviceState(), variables())
        }

    /** Resolves `{name}` and `{name[key]}` placeholders using local then global variables. */
    private fun substituteVariables(expression: String): String {
        var text = expression
        variables.forEach { (name, value) ->
            text = text.replace("{$name}", value)
        }
        globalVariables.values.forEach { variable ->
            if (variable.type == VariableType.DICTIONARY || variable.type == VariableType.ARRAY) {
                variable.entries.forEach { entry ->
                    text = text.replace("{${variable.name}[${entry.key}]}", entry.value)
                }
            } else {
                text = text.replace("{${variable.name}}", variable.value)
            }
        }
        return text
    }

    fun evaluateExpression(expression: String): Boolean {
        val resolved = substituteVariables(expression)
        val orParts = resolved.split("||")
        if (orParts.size > 1) {
            return orParts.any { evaluateSingleExpression(it.trim()) }
        }
        val andParts = resolved.split("&&")
        if (andParts.size > 1) {
            return andParts.all { evaluateSingleExpression(it.trim()) }
        }
        return evaluateSingleExpression(resolved)
    }

    private fun evaluateSingleExpression(resolved: String): Boolean {
        val comparisons = listOf("==", "!=", ">=", "<=", ">", "<")
        for (operator in comparisons) {
            val index = resolved.indexOf(operator)
            if (index > 0) {
                val left = resolved.substring(0, index).trim()
                val right = resolved.substring(index + operator.length).trim()
                return compareValues(left, operator, right)
            }
        }
        return resolved.toBooleanStrictOrNull() ?: false
    }

    private fun compareValues(
        left: String,
        operator: String,
        right: String,
    ): Boolean {
        val leftNumber = left.toDoubleOrNull()
        val rightNumber = right.toDoubleOrNull()
        if (leftNumber != null && rightNumber != null) {
            return when (operator) {
                "==" -> leftNumber == rightNumber
                "!=" -> leftNumber != rightNumber
                ">=" -> leftNumber >= rightNumber
                "<=" -> leftNumber <= rightNumber
                ">" -> leftNumber > rightNumber
                "<" -> leftNumber < rightNumber
                else -> false
            }
        }
        return when (operator) {
            "==" -> left == right
            "!=" -> left != right
            else -> false
        }
    }

    fun dispatch(event: TriggerEvent): ExecutionReport {
        event.injectTriggerVariables(this)
        val matched =
            rules.filter {
                it.enabled && event.matches(it.trigger) && conditionsSatisfied(it) && constraintsSatisfied(it, deviceState)
            }
        val results = matched.flatMap { rule -> executeActionList(rule.actions) }
        val report = ExecutionReport(matchedRuleCount = matched.size, actionResults = results)
        executionLog += ExecutionLogEntry(timestamp = System.currentTimeMillis(), report = report)
        if (executionLog.size > MAX_LOG_ENTRIES) {
            executionLog.removeAt(0)
        }
        return report
    }

    private fun executeActionList(actions: List<Action>): List<ActionResult> {
        val results = mutableListOf<ActionResult>()
        var insideIf = false
        var inElseBranch = false
        var conditionPassed = false
        var insideIterate = false
        var iterateKeyVariable = ""
        var iterateValueVariable = ""
        var iterateIndex = 0
        val iterateActions = mutableListOf<Action>()
        var insideRepeat = false
        var repeatRemaining = 0
        var repeatIndex = 0
        val repeatActions = mutableListOf<Action>()

        for (action in actions) {
            when (action) {
                is dev.niza.autodroid.engine.RepeatStartAction -> {
                    insideRepeat = true
                    repeatRemaining = action.count
                    repeatIndex = 0
                    repeatActions.clear()
                }
                is dev.niza.autodroid.engine.IterateDictionaryAction -> {
                    insideIterate = true
                    iterateKeyVariable = action.keyVariable
                    iterateValueVariable = action.valueVariable
                    iterateIndex = 0
                    iterateActions.clear()
                }
                is dev.niza.autodroid.engine.IterateDictionaryEndAction -> {
                    val entries = globalVariables[action.dictionaryName]?.entries ?: emptyList()
                    entries.forEach { entry ->
                        iterateIndex++
                        setVariable(iterateKeyVariable, entry.key)
                        setVariable(iterateValueVariable, entry.value)
                        iterateActions.forEach { inner -> results += runInner(inner, iterateIndex) }
                    }
                    insideIterate = false
                    iterateKeyVariable = ""
                    iterateValueVariable = ""
                    iterateIndex = 0
                    iterateActions.clear()
                }
                is dev.niza.autodroid.engine.RepeatEndAction -> {
                    if (insideRepeat) {
                        repeat(repeatRemaining) {
                            repeatIndex++
                            repeatActions.forEach { inner -> results += runInner(inner, repeatIndex) }
                        }
                    }
                    insideRepeat = false
                    repeatActions.clear()
                }
                is dev.niza.autodroid.engine.IfConditionAction -> {
                    insideIf = true
                    inElseBranch = false
                    conditionPassed = evaluateExpression(action.expression)
                }
                is dev.niza.autodroid.engine.ElseConditionAction -> {
                    inElseBranch = insideIf && !conditionPassed
                }
                is dev.niza.autodroid.engine.EndIfAction -> {
                    insideIf = false
                    inElseBranch = false
                    conditionPassed = false
                }
                else -> {
                    val shouldExecute = !insideIf || conditionPassed || inElseBranch
                    val target =
                        when {
                            insideIterate -> iterateActions
                            insideRepeat -> repeatActions
                            else -> null
                        }
                    if (shouldExecute && target == null) {
                        results += executeSingle(action)
                    } else if (shouldExecute && target != null) {
                        target += action
                    }
                }
            }
        }
        return results
    }

    private fun runInner(
        action: Action,
        loopIndex: Int,
    ): ActionResult =
        when (action) {
            is dev.niza.autodroid.engine.SetVariableAction -> {
                val resolved = substituteVariables(action.value).replace("{loop_index}", loopIndex.toString())
                setVariable(action.name, resolved)
                ActionResult(action.id, success = true)
            }
            else -> executeSingle(action)
        }

    private fun executeSingle(action: Action): ActionResult =
        when (action) {
            is dev.niza.autodroid.engine.SetDictionaryEntryAction ->
                applyDictionaryWrite(action).let { ActionResult(action.id, success = true, message = it) }
            is dev.niza.autodroid.engine.ClearDictionaryEntryAction ->
                applyDictionaryClear(action).let { ActionResult(action.id, success = true, message = it) }
            is dev.niza.autodroid.engine.SetArrayEntryAction ->
                applyArrayWrite(action).let { ActionResult(action.id, success = true, message = it) }
            is dev.niza.autodroid.engine.ClearVariablesAction ->
                applyClearVariables(action).let { ActionResult(action.id, success = true, message = it) }
            is dev.niza.autodroid.engine.SetVariableAction -> {
                setVariable(action.name, substituteVariables(action.value))
                ActionResult(action.id, success = true)
            }
            else -> executor.execute(action)
        }

    private fun applyDictionaryWrite(action: dev.niza.autodroid.engine.SetDictionaryEntryAction): String {
        val key = substituteVariables(action.key)
        val value = substituteVariables(action.value)
        setDictionaryEntry(action.dictionaryName, key, value)
        return "set"
    }

    private fun applyDictionaryClear(action: dev.niza.autodroid.engine.ClearDictionaryEntryAction): String {
        clearDictionaryEntry(action.dictionaryName, substituteVariables(action.key))
        return "cleared"
    }

    private fun applyArrayWrite(action: dev.niza.autodroid.engine.SetArrayEntryAction): String {
        setArrayEntry(action.arrayName, action.index, substituteVariables(action.value))
        return "set"
    }

    private fun applyClearVariables(action: dev.niza.autodroid.engine.ClearVariablesAction): String {
        variables.clear()
        return "cleared"
    }
}

data class ExecutionReport(
    val matchedRuleCount: Int,
    val actionResults: List<ActionResult>,
) {
    val success: Boolean
        get() = actionResults.all { it.success }
}

fun interface ActionExecutor {
    fun execute(action: Action): ActionResult
}

internal fun haversineMeters(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double,
): Double {
    val earthRadius = 6_371_000.0
    val deltaLat = Math.toRadians(lat2 - lat1)
    val deltaLon = Math.toRadians(lon2 - lon1)
    val a =
        Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2)
    return 2 * earthRadius * Math.asin(Math.min(1.0, Math.sqrt(a)))
}

data class ActionResult(
    val actionId: String,
    val success: Boolean,
    val message: String? = null,
)

data class ExecutionLogEntry(
    val timestamp: Long,
    val report: ExecutionReport,
)
