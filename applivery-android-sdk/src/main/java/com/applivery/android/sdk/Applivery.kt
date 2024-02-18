package com.applivery.android.sdk

import android.content.Context
import com.applivery.android.sdk.updates.IsUpToDateCallback

interface Applivery {

    fun init(context: Context, appToken: String)

    fun checkForUpdatesInBackground(check: Boolean)

    fun checkForUpdates()

    fun isUpToDate(callback: IsUpToDateCallback)

    companion object {
        private var sInstance: Applivery? = null

        fun getInstance(): Applivery {
            return requireNotNull(sInstance) { "Applivery SDK not initialized. Did you forget to call Applivery.init?" }
        }

        fun init(context: Context, appToken: String) {
            sInstance = AppliverySdk().apply { init(context, appToken) }
        }
    }
}