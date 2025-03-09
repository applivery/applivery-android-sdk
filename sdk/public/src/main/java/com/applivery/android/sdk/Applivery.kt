package com.applivery.android.sdk

import com.applivery.android.sdk.domain.model.ShakeFeedbackBehavior
import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.updates.IsUpToDateCallback
import com.applivery.android.sdk.user.BindUserCallback
import com.applivery.android.sdk.user.GetUserCallback

interface Applivery {

    fun isUpToDate(callback: IsUpToDateCallback)

    suspend fun isUpToDate(): Boolean

    fun checkForUpdates()

    fun setCheckForUpdatesBackground(enable: Boolean)

    fun getCheckForUpdatesBackground(): Boolean

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

    fun enableShakeFeedback(behavior: ShakeFeedbackBehavior = ShakeFeedbackBehavior.Normal)

    fun disableShakeFeedback()

    fun enableScreenshotFeedback()

    fun disableScreenshotFeedback()

    companion object
}
