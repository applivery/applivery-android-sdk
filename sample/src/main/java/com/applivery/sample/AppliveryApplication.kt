package com.applivery.sample

import android.app.Application
import android.util.Log
import com.applivery.applvsdklib.Applivery
import com.applivery.base.util.AppliveryLog

class AppliveryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appPreferences = AppPreferences(applicationContext)

        AppliveryLog.setLogLevel(Log.DEBUG)
        Applivery.init(this, BuildConfig.APPLIVERY_APP_TOKEN, false)
        Applivery.setCheckForUpdatesBackground(appPreferences.checkForUpdatesBackground)
    }

    companion object {
        lateinit var appPreferences: AppPreferences
    }
}