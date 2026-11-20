package dev.niza.autodroid

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.BatteryManager
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import dev.niza.autodroid.engine.DeviceState

object AndroidDeviceStateProvider {
    fun current(context: Context): DeviceState {
        val appContext = context.applicationContext
        val battery =
            appContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val batteryLevel =
            battery?.let {
                val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                if (level >= 0 && scale > 0) (level * 100) / scale else null
            }
        val plugged =
            battery?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0
        val audioManager = ContextCompat.getSystemService(appContext, AudioManager::class.java)
        val powerManager = ContextCompat.getSystemService(appContext, PowerManager::class.java)
        val wifiManager =
            appContext.getSystemService(Context.WIFI_SERVICE) as? android.net.wifi.WifiManager
        val bluetoothManager =
            appContext.getSystemService(Context.BLUETOOTH_SERVICE) as? android.bluetooth.BluetoothManager
        val connectivityManager =
            ContextCompat.getSystemService(appContext, android.net.ConnectivityManager::class.java)
        val airplane =
            Settings.Global.getInt(appContext.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1
        val headphones =
            audioManager?.isWiredHeadsetOn == true ||
                audioManager?.isBluetoothScoOn == true ||
                bluetoothManager?.adapter?.getProfileConnectionState(android.bluetooth.BluetoothProfile.HEADSET) ==
                android.bluetooth.BluetoothProfile.STATE_CONNECTED

        return DeviceState(
            batteryLevel = batteryLevel,
            batterySaver = powerManager?.isPowerSaveMode ?: false,
            bluetoothEnabled = bluetoothManager?.adapter?.isEnabled == true,
            cellConnectionType = cellularType(connectivityManager),
            calendar = java.util.Calendar.getInstance(),
            deviceLocked = powerManager?.isInteractive?.not() ?: false,
            externalPowerConnected = plugged != 0,
            gpsEnabled =
                Settings.Secure.getInt(
                    appContext.contentResolver,
                    Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF,
                ) != Settings.Secure.LOCATION_MODE_OFF,
            headphonesConnected = headphones,
            networkType =
                connectivityManager?.activeNetwork?.let { network ->
                    connectivityManager.getNetworkCapabilities(network)
                        ?.let { capabilities ->
                            when {
                                capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
                                capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) -> "cellular"
                                capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_VPN) -> "vpn"
                                else -> "none"
                            }
                        }
                } ?: "none",
            screenOn = powerManager?.isInteractive == true,
            silentModeEnabled = audioManager?.ringerMode == AudioManager.RINGER_MODE_SILENT,
            enabledTriggerIds = emptySet(),
            wifiConnected =
                wifiManager?.isWifiEnabled == true &&
                    connectivityManager?.activeNetwork
                        ?.let { connectivityManager.getNetworkCapabilities(it) }
                        ?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) == true,
            airplaneModeEnabled = airplane,
        )
    }

    private fun cellularType(connectivityManager: android.net.ConnectivityManager?): String =
        connectivityManager?.allNetworks
            ?.firstOrNull { network ->
                connectivityManager.getNetworkCapabilities(network)
                    ?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) == true
            }
            ?.let { "cellular" } ?: "none"
}
