package com.applivery.android.sdk.domain.model

data class AppConfig(
    val forceAuth: Boolean,
    val forceUpdate: Boolean,
    val lastBuildId: String,
    val lastBuildVersion: Int,
    val minVersion: Int,
    val ota: Boolean
)
