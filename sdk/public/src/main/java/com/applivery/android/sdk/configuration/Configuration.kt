package com.applivery.android.sdk.configuration

import kotlin.time.Duration

data class Configuration(
    val postponeDurations: List<Duration> = emptyList(),
    val enforceAuthentication: Boolean = false
) {
    companion object {
        val Empty = Configuration()
    }
}
