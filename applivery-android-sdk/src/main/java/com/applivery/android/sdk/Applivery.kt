package com.applivery.android.sdk

import com.applivery.android.sdk.updates.IsUpToDateCallback

interface Applivery {

    fun init(appToken: String)

    fun init(appToken: String, tenant: String)

    fun isUpToDate(callback: IsUpToDateCallback)

    suspend fun isUpToDate(): Boolean

    fun checkForUpdates()

    fun setCheckForUpdatesBackground(enable: Boolean)

    fun update()

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