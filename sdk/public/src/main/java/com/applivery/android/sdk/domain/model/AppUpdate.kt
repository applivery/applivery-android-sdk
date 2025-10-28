package com.applivery.android.sdk.domain.model

data class AppUpdate(
    val buildId: String,
    val buildVersion: Int
)

interface CachedAppUpdate {
    val appUpdate: AppUpdate
    fun install()
}
