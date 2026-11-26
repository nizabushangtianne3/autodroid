package dev.niza.autodroid.engine

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Trigger {
    val id: String
}

val Trigger.value: String?
    get() =
        when (this) {
            is LinkOpenedTrigger -> host
            is DataSmsReceivedTrigger -> destinationPort.toString()
            is AppLaunchedTrigger -> packageName
            is ScheduleTrigger -> "every ${intervalMillis}ms"
            is ScreenOnOffTrigger -> if (screenOn) "screen on" else "screen off"
            is BatteryLevelTrigger -> "battery $level%"
            is AirplaneModeTrigger -> if (enabled) "airplane on" else "airplane off"
            is BluetoothTrigger -> if (enabled) "bluetooth on" else "bluetooth off"
            is ExternalPowerTrigger -> if (connected) "power connected" else "power disconnected"
            is HeadphonesTrigger -> if (connected) "headphones connected" else "headphones disconnected"
            is HotspotTrigger -> if (enabled) "hotspot on" else "hotspot off"
            is SilentModeTrigger -> if (enabled) "silent on" else "silent off"
            is WifiConnectionTrigger -> if (connected) "wifi connected" else "wifi disconnected"
            is WifiSSIDTrigger -> "wifi ssid $ssid"
            is SignalOnOffTrigger -> if (signalOn) "signal on" else "signal off"
            is DockTrigger -> "dock $state"
            is VpnTrigger -> if (connected) "vpn connected" else "vpn disconnected"
            is GPSEnabledTrigger -> if (enabled) "gps on" else "gps off"
            is DeviceUnlockedTrigger -> "device unlocked"
            is ClipboardChangeTrigger -> "clipboard changed"
            is DarkThemeTrigger -> if (enabled) "dark theme on" else "dark theme off"
            is MediaButtonTrigger -> "media button"
            is NotificationTrigger -> "notification received"
            is AppLifecycleTrigger -> if (added) "app installed" else "app removed"
            is PowerSaveModeTrigger -> if (enabled) "battery saver on" else "battery saver off"
            is BootTrigger -> if (deviceBoot) "device boot" else "device shutdown"
            is BatteryTemperatureTrigger -> "battery temp $temperatureCelsius°C"
            is CellConnectionTypeTrigger -> "cell $connectionType"
            is DataOnOffTrigger -> if (enabled) "mobile data on" else "mobile data off"
            is AutoRotateTrigger -> if (enabled) "auto rotate on" else "auto rotate off"
            is ShakeDeviceTrigger -> "shake (sensitivity $sensitivity)"
            is FlipDeviceTrigger -> if (faceDown) "face down" else "face up"
            is ProximityTrigger -> if (near) "near" else "far"
            is LightSensorTrigger -> "light $lux lux"
            is OrientationTrigger -> "orientation $orientation"
            is VolumeButtonTrigger -> "volume $button"
            is VolumeLongPressTrigger -> "volume $button long press"
            is PowerButtonTrigger -> "power $event"
            is HomeButtonLongPressTrigger -> "home long press"
            is RecentAppsTrigger -> "recents opened"
            is IncomingCallTrigger -> phoneNumber?.let { "call from $it" } ?: "incoming call"
            is OutgoingCallTrigger -> phoneNumber?.let { "call to $it" } ?: "outgoing call"
            is CallMissedTrigger -> phoneNumber?.let { "missed $it" } ?: "missed call"
            is CallEndedTrigger -> phoneNumber?.let { "call ended $it" } ?: "call ended"
            is CallActiveTrigger -> "call active $direction"
            is IncomingSMSTrigger -> sender?.let { "sms from $it" } ?: "incoming sms"
            is SMSSentTrigger -> recipient?.let { "sms to $it" } ?: "sms sent"
            is ToastTrigger -> packageName?.let { "toast from $it" } ?: "toast shown"
            is CalendarTrigger -> "calendar event"
            is ShortcutTrigger -> "shortcut $shortcutName"
            is WidgetPressedTrigger -> "widget $widgetLabel"
            is QuickSettingsTileTrigger -> "tile $tileState"
            is IntentReceivedTrigger -> "intent $action"
            is ShareTextTrigger -> contentMatch?.let { "share text $it" } ?: "shared text"
            is ShareFileTrigger -> mimeType?.let { "share file $it" } ?: "shared file"
            is MacroFinishedTrigger -> "macro $macroId finished"
            is LogcatTrigger -> "logcat $pattern"
            is SystemSettingTrigger -> "setting $settingKey"
            is UsbDeviceConnectionTrigger -> if (connected) "usb connected" else "usb disconnected"
            is UsbTetheringTrigger -> if (enabled) "usb tethering on" else "usb tethering off"
            is IpAddressChangeTrigger -> "ip changed"
            is NetworkTransportChangeTrigger -> "transport $transport"
            is NetworkRoamingTrigger -> if (roaming) "roaming on" else "roaming off"
            is SimChangeTrigger -> "sim $state"
            is SleepTrigger -> if (asleep) "device asleep" else "device awake"
            is NfcStateTrigger -> if (enabled) "nfc on" else "nfc off"
            is NfcTagTrigger -> tagId?.let { "tag $it" } ?: "nfc tag"
            is GeofenceTrigger -> "$name (${radiusMeters}m)"
            is LocationTrigger -> "$latitude,$longitude (${radiusMeters}m)"
            is CellTowerTrigger -> "cell tower $cellId"
            is WeatherTrigger -> "weather $condition"
            is SunriseSunsetTrigger -> event
            is DayOfMonthTrigger -> "day $day"
            is SystemTimeTrigger -> if (changed) "time changed" else "time set"
            is StopwatchTrigger -> "stopwatch ${durationMillis}ms"
            is WebhookTrigger -> "webhook $url"
            is FloatingTextTrigger -> "floating text $text"
            is FloatingButtonTrigger -> "floating button $label"
            is UIClickTrigger -> textMatch?.let { "click $it" } ?: viewId?.let { "click #$it" } ?: "ui click"
            is SwipeTrigger -> "swipe $direction"
            is ScreenContentTrigger -> textMatch?.let { "screen text $it" } ?: "screen content"
            is ScreenshotContentTrigger -> textMatch?.let { "screenshot text $it" } ?: "screenshot content"
            is AccessibilityServiceStateTrigger -> if (enabled) "accessibility on" else "accessibility off"
            is EmptyTrigger -> if (immediate) "immediate" else "manual"
            is VariableTrigger -> "$variableName $expression"
            is MacroEnabledTrigger -> "macro $macroId ${if (enabled) "enabled" else "disabled"}"
            is ApplicationInvokedTrigger -> packageName
            is ActivityLaunchedTrigger -> activityClass ?: packageName ?: "activity launched"
            is AppEnabledTrigger -> "$packageName ${if (enabled) "enabled" else "disabled"}"
            is PriorityModeTrigger -> if (enabled) "priority on" else "priority off"
            is AutoSyncTrigger -> if (enabled) "auto sync on" else "auto sync off"
            is DayDreamTrigger -> if (active) "daydream started" else "daydream stopped"
            is DrawerOpenCloseTrigger -> if (opened) "drawer opened" else "drawer closed"
            is KeyboardOpenCloseTrigger -> if (opened) "keyboard opened" else "keyboard closed"
            is MediaTrackChangedTrigger -> packageName?.let { "track from $it" } ?: "track changed"
            is MusicPlayingTrigger -> if (playing) "music playing" else "music stopped"
            is MicrophoneRecordingTrigger -> if (recording) "mic recording" else "mic idle"
            is CameraInUseTrigger -> if (inUse) "camera in use" else "camera free"
            is CameraFlashlightTrigger -> if (enabled) "torch on" else "torch off"
            is BluetoothBeaconTrigger -> "beacon $beaconId"
            is AssistantActivatedTrigger -> "assistant $assistant"
            is FailedLoginTrigger -> "$attempts failed logins"
            is FingerprintGestureTrigger -> "fingerprint $direction"
            is ActivityRecognitionTrigger -> "activity $activity"
            is FoldAngleTrigger -> "fold $angle"
            is FileChangedTrigger -> path
            is FileOpenedTrigger -> mimeType?.let { "file $it" } ?: "file opened"
            is PhotoTakenTrigger -> packageName?.let { "photo from $it" } ?: "photo taken"
            is ModeEnterExitTrigger -> "$modeName ${if (enter) "entered" else "exited"}"
            is HttpServerTrigger -> "http server :$port"
            is EmailReceivedTrigger -> senderMatch?.let { "email from $it" } ?: "email received"
            is DialNumberTrigger -> phoneNumber?.let { "dial $it" } ?: "dial"
            is DataUsedTrigger -> "data $bytes bytes"
        }

val Action.summary: String
    get() =
        when (this) {
            is OpenUrlAction -> url
            is SendDataSmsAction -> "$sender:$destinationPort • ${payload.size} bytes"
            is NotifyAction -> "$title • $body"
            is SetVariableAction -> "$name = $value"
            is IfConditionAction -> "if $expression"
            is ElseConditionAction -> "else"
            is EndIfAction -> "end if"
            is WaitAction -> "wait ${durationMillis}ms"
            is RepeatStartAction -> "repeat ${count}x"
            is RepeatEndAction -> "end repeat"
            is LaunchAppAction -> packageName
            is ClipboardAction -> text
            is IterateDictionaryAction -> "$dictionaryName -> $keyVariable/$valueVariable"
            is IterateDictionaryEndAction -> "end iterate $dictionaryName"
            is ClearDictionaryEntryAction -> "$dictionaryName[$key]"
            is SetDictionaryEntryAction -> "$dictionaryName[$key] = $value"
            is SetArrayEntryAction -> "$arrayName[$index] = $value"
            is ClearVariablesAction -> "clear all variables"
            is SetVolumeAction -> "volume $stream=$level"
            is VolumeIncrementDecrementAction -> "volume $stream $direction $steps"
            is SetBrightnessAction -> "brightness $level${if (automatic) " auto" else ""}"
            is DimScreenAction -> if (dimmed) "dim screen" else "undim screen"
            is SetScreenTimeoutAction -> "screen timeout ${seconds}s"
            is SetAutoRotateAction -> if (enabled) "auto rotate on" else "auto rotate off"
            is ForceScreenRotationAction -> "rotation $orientation"
            is SetRingerModeAction -> "ringer $mode"
            is SetVibrateAction -> if (enabled) "vibrate on" else "vibrate off"
            is SilentModeAction -> "silent $mode"
            is VibrateAction -> "vibrate ${durationMillis}ms"
            is SpeakTextAction -> "speak $text"
            is PlaySoundAction -> "play $sound"
            is SetWifiAction -> if (enabled) "wifi on" else "wifi off"
            is SetBluetoothAction -> if (enabled) "bluetooth on" else "bluetooth off"
            is SetGPSAction -> if (enabled) "gps on" else "gps off"
            is SetAirplaneModeAction -> if (enabled) "airplane on" else "airplane off"
            is SetDataAction -> if (enabled) "mobile data on" else "mobile data off"
            is SetHotspotAction -> if (enabled) "hotspot on" else "hotspot off"
            is SetNFCAction -> if (enabled) "nfc on" else "nfc off"
            is SetAutoSyncAction -> if (enabled) "auto sync on" else "auto sync off"
            is SetRingtoneAction -> "ringtone $ringtoneUri"
            is SetWallpaperAction -> "wallpaper $imageUri"
            is CameraFlashLightAction -> if (enabled) "torch on" else "torch off"
            is TakeScreenshotAction -> "screenshot ${path ?: "auto"}"
            is ScreenOnAction -> if (on) "screen on" else "screen off"
            is KeepAwakeAction -> "keep awake ${durationSeconds}s"
            is ImmersiveModeAction -> if (enabled) "immersive on" else "immersive off"
            is ExpandCollapseStatusBarAction -> if (expanded) "expand status bar" else "collapse status bar"
            is DarkThemeAction -> if (enabled) "dark theme on" else "dark theme off"
            is BatterySaverAction -> if (enabled) "battery saver on" else "battery saver off"
            is LaunchHomeScreenAction -> "home screen"
            is LaunchActivityAction -> "$packageName/$activityClass"
            is CloseApplicationAction -> "close $packageName"
            is KillBackgroundAppAction -> "kill $packageName"
            is PressBackAction -> "back"
            is InputKeyEventAction -> "key $keyCode"
            is TouchScreenAction -> "tap $x,$y"
            is ClearNotificationsAction -> packageName?.let { "clear notifications $it" } ?: "clear notifications"
            is ConfigureAppNotificationsAction -> "notifications $packageName=$enabled"
            is HeadsUpNotificationsAction -> if (enabled) "heads up on" else "heads up off"
            is AmbientDisplayAction -> if (enabled) "ambient display on" else "ambient display off"
            is ClearDialogAction -> "clear dialog"
            is MessageDialogAction -> "dialog $title"
            is OptionDialogAction -> "options $title"
            is SelectionDialogAction -> "selection $title -> $variableName"
            is OverlayDialogAction -> "overlay $title"
            is ToastAction -> "toast $text"
            is PopUpAction -> "popup $text"
            is FloatingTextAction -> "floating $text"
            is FloatingButtonConfigureAction -> "floating button $label"
            is SendIntentAction -> "intent $action"
            is SendSMSAction -> "sms $recipient"
            is SendEmailAction -> "email $recipient"
            is ForwardSMSAction -> "forward sms to $recipient"
            is MakeCallAction -> "call $phoneNumber"
            is AnswerCallAction -> "answer call"
            is RejectCallAction -> "reject call"
            is SpeakerPhoneAction -> if (enabled) "speaker on" else "speaker off"
            is MuteMicrophoneAction -> if (muted) "mute mic" else "unmute mic"
            is RecordMicrophoneAction -> "record mic ${durationSeconds}s"
            is ReadSoundLevelAction -> "sound level -> $variableName"
            is UDPCommandAction -> "udp $host:$port"
            is HttpRequestAction -> "$method $url"
            is WriteToFileAction -> "write $path"
            is ReadFileAction -> "read $path -> $variableName"
            is FileOperationAction -> "$operation $sourcePath"
            is FileSelectionAction -> "pick file -> $variableName"
            is ExportLogAction -> "export log $path"
            is ClearLogAction -> "clear log"
            is ShareTextAction -> "share $subject"
            is ShareLocationAction -> "share location"
            is UpdateClipboardAction -> "clipboard $text"
            is TextManipulationAction -> "$operation $sourceVariable -> $targetVariable"
            is ArrayManipulationAction -> "$operation $arrayName"
            is JsonParseAction -> "json $jsonPath -> $targetVariable"
            is EncryptDecryptTextAction -> "$mode $sourceVariable -> $targetVariable"
            is WaitUntilTriggerAction -> "wait trigger $triggerId"
            is EnableTriggerAction -> "enable trigger $triggerId"
            is DisableTriggerAction -> "disable trigger $triggerId"
            is ForceMacroRunAction -> "run macro $macroId"
            is DisableMacroAction -> "macro $macroId=$enabled"
            is SetModeAction -> "mode $modeName=$active"
            is LogAction -> "log $message"
            is ControlMediaAction -> "media $command"
            is RebootAction -> "reboot $mode"
            is ShellScriptAction -> "shell $script"
            is SecureSettingsAction -> "secure $key"
            is SystemSettingAction -> "$settingType $key=$value"
            is GenerateQRCodeAction -> "qr $path"
            is SetLanguageAction -> "locale $locale"
            is SetNotificationSoundAction -> "notify sound $soundUri"
            is DisplayDensityAction -> "density $density"
            is FontScaleAction -> "font $scale"
            is SetKeyguardAction -> if (enabled) "keyguard on" else "keyguard off"
            is ClearAppDataAction -> "clear data $packageName"
            is DisableAppAction -> "app $packageName=$enabled"
            is SetAlarmClockAction -> "alarm $message"
            is AddCalendarEntryAction -> "event $title"
            is ClearCallLogAction -> "clear call log"
            is DeleteSMSAction -> sender?.let { "delete sms $it" } ?: "delete sms"
            is GetContactsAction -> "contacts -> $variableName"
            is GetInstalledAppsAction -> "apps -> $variableName"
            is GetNotificationsAction -> "notifications -> $variableName"
            is GetLightLevelAction -> "light -> $variableName"
            is SensorReadAction -> "sensor $sensor -> $variableName"
            is ConnectivityCheckAction -> "check $host -> $variableName"
            is ConfirmNextAction -> "confirm $message"
            is IfConfirmedThenAction -> "if confirmed $message"
            is GotoAction -> "goto $label"
            is MutexAction -> "mutex $name"
            is MutexEndAction -> "end mutex $name"
            is BreakFromLoopAction -> "break"
            is ContinueLoopAction -> "continue"
            is CancelActiveMacroAction -> "stop macro"
            is PauseAction -> "wait ${durationMillis}ms"
            is SetMacroDroidIconAction -> "icon $icon"
            is AndroidShortcutsAction -> "shortcut $shortcutId"
            is LocalePluginAction -> "plugin $pluginPackage"
            is JavaScriptAction -> "js $script"
        }

@Serializable
sealed interface Constraint {
    val id: String

    fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean
}

@Serializable
@SerialName("constraint_battery_level")
data class BatteryLevelConstraint(
    override val id: String,
    val level: Int,
    val direction: String = "above",
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.batteryLevel?.let { if (direction == "below") it <= level else it >= level } ?: false
}

@Serializable
@SerialName("constraint_battery_saver")
data class BatterySaverConstraint(
    override val id: String,
    val enabled: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.batterySaver == enabled
}

@Serializable
@SerialName("constraint_bluetooth")
data class BluetoothConstraint(
    override val id: String,
    val enabled: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.bluetoothEnabled == enabled
}

@Serializable
@SerialName("constraint_cell_connection")
data class CellConnectionConstraint(
    override val id: String,
    val requiredType: String,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.cellConnectionType == requiredType
}

@Serializable
@SerialName("constraint_day_time")
data class DayTimeConstraint(
    override val id: String,
    val startMinuteOfDay: Int,
    val endMinuteOfDay: Int,
    val days: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean {
        val calendar = state.calendar
        if (calendar == null) return true
        val day = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val dayMatches = days.contains(if (day == java.util.Calendar.SUNDAY) 7 else day - 1)
        if (!dayMatches) return false
        val minute = calendar.get(java.util.Calendar.HOUR_OF_DAY) * 60 + calendar.get(java.util.Calendar.MINUTE)
        return if (startMinuteOfDay <= endMinuteOfDay) {
            minute in startMinuteOfDay..endMinuteOfDay
        } else {
            minute >= startMinuteOfDay || minute <= endMinuteOfDay
        }
    }
}

@Serializable
@SerialName("constraint_device_locked")
data class DeviceLockedConstraint(
    override val id: String,
    val locked: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.deviceLocked == locked
}

@Serializable
@SerialName("constraint_external_power")
data class ExternalPowerConstraint(
    override val id: String,
    val connected: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.externalPowerConnected == connected
}

@Serializable
@SerialName("constraint_gps_enabled")
data class GPSEnabledConstraint(
    override val id: String,
    val enabled: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.gpsEnabled == enabled
}

@Serializable
@SerialName("constraint_headphones")
data class HeadphonesConstraint(
    override val id: String,
    val connected: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.headphonesConnected == connected
}

@Serializable
@SerialName("constraint_network_type")
data class NetworkTypeConstraint(
    override val id: String,
    val requiredType: String,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.networkType == requiredType
}

@Serializable
@SerialName("constraint_screen_on_off")
data class ScreenOnOffConstraint(
    override val id: String,
    val screenOn: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.screenOn == screenOn
}

@Serializable
@SerialName("constraint_silent_mode")
data class SilentModeConstraint(
    override val id: String,
    val enabled: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.silentModeEnabled == enabled
}

@Serializable
@SerialName("constraint_trigger_enabled")
data class TriggerEnabledConstraint(
    override val id: String,
    val triggerId: String,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.enabledTriggerIds.contains(triggerId)
}

@Serializable
@SerialName("constraint_variable")
data class VariableConstraint(
    override val id: String,
    val expression: String,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = evaluateBooleanExpression(expression, variables)
}

@Serializable
@SerialName("constraint_wifi")
data class WifiConstraint(
    override val id: String,
    val connected: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.wifiConnected == connected
}

@Serializable
@SerialName("constraint_airplane_mode")
data class AirplaneModeConstraint(
    override val id: String,
    val enabled: Boolean,
) : Constraint {
    override fun satisfied(
        state: DeviceState,
        variables: Map<String, String>,
    ): Boolean = state.airplaneModeEnabled == enabled
}

@Serializable
data class DeviceState(
    val batteryLevel: Int? = null,
    val batterySaver: Boolean = false,
    val bluetoothEnabled: Boolean = false,
    val cellConnectionType: String = "unknown",
    @Contextual val calendar: java.util.Calendar? = null,
    val deviceLocked: Boolean = false,
    val externalPowerConnected: Boolean = false,
    val gpsEnabled: Boolean = false,
    val headphonesConnected: Boolean = false,
    val networkType: String = "unknown",
    val screenOn: Boolean = false,
    val silentModeEnabled: Boolean = false,
    val enabledTriggerIds: Set<String> = emptySet(),
    val wifiConnected: Boolean = false,
    val airplaneModeEnabled: Boolean = false,
)

@Serializable
@SerialName("link")
data class LinkOpenedTrigger(
    override val id: String,
    val host: String? = null,
    val scheme: String? = null,
    val pathPrefix: String? = null,
) : Trigger

@Serializable
@SerialName("data_sms")
data class DataSmsReceivedTrigger(
    override val id: String,
    val destinationPort: Int,
    val sender: String? = null,
) : Trigger

@Serializable
@SerialName("app_launched")
data class AppLaunchedTrigger(
    override val id: String,
    val packageName: String,
) : Trigger

@Serializable
@SerialName("schedule")
data class ScheduleTrigger(
    override val id: String,
    val intervalMillis: Long,
    val startAtElapsedRealtime: Long = 0,
) : Trigger

@Serializable
@SerialName("screen_on_off")
data class ScreenOnOffTrigger(
    override val id: String,
    val screenOn: Boolean,
) : Trigger

@Serializable
@SerialName("battery_level")
data class BatteryLevelTrigger(
    override val id: String,
    val level: Int,
    val direction: String = "below",
) : Trigger

@Serializable
@SerialName("airplane_mode")
data class AirplaneModeTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("bluetooth")
data class BluetoothTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("external_power")
data class ExternalPowerTrigger(
    override val id: String,
    val connected: Boolean,
) : Trigger

@Serializable
@SerialName("headphones")
data class HeadphonesTrigger(
    override val id: String,
    val connected: Boolean,
) : Trigger

@Serializable
@SerialName("hotspot")
data class HotspotTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("silent_mode")
data class SilentModeTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("wifi_connection")
data class WifiConnectionTrigger(
    override val id: String,
    val connected: Boolean,
) : Trigger

@Serializable
@SerialName("wifi_ssid")
data class WifiSSIDTrigger(
    override val id: String,
    val ssid: String,
) : Trigger

@Serializable
@SerialName("signal_on_off")
data class SignalOnOffTrigger(
    override val id: String,
    val signalOn: Boolean,
) : Trigger

@Serializable
@SerialName("dock")
data class DockTrigger(
    override val id: String,
    val state: String,
) : Trigger

@Serializable
@SerialName("vpn")
data class VpnTrigger(
    override val id: String,
    val connected: Boolean,
) : Trigger

@Serializable
@SerialName("gps_enabled")
data class GPSEnabledTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("device_unlocked")
data class DeviceUnlockedTrigger(
    override val id: String,
) : Trigger

@Serializable
@SerialName("clipboard_change")
data class ClipboardChangeTrigger(
    override val id: String,
) : Trigger

@Serializable
@SerialName("dark_theme")
data class DarkThemeTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("media_button")
data class MediaButtonTrigger(
    override val id: String,
    val button: String,
) : Trigger

@Serializable
@SerialName("notification")
data class NotificationTrigger(
    override val id: String,
    val packageName: String? = null,
) : Trigger

@Serializable
@SerialName("app_lifecycle")
data class AppLifecycleTrigger(
    override val id: String,
    val added: Boolean,
) : Trigger

@Serializable
@SerialName("power_save_mode")
data class PowerSaveModeTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("boot")
data class BootTrigger(
    override val id: String,
    val deviceBoot: Boolean = true,
) : Trigger

@Serializable
@SerialName("battery_temperature")
data class BatteryTemperatureTrigger(
    override val id: String,
    val temperatureCelsius: Int,
    val direction: String = "above",
) : Trigger

@Serializable
@SerialName("cell_connection_type")
data class CellConnectionTypeTrigger(
    override val id: String,
    val connectionType: String,
) : Trigger

@Serializable
@SerialName("data_on_off")
data class DataOnOffTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("auto_rotate")
data class AutoRotateTrigger(
    override val id: String,
    val enabled: Boolean,
) : Trigger

@Serializable
@SerialName("shake_device")
data class ShakeDeviceTrigger(
    override val id: String,
    val sensitivity: Int = 2,
) : Trigger

@Serializable
@SerialName("flip_device")
data class FlipDeviceTrigger(
    override val id: String,
    val faceDown: Boolean = true,
) : Trigger

@Serializable
@SerialName("proximity")
data class ProximityTrigger(
    override val id: String,
    val near: Boolean = true,
) : Trigger

@Serializable
@SerialName("light_sensor")
data class LightSensorTrigger(
    override val id: String,
    val lux: Int,
    val direction: String = "above",
) : Trigger

@Serializable
@SerialName("orientation")
data class OrientationTrigger(
    override val id: String,
    val orientation: String,
) : Trigger

@Serializable
@SerialName("volume_button")
data class VolumeButtonTrigger(
    override val id: String,
    val button: String = "up",
) : Trigger

@Serializable
@SerialName("volume_long_press")
data class VolumeLongPressTrigger(
    override val id: String,
    val button: String = "up",
) : Trigger

@Serializable
@SerialName("power_button")
data class PowerButtonTrigger(
    override val id: String,
    val event: String = "toggle",
) : Trigger

@Serializable
@SerialName("home_button_long_press")
data class HomeButtonLongPressTrigger(
    override val id: String,
) : Trigger

@Serializable
@SerialName("recent_apps")
data class RecentAppsTrigger(
    override val id: String,
) : Trigger

@Serializable
@SerialName("incoming_call")
data class IncomingCallTrigger(
    override val id: String,
    val phoneNumber: String? = null,
) : Trigger

@Serializable
@SerialName("outgoing_call")
data class OutgoingCallTrigger(
    override val id: String,
    val phoneNumber: String? = null,
) : Trigger

@Serializable
@SerialName("call_missed")
data class CallMissedTrigger(
    override val id: String,
    val phoneNumber: String? = null,
) : Trigger

@Serializable
@SerialName("call_ended")
data class CallEndedTrigger(
    override val id: String,
    val phoneNumber: String? = null,
) : Trigger

@Serializable
@SerialName("call_active")
data class CallActiveTrigger(
    override val id: String,
    val direction: String = "incoming",
) : Trigger

@Serializable
@SerialName("incoming_sms")
data class IncomingSMSTrigger(
    override val id: String,
    val sender: String? = null,
    val contentMatch: String? = null,
) : Trigger

@Serializable
@SerialName("sms_sent")
data class SMSSentTrigger(
    override val id: String,
    val recipient: String? = null,
) : Trigger

@Serializable
@SerialName("toast")
data class ToastTrigger(
    override val id: String,
    val packageName: String? = null,
    val textMatch: String? = null,
) : Trigger

@Serializable
@SerialName("calendar")
data class CalendarTrigger(
    override val id: String,
    val calendarId: Long? = null,
) : Trigger

@Serializable
@SerialName("shortcut")
data class ShortcutTrigger(
    override val id: String,
    val shortcutName: String,
) : Trigger

@Serializable
@SerialName("widget_pressed")
data class WidgetPressedTrigger(
    override val id: String,
    val widgetLabel: String,
) : Trigger

@Serializable
@SerialName("quick_settings_tile")
data class QuickSettingsTileTrigger(
    override val id: String,
    val tileState: String = "on",
) : Trigger

@Serializable
@SerialName("intent_received")
data class IntentReceivedTrigger(
    override val id: String,
    val action: String,
) : Trigger

@Serializable
@SerialName("share_text")
data class ShareTextTrigger(
    override val id: String,
    val contentMatch: String? = null,
) : Trigger

@Serializable
@SerialName("share_file")
data class ShareFileTrigger(
    override val id: String,
    val mimeType: String? = null,
) : Trigger

@Serializable
@SerialName("macro_finished")
data class MacroFinishedTrigger(
    override val id: String,
    val macroId: String,
) : Trigger

@Serializable
@SerialName("logcat")
data class LogcatTrigger(
    override val id: String,
    val pattern: String,
) : Trigger

@Serializable
@SerialName("system_setting")
data class SystemSettingTrigger(
    override val id: String,
    val settingKey: String,
    val value: String? = null,
) : Trigger

@Serializable
@SerialName("usb_device_connection")
data class UsbDeviceConnectionTrigger(
    override val id: String,
    val connected: Boolean = true,
) : Trigger

@Serializable
@SerialName("usb_tethering")
data class UsbTetheringTrigger(
    override val id: String,
    val enabled: Boolean = true,
) : Trigger

@Serializable
@SerialName("ip_address_change")
data class IpAddressChangeTrigger(
    override val id: String,
) : Trigger

@Serializable
@SerialName("network_transport_change")
data class NetworkTransportChangeTrigger(
    override val id: String,
    val transport: String = "wifi",
) : Trigger

@Serializable
@SerialName("network_roaming")
data class NetworkRoamingTrigger(
    override val id: String,
    val roaming: Boolean = true,
) : Trigger

@Serializable
@SerialName("sim_change")
data class SimChangeTrigger(
    override val id: String,
    val state: String = "changed",
) : Trigger

@Serializable
@SerialName("sleep")
data class SleepTrigger(
    override val id: String,
    val asleep: Boolean = true,
) : Trigger

@Serializable
@SerialName("nfc_state")
data class NfcStateTrigger(
    override val id: String,
    val enabled: Boolean = true,
) : Trigger

@Serializable
@SerialName("nfc_tag")
data class NfcTagTrigger(
    override val id: String,
    val tagId: String? = null,
) : Trigger

@Serializable
@SerialName("geofence")
data class GeofenceTrigger(
    override val id: String,
    val name: String,
    val radiusMeters: Int = 100,
    val enter: Boolean = true,
) : Trigger

@Serializable
@SerialName("location")
data class LocationTrigger(
    override val id: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int = 200,
) : Trigger

@Serializable
@SerialName("cell_tower")
data class CellTowerTrigger(
    override val id: String,
    val cellId: String,
) : Trigger

@Serializable
@SerialName("weather")
data class WeatherTrigger(
    override val id: String,
    val condition: String,
) : Trigger

@Serializable
@SerialName("sunrise_sunset")
data class SunriseSunsetTrigger(
    override val id: String,
    val event: String = "sunrise",
) : Trigger

@Serializable
@SerialName("day_of_month")
data class DayOfMonthTrigger(
    override val id: String,
    val day: Int,
) : Trigger

@Serializable
@SerialName("system_time")
data class SystemTimeTrigger(
    override val id: String,
    val changed: Boolean = true,
) : Trigger

@Serializable
@SerialName("stopwatch")
data class StopwatchTrigger(
    override val id: String,
    val durationMillis: Long,
) : Trigger

@Serializable
@SerialName("webhook")
data class WebhookTrigger(
    override val id: String,
    val url: String,
) : Trigger

@Serializable
@SerialName("calendar_widget")
data class FloatingTextTrigger(
    override val id: String,
    val text: String,
) : Trigger

@Serializable
@SerialName("floating_button")
data class FloatingButtonTrigger(
    override val id: String,
    val label: String = "Run",
) : Trigger

@Serializable
@SerialName("ui_click")
data class UIClickTrigger(
    override val id: String,
    val viewId: String? = null,
    val textMatch: String? = null,
) : Trigger

@Serializable
@SerialName("swipe")
data class SwipeTrigger(
    override val id: String,
    val direction: String = "up",
) : Trigger

@Serializable
@SerialName("screen_content")
data class ScreenContentTrigger(
    override val id: String,
    val textMatch: String? = null,
) : Trigger

@Serializable
@SerialName("screenshot_content")
data class ScreenshotContentTrigger(
    override val id: String,
    val textMatch: String? = null,
) : Trigger

@Serializable
@SerialName("accessibility_state")
data class AccessibilityServiceStateTrigger(
    override val id: String,
    val enabled: Boolean = true,
) : Trigger

@Serializable
@SerialName("empty")
data class EmptyTrigger(
    override val id: String,
    val immediate: Boolean = true,
) : Trigger

@Serializable
@SerialName("variable")
data class VariableTrigger(
    override val id: String,
    val variableName: String,
    val expression: String,
) : Trigger

@Serializable
@SerialName("macro_enabled")
data class MacroEnabledTrigger(
    override val id: String,
    val macroId: String,
    val enabled: Boolean = true,
) : Trigger

@Serializable
@SerialName("application_invoked")
data class ApplicationInvokedTrigger(
    override val id: String,
    val packageName: String,
) : Trigger

@Serializable
@SerialName("activity_launched")
data class ActivityLaunchedTrigger(
    override val id: String,
    val packageName: String? = null,
    val activityClass: String? = null,
) : Trigger

@Serializable
@SerialName("app_enabled")
data class AppEnabledTrigger(
    override val id: String,
    val packageName: String,
    val enabled: Boolean = true,
) : Trigger

@Serializable
@SerialName("priority_mode")
data class PriorityModeTrigger(
    override val id: String,
    val enabled: Boolean = true,
) : Trigger

@Serializable
@SerialName("auto_sync")
data class AutoSyncTrigger(
    override val id: String,
    val enabled: Boolean = true,
) : Trigger

@Serializable
@SerialName("day_dream")
data class DayDreamTrigger(
    override val id: String,
    val active: Boolean = true,
) : Trigger

@Serializable
@SerialName("drawer_open_close")
data class DrawerOpenCloseTrigger(
    override val id: String,
    val opened: Boolean = true,
) : Trigger

@Serializable
@SerialName("keyboard_open_close")
data class KeyboardOpenCloseTrigger(
    override val id: String,
    val opened: Boolean = true,
) : Trigger

@Serializable
@SerialName("media_track_changed")
data class MediaTrackChangedTrigger(
    override val id: String,
    val packageName: String? = null,
) : Trigger

@Serializable
@SerialName("music_playing")
data class MusicPlayingTrigger(
    override val id: String,
    val playing: Boolean = true,
) : Trigger

@Serializable
@SerialName("microphone_recording")
data class MicrophoneRecordingTrigger(
    override val id: String,
    val recording: Boolean = true,
) : Trigger

@Serializable
@SerialName("camera_in_use")
data class CameraInUseTrigger(
    override val id: String,
    val inUse: Boolean = true,
) : Trigger

@Serializable
@SerialName("camera_flashlight")
data class CameraFlashlightTrigger(
    override val id: String,
    val enabled: Boolean = true,
) : Trigger

@Serializable
@SerialName("bluetooth_beacon")
data class BluetoothBeaconTrigger(
    override val id: String,
    val beaconId: String,
) : Trigger

@Serializable
@SerialName("assistant_activated")
data class AssistantActivatedTrigger(
    override val id: String,
    val assistant: String = "default",
) : Trigger

@Serializable
@SerialName("failed_login")
data class FailedLoginTrigger(
    override val id: String,
    val attempts: Int = 3,
) : Trigger

@Serializable
@SerialName("fingerprint_gesture")
data class FingerprintGestureTrigger(
    override val id: String,
    val direction: String = "down",
) : Trigger

@Serializable
@SerialName("activity_recognition")
data class ActivityRecognitionTrigger(
    override val id: String,
    val activity: String = "still",
) : Trigger

@Serializable
@SerialName("fold_angle")
data class FoldAngleTrigger(
    override val id: String,
    val angle: Int,
    val direction: String = "above",
) : Trigger

@Serializable
@SerialName("file_changed")
data class FileChangedTrigger(
    override val id: String,
    val path: String,
) : Trigger

@Serializable
@SerialName("file_opened")
data class FileOpenedTrigger(
    override val id: String,
    val mimeType: String? = null,
) : Trigger

@Serializable
@SerialName("photo_taken")
data class PhotoTakenTrigger(
    override val id: String,
    val packageName: String? = null,
) : Trigger

@Serializable
@SerialName("mode_enter_exit")
data class ModeEnterExitTrigger(
    override val id: String,
    val modeName: String,
    val enter: Boolean = true,
) : Trigger

@Serializable
@SerialName("http_server")
data class HttpServerTrigger(
    override val id: String,
    val port: Int,
) : Trigger

@Serializable
@SerialName("email_received")
data class EmailReceivedTrigger(
    override val id: String,
    val account: String? = null,
    val senderMatch: String? = null,
) : Trigger

@Serializable
@SerialName("dial_number")
data class DialNumberTrigger(
    override val id: String,
    val phoneNumber: String? = null,
) : Trigger

@Serializable
@SerialName("set_volume")
data class SetVolumeAction(
    override val id: String,
    val stream: String = "media",
    val level: Int,
) : Action

@Serializable
@SerialName("volume_increment")
data class VolumeIncrementDecrementAction(
    override val id: String,
    val stream: String = "media",
    val direction: String = "increment",
    val steps: Int = 1,
) : Action

@Serializable
@SerialName("set_brightness")
data class SetBrightnessAction(
    override val id: String,
    val level: Int,
    val automatic: Boolean = false,
) : Action

@Serializable
@SerialName("dim_screen")
data class DimScreenAction(
    override val id: String,
    val dimmed: Boolean = true,
) : Action

@Serializable
@SerialName("set_screen_timeout")
data class SetScreenTimeoutAction(
    override val id: String,
    val seconds: Int,
) : Action

@Serializable
@SerialName("set_auto_rotate")
data class SetAutoRotateAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("force_screen_rotation")
data class ForceScreenRotationAction(
    override val id: String,
    val orientation: String = "portrait",
) : Action

@Serializable
@SerialName("set_ringer_mode")
data class SetRingerModeAction(
    override val id: String,
    val mode: String = "normal",
) : Action

@Serializable
@SerialName("set_vibrate")
data class SetVibrateAction(
    override val id: String,
    val enabled: Boolean = true,
) : Action

@Serializable
@SerialName("silent_mode")
data class SilentModeAction(
    override val id: String,
    val mode: String = "vibrate",
) : Action

@Serializable
@SerialName("vibrate")
data class VibrateAction(
    override val id: String,
    val durationMillis: Long = 500,
    val pattern: String? = null,
) : Action

@Serializable
@SerialName("speak_text")
data class SpeakTextAction(
    override val id: String,
    val text: String,
    val queue: Boolean = false,
) : Action

@Serializable
@SerialName("play_sound")
data class PlaySoundAction(
    override val id: String,
    val sound: String,
    val stream: String = "media",
) : Action

@Serializable
@SerialName("set_wifi")
data class SetWifiAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("set_bluetooth")
data class SetBluetoothAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("set_gps")
data class SetGPSAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("set_airplane_mode")
data class SetAirplaneModeAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("set_mobile_data")
data class SetDataAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("set_hotspot")
data class SetHotspotAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("set_nfc")
data class SetNFCAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("set_auto_sync")
data class SetAutoSyncAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("set_ringtone")
data class SetRingtoneAction(
    override val id: String,
    val ringtoneUri: String,
) : Action

@Serializable
@SerialName("set_wallpaper")
data class SetWallpaperAction(
    override val id: String,
    val imageUri: String,
) : Action

@Serializable
@SerialName("camera_flashlight")
data class CameraFlashLightAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("take_screenshot")
data class TakeScreenshotAction(
    override val id: String,
    val path: String? = null,
) : Action

@Serializable
@SerialName("screen_on")
data class ScreenOnAction(
    override val id: String,
    val on: Boolean = true,
) : Action

@Serializable
@SerialName("keep_awake")
data class KeepAwakeAction(
    override val id: String,
    val durationSeconds: Int,
) : Action

@Serializable
@SerialName("immersive_mode")
data class ImmersiveModeAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("expand_status_bar")
data class ExpandCollapseStatusBarAction(
    override val id: String,
    val expanded: Boolean = true,
) : Action

@Serializable
@SerialName("dark_theme")
data class DarkThemeAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("battery_saver")
data class BatterySaverAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("launch_home_screen")
data class LaunchHomeScreenAction(
    override val id: String,
) : Action

@Serializable
@SerialName("launch_activity")
data class LaunchActivityAction(
    override val id: String,
    val packageName: String,
    val activityClass: String,
) : Action

@Serializable
@SerialName("close_application")
data class CloseApplicationAction(
    override val id: String,
    val packageName: String,
) : Action

@Serializable
@SerialName("kill_background_app")
data class KillBackgroundAppAction(
    override val id: String,
    val packageName: String,
) : Action

@Serializable
@SerialName("press_back")
data class PressBackAction(
    override val id: String,
) : Action

@Serializable
@SerialName("input_key_event")
data class InputKeyEventAction(
    override val id: String,
    val keyCode: String,
) : Action

@Serializable
@SerialName("touch_screen")
data class TouchScreenAction(
    override val id: String,
    val x: Int,
    val y: Int,
    val durationMillis: Int = 100,
) : Action

@Serializable
@SerialName("clear_notifications")
data class ClearNotificationsAction(
    override val id: String,
    val packageName: String? = null,
) : Action

@Serializable
@SerialName("configure_app_notifications")
data class ConfigureAppNotificationsAction(
    override val id: String,
    val packageName: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("heads_up_notifications")
data class HeadsUpNotificationsAction(
    override val id: String,
    val enabled: Boolean = true,
) : Action

@Serializable
@SerialName("ambient_display")
data class AmbientDisplayAction(
    override val id: String,
    val enabled: Boolean = true,
) : Action

@Serializable
@SerialName("clear_dialog")
data class ClearDialogAction(
    override val id: String,
) : Action

@Serializable
@SerialName("message_dialog")
data class MessageDialogAction(
    override val id: String,
    val title: String,
    val message: String,
    val timeoutSeconds: Int = 0,
) : Action

@Serializable
@SerialName("option_dialog")
data class OptionDialogAction(
    override val id: String,
    val title: String,
    val options: String,
) : Action

@Serializable
@SerialName("selection_dialog")
data class SelectionDialogAction(
    override val id: String,
    val title: String,
    val variableName: String,
    val options: String,
) : Action

@Serializable
@SerialName("overlay_dialog")
data class OverlayDialogAction(
    override val id: String,
    val title: String,
) : Action

@Serializable
@SerialName("toast")
data class ToastAction(
    override val id: String,
    val text: String,
) : Action

@Serializable
@SerialName("pop_up")
data class PopUpAction(
    override val id: String,
    val text: String,
) : Action

@Serializable
@SerialName("floating_text")
data class FloatingTextAction(
    override val id: String,
    val text: String,
    val durationSeconds: Int = 5,
) : Action

@Serializable
@SerialName("floating_button")
data class FloatingButtonConfigureAction(
    override val id: String,
    val label: String,
    val visible: Boolean = true,
) : Action

@Serializable
@SerialName("send_intent")
data class SendIntentAction(
    override val id: String,
    val action: String,
    val extras: String? = null,
) : Action

@Serializable
@SerialName("send_sms")
data class SendSMSAction(
    override val id: String,
    val recipient: String,
    val message: String,
) : Action

@Serializable
@SerialName("send_email")
data class SendEmailAction(
    override val id: String,
    val recipient: String,
    val subject: String,
    val body: String,
) : Action

@Serializable
@SerialName("forward_sms")
data class ForwardSMSAction(
    override val id: String,
    val recipient: String,
    val includeSender: Boolean = true,
) : Action

@Serializable
@SerialName("make_call")
data class MakeCallAction(
    override val id: String,
    val phoneNumber: String,
) : Action

@Serializable
@SerialName("answer_call")
data class AnswerCallAction(
    override val id: String,
) : Action

@Serializable
@SerialName("reject_call")
data class RejectCallAction(
    override val id: String,
) : Action

@Serializable
@SerialName("speaker_phone")
data class SpeakerPhoneAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("mute_microphone")
data class MuteMicrophoneAction(
    override val id: String,
    val muted: Boolean = true,
) : Action

@Serializable
@SerialName("record_microphone")
data class RecordMicrophoneAction(
    override val id: String,
    val path: String,
    val durationSeconds: Int,
) : Action

@Serializable
@SerialName("read_sound_level")
data class ReadSoundLevelAction(
    override val id: String,
    val variableName: String,
) : Action

@Serializable
@SerialName("udp_command")
data class UDPCommandAction(
    override val id: String,
    val host: String,
    val port: Int,
    val payload: String,
) : Action

@Serializable
@SerialName("http_request")
data class HttpRequestAction(
    override val id: String,
    val url: String,
    val method: String = "GET",
    val body: String? = null,
    val responseVariable: String? = null,
) : Action

@Serializable
@SerialName("write_to_file")
data class WriteToFileAction(
    override val id: String,
    val path: String,
    val content: String,
    val append: Boolean = false,
) : Action

@Serializable
@SerialName("read_file")
data class ReadFileAction(
    override val id: String,
    val path: String,
    val variableName: String,
) : Action

@Serializable
@SerialName("file_operation")
data class FileOperationAction(
    override val id: String,
    val operation: String,
    val sourcePath: String,
    val destinationPath: String? = null,
) : Action

@Serializable
@SerialName("file_selection")
data class FileSelectionAction(
    override val id: String,
    val variableName: String,
    val mimeType: String = "*/*",
) : Action

@Serializable
@SerialName("export_log")
data class ExportLogAction(
    override val id: String,
    val path: String,
) : Action

@Serializable
@SerialName("clear_log")
data class ClearLogAction(
    override val id: String,
) : Action

@Serializable
@SerialName("share_text")
data class ShareTextAction(
    override val id: String,
    val subject: String,
    val text: String,
) : Action

@Serializable
@SerialName("share_location")
data class ShareLocationAction(
    override val id: String,
    val message: String,
) : Action

@Serializable
@SerialName("set_clipboard")
data class UpdateClipboardAction(
    override val id: String,
    val text: String,
    val append: Boolean = false,
) : Action

@Serializable
@SerialName("text_manipulation")
data class TextManipulationAction(
    override val id: String,
    val sourceVariable: String,
    val targetVariable: String,
    val operation: String,
    val parameter: String? = null,
) : Action

@Serializable
@SerialName("array_manipulation")
data class ArrayManipulationAction(
    override val id: String,
    val arrayName: String,
    val operation: String,
    val index: Int = 0,
    val value: String = "",
) : Action

@Serializable
@SerialName("json_parse")
data class JsonParseAction(
    override val id: String,
    val sourceVariable: String,
    val jsonPath: String,
    val targetVariable: String,
) : Action

@Serializable
@SerialName("encrypt_decrypt")
data class EncryptDecryptTextAction(
    override val id: String,
    val sourceVariable: String,
    val targetVariable: String,
    val mode: String = "encrypt",
    val key: String = "",
) : Action

@Serializable
@SerialName("wait_until_trigger")
data class WaitUntilTriggerAction(
    override val id: String,
    val triggerId: String,
    val timeoutMillis: Long = 0,
) : Action

@Serializable
@SerialName("enable_trigger")
data class EnableTriggerAction(
    override val id: String,
    val triggerId: String,
) : Action

@Serializable
@SerialName("disable_trigger")
data class DisableTriggerAction(
    override val id: String,
    val triggerId: String,
) : Action

@Serializable
@SerialName("force_macro_run")
data class ForceMacroRunAction(
    override val id: String,
    val macroId: String,
    val ignoreConstraints: Boolean = false,
) : Action

@Serializable
@SerialName("disable_macro")
data class DisableMacroAction(
    override val id: String,
    val macroId: String,
    val enabled: Boolean = false,
) : Action

@Serializable
@SerialName("set_mode")
data class SetModeAction(
    override val id: String,
    val modeName: String,
    val active: Boolean = true,
) : Action

@Serializable
@SerialName("log_action")
data class LogAction(
    override val id: String,
    val message: String,
    val level: String = "info",
) : Action

@Serializable
@SerialName("control_media")
data class ControlMediaAction(
    override val id: String,
    val command: String = "pause",
) : Action

@Serializable
@SerialName("reboot")
data class RebootAction(
    override val id: String,
    val mode: String = "normal",
) : Action

@Serializable
@SerialName("shell_script")
data class ShellScriptAction(
    override val id: String,
    val script: String,
    val useRoot: Boolean = false,
) : Action

@Serializable
@SerialName("secure_settings")
data class SecureSettingsAction(
    override val id: String,
    val key: String,
    val value: String,
) : Action

@Serializable
@SerialName("system_setting")
data class SystemSettingAction(
    override val id: String,
    val key: String,
    val value: String,
    val settingType: String = "system",
) : Action

@Serializable
@SerialName("generate_qr")
data class GenerateQRCodeAction(
    override val id: String,
    val content: String,
    val path: String,
) : Action

@Serializable
@SerialName("set_language")
data class SetLanguageAction(
    override val id: String,
    val locale: String,
) : Action

@Serializable
@SerialName("set_notification_sound")
data class SetNotificationSoundAction(
    override val id: String,
    val soundUri: String,
) : Action

@Serializable
@SerialName("display_density")
data class DisplayDensityAction(
    override val id: String,
    val density: Int,
) : Action

@Serializable
@SerialName("font_scale")
data class FontScaleAction(
    override val id: String,
    val scale: Float,
) : Action

@Serializable
@SerialName("set_keyguard")
data class SetKeyguardAction(
    override val id: String,
    val enabled: Boolean,
) : Action

@Serializable
@SerialName("clear_app_data")
data class ClearAppDataAction(
    override val id: String,
    val packageName: String,
) : Action

@Serializable
@SerialName("disable_app")
data class DisableAppAction(
    override val id: String,
    val packageName: String,
    val enabled: Boolean = false,
) : Action

@Serializable
@SerialName("set_alarm_clock")
data class SetAlarmClockAction(
    override val id: String,
    val timeMillis: Long,
    val message: String,
) : Action

@Serializable
@SerialName("add_calendar_entry")
data class AddCalendarEntryAction(
    override val id: String,
    val title: String,
    val startTimeMillis: Long,
    val durationMinutes: Int,
) : Action

@Serializable
@SerialName("clear_call_log")
data class ClearCallLogAction(
    override val id: String,
) : Action

@Serializable
@SerialName("delete_sms")
data class DeleteSMSAction(
    override val id: String,
    val sender: String? = null,
) : Action

@Serializable
@SerialName("get_contacts")
data class GetContactsAction(
    override val id: String,
    val variableName: String,
) : Action

@Serializable
@SerialName("get_installed_apps")
data class GetInstalledAppsAction(
    override val id: String,
    val variableName: String,
) : Action

@Serializable
@SerialName("get_notifications")
data class GetNotificationsAction(
    override val id: String,
    val variableName: String,
) : Action

@Serializable
@SerialName("get_light_level")
data class GetLightLevelAction(
    override val id: String,
    val variableName: String,
) : Action

@Serializable
@SerialName("sensor_read")
data class SensorReadAction(
    override val id: String,
    val sensor: String,
    val variableName: String,
) : Action

@Serializable
@SerialName("connectivity_check")
data class ConnectivityCheckAction(
    override val id: String,
    val host: String,
    val variableName: String,
) : Action

@Serializable
@SerialName("confirm_next_action")
data class ConfirmNextAction(
    override val id: String,
    val message: String,
    val timeoutSeconds: Int = 30,
) : Action

@Serializable
@SerialName("if_confirmed_then")
data class IfConfirmedThenAction(
    override val id: String,
    val message: String,
    val timeoutSeconds: Int = 30,
) : Action

@Serializable
@SerialName("goto")
data class GotoAction(
    override val id: String,
    val label: String,
) : Action

@Serializable
@SerialName("mutex")
data class MutexAction(
    override val id: String,
    val name: String,
) : Action

@Serializable
@SerialName("mutex_end")
data class MutexEndAction(
    override val id: String,
    val name: String,
) : Action

@Serializable
@SerialName("break_from_loop")
data class BreakFromLoopAction(
    override val id: String,
) : Action

@Serializable
@SerialName("continue_loop")
data class ContinueLoopAction(
    override val id: String,
) : Action

@Serializable
@SerialName("stop_macro")
data class CancelActiveMacroAction(
    override val id: String,
) : Action

@Serializable
@SerialName("wait_until")
data class PauseAction(
    override val id: String,
    val durationMillis: Long,
) : Action

@Serializable
@SerialName("set_macro_droid_icon")
data class SetMacroDroidIconAction(
    override val id: String,
    val icon: String,
) : Action

@Serializable
@SerialName("open_shortcut")
data class AndroidShortcutsAction(
    override val id: String,
    val shortcutPackage: String,
    val shortcutId: String,
) : Action

@Serializable
@SerialName("locale_plugin")
data class LocalePluginAction(
    override val id: String,
    val pluginPackage: String,
    val configuration: String,
) : Action

@Serializable
@SerialName("javascript")
data class JavaScriptAction(
    override val id: String,
    val script: String,
) : Action

@Serializable
@SerialName("data_used")
data class DataUsedTrigger(
    override val id: String,
    val bytes: Long,
    val direction: String = "above",
) : Trigger

@Serializable
sealed interface Action {
    val id: String
}

@Serializable
@SerialName("notify")
data class NotifyAction(
    override val id: String,
    val title: String,
    val body: String,
) : Action

@Serializable
@SerialName("send_data_sms")
data class SendDataSmsAction(
    override val id: String,
    val destinationPort: Int,
    val sender: String,
    val payload: ByteArray,
) : Action

@Serializable
@SerialName("open_url")
data class OpenUrlAction(
    override val id: String,
    val url: String,
) : Action

@Serializable
data class Condition(
    val expression: String,
)

@Serializable
@SerialName("set_variable")
data class SetVariableAction(
    override val id: String,
    val name: String,
    val value: String,
) : Action

@Serializable
@SerialName("if_condition")
data class IfConditionAction(
    override val id: String,
    val expression: String,
) : Action

@Serializable
@SerialName("else_condition")
data class ElseConditionAction(
    override val id: String,
) : Action

@Serializable
@SerialName("end_if")
data class EndIfAction(
    override val id: String,
) : Action

@Serializable
@SerialName("wait")
data class WaitAction(
    override val id: String,
    val durationMillis: Long,
) : Action

@Serializable
@SerialName("repeat_start")
data class RepeatStartAction(
    override val id: String,
    val count: Int,
) : Action

@Serializable
@SerialName("repeat_end")
data class RepeatEndAction(
    override val id: String,
) : Action

@Serializable
@SerialName("launch_app")
data class LaunchAppAction(
    override val id: String,
    val packageName: String,
) : Action

@Serializable
@SerialName("clipboard")
data class ClipboardAction(
    override val id: String,
    val text: String,
) : Action

@Serializable
@SerialName("iterate_dictionary")
data class IterateDictionaryAction(
    override val id: String,
    val dictionaryName: String,
    val keyVariable: String,
    val valueVariable: String,
) : Action

@Serializable
@SerialName("iterate_dictionary_end")
data class IterateDictionaryEndAction(
    override val id: String,
    val dictionaryName: String,
) : Action

@Serializable
@SerialName("clear_dictionary_entry")
data class ClearDictionaryEntryAction(
    override val id: String,
    val dictionaryName: String,
    val key: String,
) : Action

@Serializable
@SerialName("set_dictionary_entry")
data class SetDictionaryEntryAction(
    override val id: String,
    val dictionaryName: String,
    val key: String,
    val value: String,
) : Action

@Serializable
@SerialName("set_array_entry")
data class SetArrayEntryAction(
    override val id: String,
    val arrayName: String,
    val index: Int,
    val value: String,
) : Action

@Serializable
@SerialName("clear_variables")
data class ClearVariablesAction(
    override val id: String,
) : Action

@Serializable
data class Rule(
    val id: String,
    val name: String,
    val trigger: Trigger,
    val conditions: List<Condition> = emptyList(),
    val constraints: List<Constraint> = emptyList(),
    val actions: List<Action>,
    val enabled: Boolean = true,
)
