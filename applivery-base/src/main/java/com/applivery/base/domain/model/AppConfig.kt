package com.applivery.base.domain.model

data class AppConfig(
    val forceAuth: Boolean,
    val forceUpdate: Boolean,
    val lastBuildId: String,
    val lastBuildVersion: String,
    val minVersion: String,
    val ota: Boolean
)