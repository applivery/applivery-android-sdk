package com.applivery.android.sdk

import android.content.Context

interface Applivery {

    fun init(context: Context, appToken: String)

    fun checkForUpdatesInBackground(check: Boolean)

    fun checkForUpdates()

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