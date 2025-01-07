package com.applivery.android.sdk.domain

import android.util.Log

internal interface Logger {

    fun log(message: String)
}

internal class AndroidLogger : Logger {

    override fun log(message: String) {
        Log.d(TAG, message)
    }

    companion object {
        private const val TAG = "Applivery SDK"
    }
}

