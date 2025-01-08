package com.applivery.android.sdk.domain.model

data class PackageInfo(
    val appName : String,
    val packageName: String,
    val versionCode: Long,
    val versionName: String,
    val minSdkVersion: Int,
    val targetSdkVersion: Int
)