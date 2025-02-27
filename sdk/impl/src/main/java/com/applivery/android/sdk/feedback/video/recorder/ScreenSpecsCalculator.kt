package com.applivery.android.sdk.feedback.video.recorder

import android.content.Context
import android.content.res.Configuration
import android.media.CamcorderProfile
import android.util.DisplayMetrics
import android.view.WindowManager

internal class ScreenSpecsCalculator(
    private val context: Context
) {
    val screenSpecs: ScreenSpecs
        get() {
            val displayMetrics = DisplayMetrics()
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getRealMetrics(displayMetrics)
            val displayWidth = displayMetrics.widthPixels
            val displayHeight = displayMetrics.heightPixels
            val displayDensity = displayMetrics.densityDpi
            val configuration = context.resources.configuration
            val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            val camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
            val cameraWidth = camcorderProfile?.videoFrameWidth ?: -1
            val cameraHeight = camcorderProfile?.videoFrameHeight ?: -1
            val cameraFrameRate = camcorderProfile?.videoFrameRate ?: 30

            return calculateRecordingInfo(
                displayWidth,
                displayHeight,
                displayDensity,
                isLandscape,
                cameraWidth,
                cameraHeight,
                cameraFrameRate,
                100
            )
        }

    data class ScreenSpecs(
        val width: Int,
        val height: Int,
        val frameRate: Int,
        val density: Int
    )

    companion object {
        fun calculateRecordingInfo(
            displayWidth: Int,
            displayHeight: Int,
            displayDensity: Int,
            isLandscapeDevice: Boolean,
            cameraWidth: Int,
            cameraHeight: Int,
            cameraFrameRate: Int,
            sizePercentage: Int
        ): ScreenSpecs {
            // Scale the display size before any maximum size calculations.
            var realDisplayWidth = displayWidth
            var realDisplayHeight = displayHeight
            realDisplayWidth = realDisplayWidth * sizePercentage / 100
            realDisplayHeight = realDisplayHeight * sizePercentage / 100

            if (cameraWidth == -1 && cameraHeight == -1) {
                // No cameras. Fall back to the display size.
                return ScreenSpecs(realDisplayWidth, realDisplayHeight, cameraFrameRate, displayDensity)
            }

            var frameWidth = if (isLandscapeDevice) cameraWidth else cameraHeight
            var frameHeight = if (isLandscapeDevice) cameraHeight else cameraWidth
            if (frameWidth >= realDisplayWidth && frameHeight >= realDisplayHeight) {
                // Frame can hold the entire display. Use exact values.
                return ScreenSpecs(realDisplayWidth, realDisplayHeight, cameraFrameRate, displayDensity)
            }

            // Calculate new width or height to preserve aspect ratio.
            if (isLandscapeDevice) {
                frameWidth = realDisplayWidth * frameHeight / realDisplayHeight
            } else {
                frameHeight = realDisplayHeight * frameWidth / realDisplayWidth
            }
            return ScreenSpecs(frameWidth, frameHeight, cameraFrameRate, displayDensity)
        }
    }
}
