package com.applivery.sample

import android.app.Application
import com.applivery.applvsdklib.Applivery

class AppliveryApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    Applivery.init(this, BuildConfig.APPLIVERY_APP_TOKEN, false)
    Applivery.setCheckForUpdatesBackground(false)
  }
}