package com.applivery.android.sdk.ui.video.pip

import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Rational
import androidx.media3.ui.PlayerView
import com.applivery.android.sdk.ui.video.util.findActivity

/**
 * Enables PIP mode for the current activity.
 *
 * @param context Activity context.
 * @param defaultPlayerView Current video player controller.
 */
@Suppress("DEPRECATION")
internal fun enterPIPMode(context: Context, defaultPlayerView: PlayerView) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    ) {
        defaultPlayerView.useController = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = PictureInPictureParams.Builder()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                params
                    .setTitle("Video Player")
                    .setAspectRatio(Rational(16, 9))
                    .setSeamlessResizeEnabled(true)
            }

            context.findActivity().enterPictureInPictureMode(params.build())
        } else {
            context.findActivity().enterPictureInPictureMode()
        }
    }
}

/**
 * Check that the current activity is in PIP mode.
 *
 * @return `true` if the activity is in pip mode. (PIP mode is not supported in the version below Android N, so `false` is returned unconditionally.)
 */
internal fun Context.isActivityStatePipMode(): Boolean {
    val currentActivity = findActivity()

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        currentActivity.isInPictureInPictureMode
    } else {
        false
    }
}
