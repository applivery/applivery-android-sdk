package com.applivery.android.sdk.feedback.video.recorder

import android.content.Context
import android.content.res.Configuration
import android.media.CamcorderProfile
import android.util.DisplayMetrics
import android.view.WindowManager
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.VideoDimensionNotSpecified
import kotlin.math.roundToInt

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
            val cameraWidth = camcorderProfile?.videoFrameWidth ?: VideoDimensionNotSpecified
            val cameraHeight = camcorderProfile?.videoFrameHeight ?: VideoDimensionNotSpecified

            return calculateRecordingInfo(
                displayWidth = displayWidth,
                displayHeight = displayHeight,
                displayDensity = displayDensity,
                isLandscapeDevice = isLandscape,
                cameraWidth = cameraWidth,
                cameraHeight = cameraHeight,
            )
        }

    data class ScreenSpecs(
        val width: Int,
        val height: Int,
        val density: Int
    )

    companion object {

        private const val DefaultSizeFactor = 1f

        @Suppress("LongParameterList")
        fun calculateRecordingInfo(
            displayWidth: Int,
            displayHeight: Int,
            displayDensity: Int,
            isLandscapeDevice: Boolean,
            cameraWidth: Int,
            cameraHeight: Int,
            sizeFactor: Float = DefaultSizeFactor
        ): ScreenSpecs {
            // Scale the display size before any maximum size calculations.
            var realDisplayWidth = displayWidth
            var realDisplayHeight = displayHeight
            realDisplayWidth *= sizeFactor.roundToInt()
            realDisplayHeight *= sizeFactor.roundToInt()

            if (cameraWidth == VideoDimensionNotSpecified && cameraHeight == VideoDimensionNotSpecified) {
                // No cameras. Fall back to the display size.
                return ScreenSpecs(
                    realDisplayWidth,
                    realDisplayHeight,
                    displayDensity
                )
            }

            var frameWidth = if (isLandscapeDevice) cameraWidth else cameraHeight
            var frameHeight = if (isLandscapeDevice) cameraHeight else cameraWidth
            if (frameWidth >= realDisplayWidth && frameHeight >= realDisplayHeight) {
                // Frame can hold the entire display. Use exact values.
                return ScreenSpecs(
                    realDisplayWidth,
                    realDisplayHeight,
                    displayDensity
                )
            }

            // Calculate new width or height to preserve aspect ratio.
            if (isLandscapeDevice) {
                frameWidth = realDisplayWidth * frameHeight / realDisplayHeight
            } else {
                frameHeight = realDisplayHeight * frameWidth / realDisplayWidth
            }
            return ScreenSpecs(frameWidth, frameHeight, displayDensity)
        }
    }
}
