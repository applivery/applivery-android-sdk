package com.applivery.android.sdk.feedback

import android.Manifest
import android.content.Context
import com.applivery.android.sdk.ui.check

internal enum class MediaPermissionGrantStatus {
    Granted,
    PartiallyGranted,
    Denied
}

internal object MediaPermissionGrantStatusChecker {

    fun of(context: Context): MediaPermissionGrantStatus {
        return if (context.check(Manifest.permission.READ_MEDIA_IMAGES)) {
            MediaPermissionGrantStatus.Granted
        } else if (context.check(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
            MediaPermissionGrantStatus.PartiallyGranted
        } else if (context.check(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            MediaPermissionGrantStatus.Granted
        } else {
            MediaPermissionGrantStatus.Denied
        }
    }
}