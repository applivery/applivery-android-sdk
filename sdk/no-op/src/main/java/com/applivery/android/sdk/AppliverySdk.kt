package com.applivery.android.sdk

import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.updates.IsUpToDateCallback
import com.applivery.android.sdk.user.BindUserCallback
import com.applivery.android.sdk.user.GetUserCallback

internal class AppliverySdk : Applivery {

    override fun isUpToDate(callback: IsUpToDateCallback) {
        callback.onResponse(isUpToDate = true)
    }

    override suspend fun isUpToDate(): Boolean = true

    override fun checkForUpdates() = Unit

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

    override fun enableShakeFeedback() = Unit

    override fun disableShakeFeedback() = Unit

    override fun enableScreenshotFeedback() = Unit

    override fun disableScreenshotFeedback() = Unit
}

class AppliveryNoOpError : Throwable() {
    override val message: String = "This is a no op instance of the Applivery SDK"
}

private var sInstance: Applivery = AppliverySdk()

fun Applivery.Companion.getInstance(): Applivery = sInstance

@Suppress("UNUSED_PARAMETER")
fun Applivery.Companion.init(appToken: String) = Unit

@Suppress("UNUSED_PARAMETER")
fun Applivery.Companion.init(appToken: String, tenant: String) = Unit
