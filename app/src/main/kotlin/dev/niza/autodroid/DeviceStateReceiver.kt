package dev.niza.autodroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.core.content.ContextCompat
import dev.niza.autodroid.engine.AirplaneModeEvent
import dev.niza.autodroid.engine.AppLifecycleEvent
import dev.niza.autodroid.engine.BatteryLevelEvent
import dev.niza.autodroid.engine.BluetoothEvent
import dev.niza.autodroid.engine.PowerSaveModeEvent
import dev.niza.autodroid.engine.ScreenOnOffEvent

object DeviceStateReceiver {
    private const val ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON"
    private const val ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF"
    private const val ACTION_AIRPLANE_MODE = "android.intent.action.AIRPLANE_MODE"
    private const val ACTION_BLUETOOTH_STATE = "android.bluetooth.adapter.action.STATE_CHANGED"
    private const val ACTION_POWER_SAVE_MODE = "android.os.action.POWER_SAVE_MODE_CHANGED"

    private var screenReceiver: BroadcastReceiver? = null
    private var batteryReceiver: BroadcastReceiver? = null
    private var airplaneReceiver: BroadcastReceiver? = null
    private var bluetoothReceiver: BroadcastReceiver? = null
    private var packageReceiver: BroadcastReceiver? = null
    private var powerSaveReceiver: BroadcastReceiver? = null
    private var appContext: Context? = null

    fun registeredChannelsForTest(): Set<String> =
        buildSet {
            if (screenReceiver != null) add("screen")
            if (batteryReceiver != null) add("battery")
            if (airplaneReceiver != null) add("airplane")
            if (bluetoothReceiver != null) add("bluetooth")
            if (packageReceiver != null) add("package")
            if (powerSaveReceiver != null) add("power_save")
        }

    fun resetForTest() {
        screenReceiver = null
        batteryReceiver = null
        airplaneReceiver = null
        bluetoothReceiver = null
        packageReceiver = null
        powerSaveReceiver = null
        appContext = null
    }

    fun refresh(context: Context) {
        val appContext = context.applicationContext
        this.appContext = appContext
        val rules = readRules(appContext)
        val needScreen = rules.any { it.enabled && it.trigger is dev.niza.autodroid.engine.ScreenOnOffTrigger }
        val needBattery =
            rules.any {
                it.enabled && it.trigger is dev.niza.autodroid.engine.BatteryLevelTrigger
            }
        val needAirplane = rules.any { it.enabled && it.trigger is dev.niza.autodroid.engine.AirplaneModeTrigger }
        val needBluetooth = rules.any { it.enabled && it.trigger is dev.niza.autodroid.engine.BluetoothTrigger }
        val needPackage = rules.any { it.enabled && it.trigger is dev.niza.autodroid.engine.AppLifecycleTrigger }
        val needPowerSave = rules.any { it.enabled && it.trigger is dev.niza.autodroid.engine.PowerSaveModeTrigger }

        if (needScreen && screenReceiver == null) registerScreen(appContext)
        if (needBattery && batteryReceiver == null) registerBattery(appContext)
        if (needAirplane && airplaneReceiver == null) registerAirplane(appContext)
        if (needBluetooth && bluetoothReceiver == null) registerBluetooth(appContext)
        if (needPackage && packageReceiver == null) registerPackageLifecycle(appContext)
        if (needPowerSave && powerSaveReceiver == null) registerPowerSaveMode(appContext)
    }

    private fun readRules(context: Context) =
        runCatching {
            context.openFileInput("rules.json").use {
                it.bufferedReader().readText()
            }
        }.getOrNull()?.let { dev.niza.autodroid.engine.RuleStore().decode(it) } ?: emptyList()

    private fun registerScreen(context: Context) {
        val receiver =
            object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context,
                    intent: Intent,
                ) {
                    val on = intent.action == Intent.ACTION_SCREEN_ON
                    Runtime.initialize(context)
                    Runtime.loadRules(context)
                    Runtime.dispatchAndReport(ScreenOnOffEvent(on))
                }
            }
        val filter =
            IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
        screenReceiver = receiver
    }

    private fun registerBattery(context: Context) {
        val receiver =
            object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context,
                    intent: Intent,
                ) {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    if (level < 0) return
                    Runtime.initialize(context)
                    Runtime.loadRules(context)
                    Runtime.dispatchAndReport(BatteryLevelEvent(level))
                }
            }
        val filter = IntentFilter().apply { addAction(Intent.ACTION_BATTERY_CHANGED) }
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
        batteryReceiver = receiver
    }

    private fun registerAirplane(context: Context) {
        val receiver =
            object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context,
                    intent: Intent,
                ) {
                    val on = intent.getBooleanExtra("state", false)
                    Runtime.initialize(context)
                    Runtime.loadRules(context)
                    Runtime.dispatchAndReport(AirplaneModeEvent(on))
                }
            }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(ACTION_AIRPLANE_MODE),
            ContextCompat.RECEIVER_EXPORTED,
        )
        airplaneReceiver = receiver
    }

    private fun registerBluetooth(context: Context) {
        val receiver =
            object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context,
                    intent: Intent,
                ) {
                    val state = intent.getIntExtra(android.bluetooth.BluetoothAdapter.EXTRA_STATE, -1)
                    if (state == -1) return
                    val on = state == android.bluetooth.BluetoothAdapter.STATE_ON
                    Runtime.initialize(context)
                    Runtime.loadRules(context)
                    Runtime.dispatchAndReport(BluetoothEvent(on))
                }
            }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(ACTION_BLUETOOTH_STATE),
            ContextCompat.RECEIVER_EXPORTED,
        )
        bluetoothReceiver = receiver
    }

    private fun registerPackageLifecycle(context: Context) {
        val receiver =
            object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context,
                    intent: Intent,
                ) {
                    val packageName = intent.data?.schemeSpecificPart ?: return
                    val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                    if (replacing) return
                    val added = intent.action == Intent.ACTION_PACKAGE_ADDED
                    Runtime.initialize(context)
                    Runtime.loadRules(context)
                    Runtime.dispatchAndReport(AppLifecycleEvent(packageName, added))
                }
            }
        val filter =
            IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addDataScheme("package")
            }
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
        packageReceiver = receiver
    }

    private fun registerPowerSaveMode(context: Context) {
        val receiver =
            object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context,
                    intent: Intent,
                ) {
                    val powerManager = context.getSystemService(Context.POWER_SERVICE) as? android.os.PowerManager
                        ?: return
                    Runtime.initialize(context)
                    Runtime.loadRules(context)
                    Runtime.dispatchAndReport(PowerSaveModeEvent(powerManager.isPowerSaveMode))
                }
            }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(ACTION_POWER_SAVE_MODE),
            ContextCompat.RECEIVER_EXPORTED,
        )
        powerSaveReceiver = receiver
    }
}
