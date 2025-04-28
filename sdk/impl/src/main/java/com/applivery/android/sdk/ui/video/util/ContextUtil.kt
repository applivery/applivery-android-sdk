package com.applivery.android.sdk.ui.video.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * The environment in which Compose is hosted may not be an activity unconditionally.
 * Gets the current activity that is open from various kinds of contexts such as Fragment, Dialog, etc.
 */
internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Activity not found. Unknown error.")
}
