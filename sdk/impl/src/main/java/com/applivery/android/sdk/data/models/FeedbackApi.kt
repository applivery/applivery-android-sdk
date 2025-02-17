package com.applivery.android.sdk.data.models

import com.google.gson.annotations.SerializedName

internal class FeedbackApi(
    @SerializedName("deviceInfo") val deviceInfo: DeviceInfoApi?,
    @SerializedName("message") val message: String?,
    @SerializedName("packageInfo") val packageInfo: PackageInfoApi?,
    @SerializedName("screenshot") val screenshot: String?,
    @SerializedName("hasVideo") val hasVideo: Boolean?,
    @SerializedName("type") val type: String?,
    @SerializedName("email") val email: String?
)

internal data class DeviceInfoApi(
    @SerializedName("device") val device: DeviceApi?,
    @SerializedName("os") val os: OsApi?
)

internal data class DeviceApi(
    @SerializedName("battery") val battery: String?,
    @SerializedName("batteryStatus") val batteryStatus: Boolean?,
    @SerializedName("diskFree") val diskFree: String?,
    @SerializedName("model") val model: String?,
    @SerializedName("network") val network: String?,
    @SerializedName("orientation") val orientation: String?,
    @SerializedName("ramTotal") val ramTotal: String?,
    @SerializedName("ramUsed") val ramUsed: String?,
    @SerializedName("resolution") val resolution: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("vendor") val vendor: String?
)

internal data class OsApi(
    @SerializedName("name") val name: String?,
    @SerializedName("version") val version: String?
)

internal data class PackageInfoApi(
    @SerializedName("name") val name: String?,
    @SerializedName("version") val version: String?,
    @SerializedName("versionName") val versionName: String?
)
