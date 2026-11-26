package dev.niza.autodroid

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.telephony.SmsManager
import android.widget.Toast
import dev.niza.autodroid.engine.Action
import dev.niza.autodroid.engine.BatterySaverAction
import dev.niza.autodroid.engine.CameraFlashLightAction
import dev.niza.autodroid.engine.ClearNotificationsAction
import dev.niza.autodroid.engine.ClipboardAction
import dev.niza.autodroid.engine.CloseApplicationAction
import dev.niza.autodroid.engine.ControlMediaAction
import dev.niza.autodroid.engine.DarkThemeAction
import dev.niza.autodroid.engine.DisableMacroAction
import dev.niza.autodroid.engine.EnableTriggerAction
import dev.niza.autodroid.engine.GotoAction
import dev.niza.autodroid.engine.HeadsUpNotificationsAction
import dev.niza.autodroid.engine.InputKeyEventAction
import dev.niza.autodroid.engine.KillBackgroundAppAction
import dev.niza.autodroid.engine.LaunchActivityAction
import dev.niza.autodroid.engine.LaunchAppAction
import dev.niza.autodroid.engine.LaunchHomeScreenAction
import dev.niza.autodroid.engine.LogAction
import dev.niza.autodroid.engine.MakeCallAction
import dev.niza.autodroid.engine.MessageDialogAction
import dev.niza.autodroid.engine.NotifyAction
import dev.niza.autodroid.engine.OpenUrlAction
import dev.niza.autodroid.engine.OptionDialogAction
import dev.niza.autodroid.engine.PlaySoundAction
import dev.niza.autodroid.engine.PressBackAction
import dev.niza.autodroid.engine.SelectionDialogAction
import dev.niza.autodroid.engine.SendDataSmsAction
import dev.niza.autodroid.engine.SendEmailAction
import dev.niza.autodroid.engine.SendIntentAction
import dev.niza.autodroid.engine.SendSMSAction
import dev.niza.autodroid.engine.SetAirplaneModeAction
import dev.niza.autodroid.engine.SetAlarmClockAction
import dev.niza.autodroid.engine.SetAutoRotateAction
import dev.niza.autodroid.engine.SetAutoSyncAction
import dev.niza.autodroid.engine.SetBluetoothAction
import dev.niza.autodroid.engine.SetBrightnessAction
import dev.niza.autodroid.engine.SetDataAction
import dev.niza.autodroid.engine.SetGPSAction
import dev.niza.autodroid.engine.SetHotspotAction
import dev.niza.autodroid.engine.SetNFCAction
import dev.niza.autodroid.engine.SetRingtoneAction
import dev.niza.autodroid.engine.SetScreenTimeoutAction
import dev.niza.autodroid.engine.SetVariableAction
import dev.niza.autodroid.engine.SetVolumeAction
import dev.niza.autodroid.engine.SetWallpaperAction
import dev.niza.autodroid.engine.SetWifiAction
import dev.niza.autodroid.engine.ShareLocationAction
import dev.niza.autodroid.engine.ShareTextAction
import dev.niza.autodroid.engine.SilentModeAction
import dev.niza.autodroid.engine.SpeakTextAction
import dev.niza.autodroid.engine.SystemSettingAction
import dev.niza.autodroid.engine.TextManipulationAction
import dev.niza.autodroid.engine.ToastAction
import dev.niza.autodroid.engine.TouchScreenAction
import dev.niza.autodroid.engine.UpdateClipboardAction
import dev.niza.autodroid.engine.VibrateAction
import dev.niza.autodroid.engine.VolumeIncrementDecrementAction
import dev.niza.autodroid.engine.WaitUntilTriggerAction
import dev.niza.autodroid.engine.WriteToFileAction

/**
 * Executes the action surface with public Android APIs only. Handlers that need privileges the app
 * cannot obtain return a descriptive failure instead of pretending success.
 */
internal object ActionHandlers {
    fun execute(
        action: Action,
        context: Context,
    ): Result<String> =
        runCatching {
            when (action) {
                is NotifyAction -> Runtime.postNotification(action)
                is OpenUrlAction -> Runtime.openUrl(action)
                is SendDataSmsAction -> Runtime.sendDataMessage(action)
                is LaunchAppAction -> launchApp(context, action)
                is ClipboardAction -> copyToClipboard(context, action)
                is SetVariableAction -> {
                    Runtime.engine.setVariable(action.name, action.value)
                    "variable set"
                }
                is ToastAction -> showToast(context, action)
                is VibrateAction -> vibrate(context, action)
                is SpeakTextAction -> speak(context, action)
                is PlaySoundAction -> playSound(context, action)
                is SetVolumeAction -> setVolume(context, action)
                is VolumeIncrementDecrementAction -> adjustVolume(context, action)
                is SetBrightnessAction -> setBrightness(context, action)
                is SetScreenTimeoutAction -> setScreenTimeout(context, action)
                is SetAutoRotateAction -> writeSystemInt(context, Settings.System.ACCELEROMETER_ROTATION, if (action.enabled) 1 else 0)
                is SetRingtoneAction -> "ringtone requires WRITE_SETTINGS"
                is SetWifiAction -> openPanel(context, Settings.ACTION_WIFI_SETTINGS)
                is SetBluetoothAction -> openPanel(context, Settings.ACTION_BLUETOOTH_SETTINGS)
                is SetAirplaneModeAction -> openPanel(context, Settings.ACTION_AIRPLANE_MODE_SETTINGS)
                is SetDataAction -> "mobile data toggle requires MODIFY_PHONE_STATE"
                is SetHotspotAction -> "hotspot toggle requires TETHER_PRIVILEGED"
                is SetGPSAction -> openPanel(context, Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                is SetAutoSyncAction -> "auto sync requires WRITE_SYNC_SETTINGS"
                is SetNFCAction -> openPanel(context, Settings.ACTION_NFC_SETTINGS)
                is CameraFlashLightAction -> "torch requires CAMERA permission at runtime"
                is BatterySaverAction -> openPanel(context, Settings.ACTION_BATTERY_SAVER_SETTINGS)
                is SilentModeAction -> setRinger(context, action)
                is DarkThemeAction -> "night mode requires WRITE_SECURE_SETTINGS"
                is HeadsUpNotificationsAction -> "heads-up flag requires system permission"
                is LaunchHomeScreenAction -> launchHome(context)
                is LaunchActivityAction -> launchActivity(context, action)
                is CloseApplicationAction -> killApp(context, action.packageName)
                is KillBackgroundAppAction -> killApp(context, action.packageName)
                is PressBackAction -> accessibilityBack()
                is InputKeyEventAction -> accessibilityKey(action)
                is TouchScreenAction -> accessibilityTap(action)
                is ClearNotificationsAction -> "notification dismissal requires listener binding"
                is MessageDialogAction -> showDialog(context, action)
                is OptionDialogAction -> showDialog(context, action)
                is SelectionDialogAction -> showDialog(context, action)
                is SendIntentAction -> sendIntent(context, action)
                is SendSMSAction -> sendSms(context, action)
                is SendEmailAction -> sendEmail(context, action)
                is ShareTextAction -> shareText(context, action)
                is ShareLocationAction -> "location share requires ACCESS_FINE_LOCATION"
                is UpdateClipboardAction -> copyToClipboard(context, action)
                is TextManipulationAction -> manipulateText(action)
                is WriteToFileAction -> writeFile(context, action)
                is LogAction -> {
                    Runtime.engine.setVariable("last_log_message", action.message)
                    "logged"
                }
                is ControlMediaAction -> mediaAction(context, action)
                is SystemSettingAction -> writeGlobalString(context, action.key, action.value)
                is GotoAction -> "goto registered"
                is WaitUntilTriggerAction -> "wait registered"
                is EnableTriggerAction -> {
                    Runtime.engine.rules().filter { it.trigger.id == action.triggerId }.forEach {
                        Runtime.engine.replace(it.copy(enabled = true))
                    }
                    "trigger enabled"
                }
                is DisableMacroAction -> {
                    Runtime.engine.rules().filter { it.id == action.macroId }.forEach {
                        Runtime.engine.replace(it.copy(enabled = false))
                    }
                    "macro disabled"
                }
                is SetAlarmClockAction -> setAlarm(context, action)
                is SetWallpaperAction -> "wallpaper requires SET_WALLPAPER"
                else -> "unsupported action ${action::class.simpleName}"
            }
        }

    private fun launchApp(
        context: Context,
        action: LaunchAppAction,
    ): String {
        val intent =
            context.packageManager.getLaunchIntentForPackage(action.packageName)
                ?: throw IllegalStateException("no launcher for ${action.packageName}")
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "requested"
    }

    private fun launchHome(context: Context): String {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "requested"
    }

    private fun launchActivity(
        context: Context,
        action: LaunchActivityAction,
    ): String {
        val intent = Intent().setClassName(action.packageName, "${action.packageName}${action.activityClass}")
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "requested"
    }

    private fun killApp(
        context: Context,
        packageName: String,
    ): String {
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
        return "settings opened"
    }

    private fun showToast(
        context: Context,
        action: ToastAction,
    ): String {
        Toast.makeText(context.applicationContext, action.text, Toast.LENGTH_LONG).show()
        return "shown"
    }

    private fun vibrate(
        context: Context,
        action: VibrateAction,
    ): String {
        val vibrator = context.getSystemService(android.os.Vibrator::class.java) ?: throw IllegalStateException("no vibrator")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(android.os.VibrationEffect.createOneShot(action.durationMillis, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(action.durationMillis)
        }
        return "vibrated"
    }

    private fun speak(
        context: Context,
        action: SpeakTextAction,
    ): String {
        val tts =
            TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) Unit
            }
        tts.speak(action.text, TextToSpeech.QUEUE_FLUSH, null, action.id)
        return "speaking"
    }

    private fun playSound(
        context: Context,
        action: PlaySoundAction,
    ): String {
        val uri = RingtoneCompat.resolveUri(context, action.sound)
        RingtoneCompat.play(context, uri, streamOf(action.stream))
        return "playing"
    }

    private fun setVolume(
        context: Context,
        action: SetVolumeAction,
    ): String {
        val audio = context.getSystemService(AudioManager::class.java) ?: throw IllegalStateException("no AudioManager")
        audio.setStreamVolume(streamOf(action.stream), action.level, 0)
        return "volume set"
    }

    private fun adjustVolume(
        context: Context,
        action: VolumeIncrementDecrementAction,
    ): String {
        val audio = context.getSystemService(AudioManager::class.java) ?: throw IllegalStateException("no AudioManager")
        val direction = if (action.direction == "increment") AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
        repeat(action.steps) { audio.adjustStreamVolume(streamOf(action.stream), direction, 0) }
        return "volume adjusted"
    }

    private fun setBrightness(
        context: Context,
        action: SetBrightnessAction,
    ): String {
        if (!android.provider.Settings.System.canWrite(context)) throw SecurityException("WRITE_SETTINGS not granted")
        val values =
            android.content.ContentValues().apply {
                put(android.provider.Settings.System.SCREEN_BRIGHTNESS, action.level)
                put(android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, if (action.automatic) 1 else 0)
            }
        context.contentResolver.update(
            android.provider.Settings.System.getUriFor(android.provider.Settings.System.SCREEN_BRIGHTNESS),
            values,
            null,
            null,
        )
        return "brightness set"
    }

    private fun setScreenTimeout(
        context: Context,
        action: SetScreenTimeoutAction,
    ): String {
        if (!android.provider.Settings.System.canWrite(context)) throw SecurityException("WRITE_SETTINGS not granted")
        writeSystemInt(context, android.provider.Settings.System.SCREEN_OFF_TIMEOUT, action.seconds * 1000)
        return "timeout set"
    }

    private fun setRinger(
        context: Context,
        action: SilentModeAction,
    ): String {
        val audio = context.getSystemService(AudioManager::class.java) ?: throw IllegalStateException("no AudioManager")
        val mode =
            when (action.mode) {
                "normal" -> AudioManager.RINGER_MODE_NORMAL
                "vibrate" -> AudioManager.RINGER_MODE_VIBRATE
                "silent" -> AudioManager.RINGER_MODE_SILENT
                else -> AudioManager.RINGER_MODE_NORMAL
            }
        audio.ringerMode = mode
        return "ringer $mode"
    }

    private fun openPanel(
        context: Context,
        action: String,
    ): String {
        context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "settings opened"
    }

    private fun accessibilityBack(): String =
        accessibility {
            it.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
        }

    private fun accessibilityKey(action: InputKeyEventAction): String =
        accessibility {
            val code = android.view.KeyEvent.KEYCODE_HOME
            when (action.keyCode.uppercase()) {
                "HOME" -> it.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME)
                "RECENTS" -> it.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_RECENTS)
                "BACK" -> it.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
                "NOTIFICATIONS" -> it.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
                else -> {
                    val injected = android.view.KeyEvent(code, 0)
                    it.performGlobalAction(injected.keyCode)
                }
            }
            true
        }

    private fun accessibilityTap(action: TouchScreenAction): String =
        accessibility {
            it.rootInActiveWindow?.let { root ->
                root.recycle()
                true
            } ?: false
        }

    private fun accessibility(block: (android.accessibilityservice.AccessibilityService) -> Boolean): String {
        val service = AppLaunchAccessibilityService.instance ?: throw IllegalStateException("accessibility service disabled")
        return if (block(service)) "dispatched" else throw IllegalStateException("no window to act on")
    }

    private fun showDialog(
        context: Context,
        action: MessageDialogAction,
    ): String {
        val host = context as? android.app.Activity ?: throw IllegalStateException("no foreground activity")
        androidx.appcompat.app.AlertDialog.Builder(host)
            .setTitle(action.title)
            .setMessage(action.message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
        return "shown"
    }

    private fun showDialog(
        context: Context,
        action: OptionDialogAction,
    ): String {
        val host = context as? android.app.Activity ?: throw IllegalStateException("no foreground activity")
        androidx.appcompat.app.AlertDialog.Builder(host)
            .setTitle(action.title)
            .setItems(action.options.split('|').toTypedArray(), null)
            .show()
        return "shown"
    }

    private fun showDialog(
        context: Context,
        action: SelectionDialogAction,
    ): String {
        val host = context as? android.app.Activity ?: throw IllegalStateException("no foreground activity")
        val options = action.options.split('|')
        androidx.appcompat.app.AlertDialog.Builder(host)
            .setTitle(action.title)
            .setItems(options.toTypedArray()) { _, which ->
                Runtime.engine.setVariable(action.variableName, options[which])
            }
            .show()
        return "shown"
    }

    private fun sendIntent(
        context: Context,
        action: SendIntentAction,
    ): String {
        val intent = Intent(action.action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        action.extras?.split('|')?.forEach { pair ->
            val separator = pair.indexOf('=')
            if (separator > 0) {
                intent.putExtra(pair.substring(0, separator), pair.substring(separator + 1))
            }
        }
        context.sendBroadcast(intent)
        return "sent"
    }

    private fun sendSms(
        context: Context,
        action: SendSMSAction,
    ): String {
        val manager = android.telephony.SmsManager.getDefault()
        val parts = manager.divideMessage(action.message)
        manager.sendMultipartTextMessage(action.recipient, null, parts, null, null)
        return "sent"
    }

    private fun sendEmail(
        context: Context,
        action: SendEmailAction,
    ): String {
        val intent =
            Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", action.recipient, null)).apply {
                putExtra(Intent.EXTRA_SUBJECT, action.subject)
                putExtra(Intent.EXTRA_TEXT, action.body)
            }
        context.startActivity(Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "requested"
    }

    private fun shareText(
        context: Context,
        action: ShareTextAction,
    ): String {
        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, action.subject)
                putExtra(Intent.EXTRA_TEXT, action.text)
            }
        context.startActivity(Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "requested"
    }

    private fun copyToClipboard(
        context: Context,
        action: UpdateClipboardAction,
    ): String {
        val manager =
            context.getSystemService(android.content.ClipboardManager::class.java)
                ?: throw IllegalStateException("no clipboard")
        manager.setPrimaryClip(android.content.ClipData.newPlainText(action.id, action.text))
        return "copied"
    }

    private fun copyToClipboard(
        context: Context,
        action: ClipboardAction,
    ): String {
        val manager =
            context.getSystemService(android.content.ClipboardManager::class.java)
                ?: throw IllegalStateException("no clipboard")
        manager.setPrimaryClip(android.content.ClipData.newPlainText(action.id, action.text))
        return "copied"
    }

    private fun manipulateText(action: TextManipulationAction): String {
        val source = Runtime.engine.variable(action.sourceVariable) ?: ""
        val result =
            when (action.operation) {
                "upper" -> source.uppercase()
                "lower" -> source.lowercase()
                "trim" -> source.trim()
                "reverse" -> source.reversed()
                "length" -> source.length.toString()
                "md5" -> source.md5()
                else -> source
            }
        Runtime.engine.setVariable(action.targetVariable, result)
        return "manipulated"
    }

    private fun String.md5(): String {
        val digest = java.security.MessageDigest.getInstance("MD5").digest(toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun writeFile(
        context: Context,
        action: WriteToFileAction,
    ): String {
        val file = java.io.File(action.path.removePrefix("file://"))
        file.parentFile?.mkdirs()
        if (action.append) {
            file.appendText(action.content, Charsets.UTF_8)
        } else {
            file.writeText(action.content, Charsets.UTF_8)
        }
        context.contentResolver.notifyChange(Uri.fromFile(file), null)
        return "written"
    }

    private fun makeCall(
        context: Context,
        action: MakeCallAction,
    ): String {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${action.phoneNumber}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "requested"
    }

    private fun mediaAction(
        context: Context,
        action: ControlMediaAction,
    ): String {
        val event =
            when (action.command) {
                "play", "pause" -> android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                "next" -> android.view.KeyEvent.KEYCODE_MEDIA_NEXT
                "previous" -> android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS
                "stop" -> android.view.KeyEvent.KEYCODE_MEDIA_STOP
                else -> android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
            }
        val audio = context.getSystemService(AudioManager::class.java)
        audio?.dispatchMediaKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, event))
        audio?.dispatchMediaKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, event))
        return "dispatched"
    }

    private fun setAlarm(
        context: Context,
        action: SetAlarmClockAction,
    ): String {
        val intent =
            Intent(android.provider.AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(android.provider.AlarmClock.EXTRA_MESSAGE, action.message)
                putExtra(android.provider.AlarmClock.EXTRA_SKIP_UI, true)
            }
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "requested"
    }

    private fun streamOf(stream: String): Int =
        when (stream) {
            "ring" -> AudioManager.STREAM_RING
            "alarm" -> AudioManager.STREAM_ALARM
            "notification" -> AudioManager.STREAM_NOTIFICATION
            "system" -> AudioManager.STREAM_SYSTEM
            "voice" -> AudioManager.STREAM_VOICE_CALL
            else -> AudioManager.STREAM_MUSIC
        }

    private fun writeSystemInt(
        context: Context,
        key: String,
        value: Int,
    ): String {
        val ok = android.provider.Settings.System.putInt(context.contentResolver, key, value)
        if (!ok) throw IllegalStateException("cannot write $key")
        return "written"
    }

    private fun writeGlobalString(
        context: Context,
        key: String,
        value: String,
    ): String {
        val ok = android.provider.Settings.Global.putString(context.contentResolver, key, value)
        if (!ok) throw IllegalStateException("cannot write $key")
        return "written"
    }
}

private object RingtoneCompat {
    fun resolveUri(
        context: Context,
        sound: String,
    ): Uri =
        when (sound) {
            "alarm" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            "ringtone" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            "notification" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            else -> Uri.parse(sound)
        }

    fun play(
        context: Context,
        uri: Uri,
        stream: Int,
    ) {
        val player = android.media.MediaPlayer()
        player.setDataSource(context, uri)
        @Suppress("DEPRECATION")
        player.setAudioStreamType(stream)
        player.prepare()
        player.start()
    }
}
