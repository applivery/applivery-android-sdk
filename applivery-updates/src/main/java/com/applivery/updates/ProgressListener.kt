package com.applivery.updates

import com.applivery.updates.domain.DownloadInfo
import kotlin.properties.Delegates

// TODO remove this after update to a new view
object ProgressListener {

    var downloadInfo: DownloadInfo by Delegates.observable(DownloadInfo()) { _, _, newValue ->
        onUpdate?.invoke(newValue)
    }

    var onUpdate: ((DownloadInfo) -> Unit)? = null
    var onFinish: (() -> Unit?)? = null

    fun clearListener() {
        onUpdate = null
        onFinish = null
    }
}