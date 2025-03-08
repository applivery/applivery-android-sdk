package com.applivery.android.sdk.feedback.video

import androidx.activity.result.ActivityResult

internal fun interface MediaProjectionCallback {

    fun onResult(result: ActivityResult)

    companion object {
        operator fun invoke(onResult: (ActivityResult) -> Unit): MediaProjectionCallback {
            return MediaProjectionCallback { result -> onResult(result) }
        }
    }
}

internal object MediaProjectionCallbacks : MediaProjectionCallback {

    private val callbacks = mutableListOf<MediaProjectionCallback>()

    override fun onResult(result: ActivityResult) {
        callbacks.forEach { it.onResult(result) }
        callbacks.clear()
    }

    fun add(callback: MediaProjectionCallback) {
        callbacks.add(callback)
    }

    fun remove(callback: MediaProjectionCallback) {
        callbacks.remove(callback)
    }
}
