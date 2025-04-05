package com.applivery.android.sdk.configuration

import kotlin.time.Duration

data class Configuration(
    val postponeDurations: List<Duration>
) {
    companion object {
        val Empty = Configuration(
            postponeDurations = emptyList()
        )
    }
}