package com.applivery.android.sdk

import com.applivery.android.sdk.di.AppliveryDiContext
import com.applivery.android.sdk.di.AppliveryKoinComponent
import com.applivery.android.sdk.di.Properties
import com.applivery.android.sdk.domain.usecases.CheckUpdatesUseCase
import com.applivery.android.sdk.domain.usecases.GetAppConfigUseCase
import com.applivery.android.sdk.domain.usecases.IsUpToDateUseCase
import com.applivery.android.sdk.updates.IsUpToDateCallback
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.get

internal class AppliverySdk : Applivery, AppliveryKoinComponent {

    private val mainScope = MainScope()

    override fun init(appToken: String) {
        initialize(appToken, tenant = null)
    }

    override fun init(appToken: String, tenant: String) {
        require(tenant.isNotBlank()) { "Empty tenant received" }
        initialize(appToken, tenant)
    }

    override fun isUpToDate(callback: IsUpToDateCallback) {
        mainScope.launch { callback.onResponse(isUpToDate()) }
    }

    override suspend fun isUpToDate(): Boolean {
        return get<IsUpToDateUseCase>().invoke().getOrNull() == true
    }

    override fun checkForUpdates() {
        mainScope.launch { get<CheckUpdatesUseCase>().invoke() }
    }

    private fun initialize(appToken: String, tenant: String?) {
        require(appToken.isNotBlank()) { "Empty appToken received" }

        AppliveryDiContext.koinApp.apply {
            properties(
                mapOf(
                    Properties.AppToken to appToken,
                    Properties.AppTenant to tenant.orEmpty()
                )
            )
        }

        /*Lets fetch the config to check if configuration is correct*/
        mainScope.launch { get<GetAppConfigUseCase>().invoke() }
    }
}