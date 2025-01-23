package com.applivery.android.sdk

import android.content.Context
import androidx.startup.Initializer
import com.applivery.android.sdk.di.AppliveryDiContext
import com.applivery.android.sdk.di.AppliveryKoinComponent
import com.applivery.android.sdk.di.Properties
import com.applivery.android.sdk.di.appModules
import com.applivery.android.sdk.di.dataModules
import com.applivery.android.sdk.di.domainModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.get

internal class AppliveryDiInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        AppliveryDiContext.koinApp.apply {
            androidContext(context)
            modules(
                dataModules,
                domainModules,
                appModules
            )
            properties(
                mapOf(
                    Properties.ApiUrl to BuildConfig.ApiBaseUrl,
                    Properties.DownloadUrl to BuildConfig.DownloadApiUrl
                )
            )
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}

internal class AppliveryInitializer : Initializer<Unit>, AppliveryKoinComponent {

    override fun create(context: Context) {
        get<HostActivityProvider>().start()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(AppliveryDiInitializer::class.java)
    }
}
