package com.applivery.android.sdk.data.models

import com.google.gson.annotations.SerializedName

data class AppConfigApi(
    @SerializedName("description") val description: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("oss") val oss: List<String>?,
    @SerializedName("sdk") val sdk: SdkApi,
    @SerializedName("slug") val slug: String?
)

data class SdkApi(
    @SerializedName("android") val android: AndroidApi
)

data class AndroidApi(
    @SerializedName("forceAuth") val forceAuth: Boolean?,
    @SerializedName("forceUpdate") val forceUpdate: Boolean?,
    @SerializedName("lastBuildId") val lastBuildId: String?,
    @SerializedName("lastBuildVersion") val lastBuildVersion: String?,
    @SerializedName("minVersion") val minVersion: String?,
    @SerializedName("ota") val ota: Boolean?
)
