package com.applivery.android.sdk

import com.applivery.android.sdk.configuration.Configuration
import com.applivery.android.sdk.di.AppliveryDiContext
import com.applivery.android.sdk.di.AppliveryKoinComponent
import com.applivery.android.sdk.di.Properties
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.asResult
import com.applivery.android.sdk.domain.model.BindUser
import com.applivery.android.sdk.domain.model.ShakeFeedbackBehavior
import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.domain.usecases.BindUserUseCase
import com.applivery.android.sdk.domain.usecases.CheckUpdatesUseCase
import com.applivery.android.sdk.domain.usecases.GetAppConfigUseCase
import com.applivery.android.sdk.domain.usecases.GetUserUseCase
import com.applivery.android.sdk.domain.usecases.IsUpToDateUseCase
import com.applivery.android.sdk.domain.usecases.UnbindUserUseCase
import com.applivery.android.sdk.feedback.ScreenshotFeedbackChecker
import com.applivery.android.sdk.feedback.ShakeFeedbackChecker
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

    fun init(
        appToken: String,
        tenant: String? = null,
        configuration: Configuration = Configuration.Empty
    ) {
        require(appToken.isNotBlank()) { "Empty appToken received" }

        get<DomainLogger>().startingSdk(appToken, tenant)

        AppliveryDiContext.koinApp.apply {
            properties(
                mapOf(
                    Properties.AppToken to appToken,
                    Properties.AppTenant to tenant.orEmpty(),
                    Properties.Configuration to configuration,
                )
            )
        }

        /*Lets fetch the config to check if configuration is correct*/
        mainScope.launch { get<GetAppConfigUseCase>().invoke() }

        /*Initialize SDK dependent components*/
        get<UpdatesBackgroundChecker>().start()
        get<ShakeFeedbackChecker>().start()
        get<ScreenshotFeedbackChecker>().start()
    }

    override fun isUpToDate(callback: IsUpToDateCallback) {
        mainScope.launch { callback.onResponse(isUpToDate()) }
    }

    override suspend fun isUpToDate(): Boolean {
        return get<IsUpToDateUseCase>().invoke().getOrNull() == true
    }

    override fun checkForUpdates(forceUpdate: Boolean) {
        mainScope.launch { get<CheckUpdatesUseCase>().invoke(forceUpdate) }
    }

    override fun setCheckForUpdatesBackground(enable: Boolean) {
        get<UpdatesBackgroundChecker>().enableCheckForUpdatesBackground(enable)
    }

    override fun getCheckForUpdatesBackground(): Boolean {
        return get<UpdatesBackgroundChecker>().isEnabled
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

    override fun enableShakeFeedback(behavior: ShakeFeedbackBehavior) {
        get<ShakeFeedbackChecker>().enable(behavior)
    }

    override fun disableShakeFeedback() {
        get<ShakeFeedbackChecker>().disable()
    }

    override fun enableScreenshotFeedback() {
        get<ScreenshotFeedbackChecker>().enable(true)
    }

    override fun disableScreenshotFeedback() {
        get<ScreenshotFeedbackChecker>().enable(false)
    }
}

private var sInstance: Applivery? = null

fun Applivery.Companion.getInstance(): Applivery {
    return requireNotNull(sInstance) {
        "Applivery SDK not initialized. Did you forget to call Applivery.init() ?"
    }
}

fun Applivery.Companion.init(
    appToken: String,
    tenant: String? = null,
    configuration: Configuration = Configuration.Empty
) {
    sInstance = AppliverySdk().apply { init(appToken, tenant, configuration) }
}