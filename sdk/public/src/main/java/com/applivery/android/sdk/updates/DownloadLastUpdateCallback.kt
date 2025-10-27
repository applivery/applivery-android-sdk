package com.applivery.android.sdk.updates

import com.applivery.android.sdk.domain.model.CachedAppUpdate

interface DownloadLastUpdateCallback {
    fun onSuccess(update: CachedAppUpdate)
    fun onError(error: Throwable)
}