package com.applivery.sample

import android.app.Application
import com.applivery.android.sdk.Applivery
import com.applivery.android.sdk.configuration.Configuration
import com.applivery.android.sdk.init
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class AppliveryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val configuration = Configuration(
            postponeDurations = listOf(
                2.hours,
                30.minutes,
                5.minutes,
            ),
            enforceAuthentication = false
        )
        Applivery.init(BuildConfig.APPLIVERY_APP_TOKEN, configuration = configuration)
    }
}
