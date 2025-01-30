package com.applivery.android.sdk

import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.updates.IsUpToDateCallback
import com.applivery.android.sdk.user.BindUserCallback
import com.applivery.android.sdk.user.GetUserCallback

internal class AppliverySdk : Applivery {

    override fun init(appToken: String) = Unit

    override fun init(appToken: String, tenant: String) = Unit

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

private var sInstance: Applivery? = null

fun Applivery.Companion.getInstance(): Applivery {
    return requireNotNull(sInstance) {
        "Applivery SDK not initialized. Did you forget to call Applivery.init() ?"
    }
}

fun Applivery.Companion.init(appToken: String) {
    sInstance = AppliverySdk().apply { init(appToken) }
}

fun Applivery.Companion.init(appToken: String, tenant: String) {
    sInstance = AppliverySdk().apply { init(appToken, tenant) }
}
