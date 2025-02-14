package com.applivery.android.sdk.ui

import android.content.Context
import android.content.pm.PackageManager

internal fun Context.check(permission: String): Boolean {
    return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}