package com.applivery.android.sdk.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable

internal inline fun <reified T : Parcelable> Intent.parcelable(name: String): T? {
    return extras?.parcelable(name)
}

internal inline fun <reified T : Parcelable> Bundle.parcelable(name: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(name, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(name)
    }
}

internal fun AlertDialog.Builder.configureIf(
    condition: Boolean,
    configure: AlertDialog.Builder.() -> AlertDialog.Builder
): AlertDialog.Builder {
    return if (condition) configure() else this
}