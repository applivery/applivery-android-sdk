package com.applivery.android.sdk.updates

fun interface IsUpToDateCallback {
    fun onResponse(isUpToDate: Boolean)
}