package com.applivery.android.sdk.updates

interface IsUpToDateCallback {
    fun onResponse(isUpToDate: Boolean)
}