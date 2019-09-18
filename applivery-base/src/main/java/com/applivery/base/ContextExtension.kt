package com.applivery.base

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

 fun Context.getLifecycle(): Lifecycle? {
    var context = this
    while (true) {
        when (context) {
            is LifecycleOwner -> return context.lifecycle
            !is ContextWrapper -> return null
            else -> context = context.baseContext
        }
    }
}

internal inline fun <reified T : Any> Context.requireSystemService(): T {
    return checkNotNull(getSystemService<T>()) { "System service of type ${T::class.java} was not found." }
}

internal inline fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}