package com.applivery.android.sdk.configuration

import com.applivery.android.sdk.domain.model.BuildDownloadAction
import kotlin.time.Duration

data class Configuration(
    val postponeDurations: List<Duration> = emptyList(),
    val enforceAuthentication: Boolean = false,
    val downloadAction: BuildDownloadAction = BuildDownloadAction.IMMEDIATE
) {
    companion object {
        val Empty = Configuration()
    }
}
