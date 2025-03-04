package com.applivery.sample

import android.app.Application
import com.applivery.android.sdk.Applivery
import com.applivery.android.sdk.getInstance
import com.applivery.android.sdk.init

class AppliveryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appPreferences = AppPreferences(applicationContext)
        Applivery.init(BuildConfig.APPLIVERY_APP_TOKEN)
    }

    companion object {
        lateinit var appPreferences: AppPreferences
    }
}
