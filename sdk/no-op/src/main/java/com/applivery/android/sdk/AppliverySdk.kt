package com.applivery.android.sdk

import com.applivery.android.sdk.configuration.Configuration
import com.applivery.android.sdk.domain.model.CachedAppUpdate
import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.updates.DownloadLastUpdateCallback
import com.applivery.android.sdk.updates.IsUpToDateCallback
import com.applivery.android.sdk.user.BindUserCallback
import com.applivery.android.sdk.user.GetUserCallback

internal class AppliverySdk : Applivery {

    override fun isUpToDate(callback: IsUpToDateCallback) {
        callback.onResponse(isUpToDate = true)
    }

    override suspend fun isUpToDate(): Boolean = true

    override fun checkForUpdates(forceUpdate: Boolean) = Unit

    override fun setCheckForUpdatesBackground(enable: Boolean) = Unit

    override fun getCheckForUpdatesBackground(): Boolean = false

    override fun update() = Unit

    override fun bindUser(
        email: String,
        firstName: String?,
        lastName: String?,
        tags: List<String>,
        callback: BindUserCallback
    ) {
        callback.onError(AppliveryNoOpError().message)
    }

    override suspend fun bindUser(
        email: String,
        firstName: String?,
        lastName: String?,
        tags: List<String>
    ): Result<Unit> = Result.failure(AppliveryNoOpError())

    override fun unbindUser() = Unit

    override fun getUser(callback: GetUserCallback) {
        callback.onError(AppliveryNoOpError().message)
    }

    override suspend fun getUser(): Result<User> = Result.failure(AppliveryNoOpError())

    override fun feedbackEvent() = Unit

    override fun enableScreenshotFeedback() = Unit

    override fun disableScreenshotFeedback() = Unit

    override fun downloadLastUpdate(callback: DownloadLastUpdateCallback) {
        callback.onError(AppliveryNoOpError())
    }

    override suspend fun downloadLastUpdate(): Result<CachedAppUpdate> =
        Result.failure(AppliveryNoOpError())

    override fun enableDownloadLastUpdateBackground(callback: DownloadLastUpdateCallback) = Unit

    override fun disableDownloadLastUpdateBackground() = Unit
}

class AppliveryNoOpError : Throwable() {
    override val message: String = "This is a no op instance of the Applivery SDK"
}

private var sInstance: Applivery = AppliverySdk()

fun Applivery.Companion.getInstance(): Applivery = sInstance

@Suppress("UNUSED_PARAMETER")
fun Applivery.Companion.init(
    appToken: String,
    tenant: String? = null,
    configuration: Configuration = Configuration.Empty
) = Unit

@Suppress("UNUSED_PARAMETER")
fun Applivery.Companion.start(
    appToken: String,
    tenant: String? = null,
    configuration: Configuration = Configuration.Empty
) = Unit
