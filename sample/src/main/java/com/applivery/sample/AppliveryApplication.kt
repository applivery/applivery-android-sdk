package com.applivery.sample

import android.app.Application
import com.applivery.applvsdklib.Applivery

class AppliveryApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    Applivery.init(this, BuildConfig.APP_ID, BuildConfig.ACCOUNT_API_KEY, false)
    Applivery.setUpdateCheckingInterval(0)
  }
}