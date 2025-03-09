package com.applivery.android.sdk.feedback.video.bubble

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager
import androidx.compose.ui.unit.IntSize
import androidx.core.content.getSystemService

internal fun getScreenSizeWithoutInsets(context: Context): IntSize {
    val windowManager = context.getSystemService<WindowManager>() ?: return IntSize.Zero
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            val windowMetrics = windowManager.maximumWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.systemBars()
            )
            IntSize(
                windowMetrics.bounds.width() - (insets.left + insets.right),
                windowMetrics.bounds.height() - (insets.top + insets.bottom)
            )
        }

        else -> {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            IntSize(
                displayMetrics.widthPixels,
                displayMetrics.heightPixels
            )
        }
    }
}
