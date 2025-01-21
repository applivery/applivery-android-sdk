package com.applivery.android.sdk.domain

import android.util.Log
import com.applivery.android.sdk.feedback.MediaPermissionGrantStatus

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

internal class DomainLogger(
    private val logger: Logger
) {

    fun logMediaPermissionGrantStatus(status: MediaPermissionGrantStatus) {
        when (status) {
            MediaPermissionGrantStatus.Granted -> Unit
            MediaPermissionGrantStatus.PartiallyGranted,
            MediaPermissionGrantStatus.Denied -> logger.log(
                """
                    Media permission partially granted, screenshot feedback will not work.
                    Make sure the app has full media access in order to be able to detect screenshots 
                    (https://developer.android.com/about/versions/14/changes/partial-photo-video-access)
                """.trimIndent()
            )
        }
    }

    fun noActivityFoundForFeedbackView() {
        logger.log("No Activity found in foreground to launch feedback view")
    }

    fun imageDecodingFailed(error: Throwable) {
        logger.log("Image decoding failed with the following error:")
        error.printStackTrace()
    }
}
