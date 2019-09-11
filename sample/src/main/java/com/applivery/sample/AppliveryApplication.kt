package com.applivery.sample

import android.app.Application
import com.applivery.applvsdklib.Applivery

class AppliveryApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    appPreferences = AppPreferences(applicationContext)
    Applivery.init(this, BuildConfig.APPLIVERY_APP_TOKEN, false)
    Applivery.setCheckForUpdatesBackground(appPreferences.checkForUpdatesBackground)
  }

  companion object {
    lateinit var appPreferences: AppPreferences
  }
}