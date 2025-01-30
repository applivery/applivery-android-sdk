package com.applivery.android.sdk.domain.model

internal data class AppConfig(
    val forceAuth: Boolean,
    val forceUpdate: Boolean,
    val lastBuildId: String,
    val lastBuildVersion: Int,
    val minVersion: Int,
    val ota: Boolean
)
