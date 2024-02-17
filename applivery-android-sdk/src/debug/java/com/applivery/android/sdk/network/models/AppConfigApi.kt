package com.applivery.android.sdk.network.models

import com.google.gson.annotations.SerializedName

data class AppConfigApi(
    @SerializedName("description") val description: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("oss") val oss: List<String>?,
    @SerializedName("sdk") val sdk: SdkEntityApi,
    @SerializedName("slug") val slug: String?
)

data class SdkEntityApi(
    @SerializedName("android") val android: AndroidEntityApi
)

data class AndroidEntityApi(
    @SerializedName("forceAuth") val forceAuth: Boolean?,
    @SerializedName("forceUpdate") val forceUpdate: Boolean?,
    @SerializedName("lastBuildId") val lastBuildId: String?,
    @SerializedName("lastBuildVersion") val lastBuildVersion: String?,
    @SerializedName("minVersion") val minVersion: String?,
    @SerializedName("ota") val ota: Boolean?
)
