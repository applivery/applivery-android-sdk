package com.applivery.android.sdk

import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.updates.IsUpToDateCallback
import com.applivery.android.sdk.user.BindUserCallback
import com.applivery.android.sdk.user.GetUserCallback

interface Applivery {

    fun init(appToken: String)

    fun init(appToken: String, tenant: String)

    fun isUpToDate(callback: IsUpToDateCallback)

    suspend fun isUpToDate(): Boolean

    fun checkForUpdates()

    fun setCheckForUpdatesBackground(enable: Boolean)

    fun update()

    fun bindUser(
        email: String,
        firstName: String? = null,
        lastName: String? = null,
        tags: List<String> = emptyList(),
        callback: BindUserCallback
    )

    suspend fun bindUser(
        email: String,
        firstName: String? = null,
        lastName: String? = null,
        tags: List<String> = emptyList()
    ): Result<Unit>

    fun unbindUser()

    fun getUser(callback: GetUserCallback)

    suspend fun getUser(): Result<User>

    fun enableShakeFeedback()

    fun disableShakeFeedback()

    fun enableScreenshotFeedback()

    fun disableScreenshotFeedback()

    companion object {
        private var sInstance: Applivery? = null

        fun getInstance(): Applivery {
            return requireNotNull(sInstance) { "Applivery SDK not initialized. Did you forget to call Applivery.init?" }
        }

        fun init(appToken: String) {
            sInstance = AppliverySdk().apply { init(appToken) }
        }

        fun init(appToken: String, tenant: String) {
            sInstance = AppliverySdk().apply { init(appToken, tenant) }
        }
    }
}