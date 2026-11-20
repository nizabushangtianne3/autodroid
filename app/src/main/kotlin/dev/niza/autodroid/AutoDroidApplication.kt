package dev.niza.autodroid

import android.app.Application
import android.content.Context

class AutoDroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appInstance = this
        Runtime.deviceStateProvider = AndroidDeviceStateProvider::current
    }

    companion object {
        private lateinit var appInstance: AutoDroidApplication

        fun context(): Context = appInstance.applicationContext
    }
}
