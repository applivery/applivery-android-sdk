package com.applivery.android.sdk

import com.applivery.android.sdk.di.AppliveryDiContext
import com.applivery.android.sdk.di.AppliveryKoinComponent
import com.applivery.android.sdk.di.Properties
import com.applivery.android.sdk.domain.asResult
import com.applivery.android.sdk.domain.model.BindUser
import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.domain.usecases.BindUserUseCase
import com.applivery.android.sdk.domain.usecases.CheckUpdatesUseCase
import com.applivery.android.sdk.domain.usecases.GetAppConfigUseCase
import com.applivery.android.sdk.domain.usecases.GetUserUseCase
import com.applivery.android.sdk.domain.usecases.IsUpToDateUseCase
import com.applivery.android.sdk.domain.usecases.UnbindUserUseCase
import com.applivery.android.sdk.updates.DownloadBuildService
import com.applivery.android.sdk.updates.IsUpToDateCallback
import com.applivery.android.sdk.updates.UpdatesBackgroundChecker
import com.applivery.android.sdk.user.BindUserCallback
import com.applivery.android.sdk.user.GetUserCallback
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

    override fun setCheckForUpdatesBackground(enable: Boolean) {
        get<UpdatesBackgroundChecker>().enableCheckForUpdatesBackground(enable)
    }

    override fun update() {
        DownloadBuildService.start(context = get())
    }

    override fun bindUser(
        email: String,
        firstName: String?,
        lastName: String?,
        tags: List<String>,
        callback: BindUserCallback
    ) {
        mainScope.launch {
            bindUser(email, firstName, lastName, tags).fold(
                onSuccess = { callback.onSuccess() },
                onFailure = { callback.onError(it.message.orEmpty()) }
            )
        }
    }

    override suspend fun bindUser(
        email: String,
        firstName: String?,
        lastName: String?,
        tags: List<String>
    ): Result<Unit> {
        val bindUser = BindUser(email, firstName, lastName, tags)
        return get<BindUserUseCase>().invoke(bindUser).asResult()
    }

    override fun unbindUser() {
        mainScope.launch { get<UnbindUserUseCase>().invoke() }
    }

    override fun getUser(callback: GetUserCallback) {
        mainScope.launch {
            getUser().fold(
                onSuccess = { callback.onSuccess(it) },
                onFailure = { callback.onError(it.message.orEmpty()) }
            )
        }
    }

    override suspend fun getUser(): Result<User> {
        return get<GetUserUseCase>().invoke().asResult()
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

        /*Initialize SDK dependent components*/
        get<UpdatesBackgroundChecker>().start()
    }
}