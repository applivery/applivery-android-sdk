package com.applivery.android.sdk.domain

import android.util.Log
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.UpdateType
import com.applivery.android.sdk.feedback.MediaPermissionGrantStatus
import com.applivery.android.sdk.updates.UpdateInstallStep

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

    fun startingSdk(appToken: String, tenant: String?) {
        logger.log("Starting Applivery SDK with appToken: $appToken and tenant: $tenant")
    }

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
        logger.log("Image decoding failed with the following error: ${error.stackTraceToString()}")
    }

    fun accelerometerNotAvailable() {
        logger.log("Accelerometer not found. Shake feedback won't work for this device")
    }

    fun updateType(updateType: UpdateType) {
        when (updateType) {
            UpdateType.ForceUpdate -> logger.log("Force update available")
            UpdateType.SuggestedUpdate -> logger.log("Suggested update available")
            UpdateType.None -> logger.log("No update available")
        }
    }

    fun errorCapturingScreenFromHostApp(error: DomainError) {
        logger.log("Error capturing screen from host app: ${error.log()}")
    }

    fun installBuildProgress(step: UpdateInstallStep) {
        logger.log("Installing build in progress: $step")
    }

    fun errorInstallingBuild(error: DomainError) {
        logger.log("Error installing build: ${error.log()}")
    }

    fun noOverlayPermission() {
        logger.log(
            """
            android.permission.SYSTEM_ALERT_WINDOW not granted. Video reporting floating button can 
            not be shown until the permission is granted. (https://developer.android.com/reference/android/Manifest.permission#SYSTEM_ALERT_WINDOW)
            """.trimIndent()
        )
    }

    fun onShakeDetectedAlreadyRecording() {
        logger.log("Shake detected while already recording")
    }

    fun videoReportingError(error: DomainError) {
        logger.log("Unexpected error while recording screen: ${error.log()}")
    }

    private fun DomainError.log(): String {
        return "${this::class.java.simpleName}: $message"
    }
}
