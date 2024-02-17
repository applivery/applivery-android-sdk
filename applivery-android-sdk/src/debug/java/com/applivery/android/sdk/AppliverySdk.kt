package com.applivery.android.sdk

import android.app.Application
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import com.applivery.android.sdk.di.AppliveryDiContext
import com.applivery.android.sdk.di.Properties
import com.applivery.android.sdk.di.networkModules
import com.applivery.android.sdk.updates.UpdatesLifecycleObserver
import org.koin.android.ext.koin.androidContext

internal class AppliverySdk : Applivery {

    private val currentActivityProvider: CurrentActivityProvider = CurrentActivityProvider()
    private val processLifecycle get() = ProcessLifecycleOwner.get().lifecycle
    private val updatesLifecycleObserver = UpdatesLifecycleObserver()

    override fun init(context: Context, appToken: String) {
        require(context.applicationContext is Application) { "Application context expected" }
        require(appToken.isNotBlank()) { "Empty appToken received" }

        val app = context.applicationContext as Application
        AppliveryDiContext.koinApp.apply {
            androidContext(app)
            // TODO: modules
            modules(networkModules)
            // TODO: map properties from build config correctly
            properties(
                mapOf(
                    Properties.AppToken to appToken,
                    Properties.ApiUrl to "https://sdk-api.applivery.io"
                )
            )
        }

        app.registerActivityLifecycleCallbacks(currentActivityProvider)
    }

    override fun checkForUpdatesInBackground(check: Boolean) {
        processLifecycle.removeObserver(updatesLifecycleObserver)
        if (check) {
            processLifecycle.addObserver(updatesLifecycleObserver)
        }
    }

    override fun checkForUpdates() {
        TODO("Not yet implemented")
    }
}