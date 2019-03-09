package com.applivery.applvsdklib.network.api.model

import com.applivery.applvsdklib.domain.model.Device
import com.applivery.applvsdklib.domain.model.DeviceInfo
import com.applivery.applvsdklib.domain.model.Feedback
import com.applivery.applvsdklib.domain.model.Os
import com.applivery.applvsdklib.domain.model.PackageInfo
import com.google.gson.annotations.SerializedName


data class FeedbackEntity(
  @SerializedName("deviceInfo")
  val deviceInfo: DeviceInfoEntity?,
  @SerializedName("message")
  val message: String?,
  @SerializedName("packageInfo")
  val packageInfo: PackageInfoEntity?,
  @SerializedName("screenshot")
  val screenshot: String?,
  @SerializedName("type")
  val type: String?
) {
  companion object {
    fun fromFeedback(feedback: Feedback) = FeedbackEntity(
      DeviceInfoEntity.fromDeviceInfo(feedback.deviceInfo),
      feedback.message,
      PackageInfoEntity.fromPackageInfo(feedback.packageInfo),
      "data:image/jpeg;base64," + feedback.screenshot,
      feedback.type
    )
  }
}

data class DeviceInfoEntity(
  @SerializedName("device")
  val device: DeviceEntity?,
  @SerializedName("os")
  val os: OsEntity?
) {

  companion object {
    fun fromDeviceInfo(deviceInfo: DeviceInfo) = DeviceInfoEntity(
      DeviceEntity.fromDevice(deviceInfo.device),
      OsEntity.fromOs(deviceInfo.os)
    )
  }
}

data class DeviceEntity(
  @SerializedName("battery")
  val battery: String?,
  @SerializedName("batteryStatus")
  val batteryStatus: Boolean?,
  @SerializedName("diskFree")
  val diskFree: String?,
  @SerializedName("model")
  val model: String?,
  @SerializedName("network")
  val network: String?,
  @SerializedName("orientation")
  val orientation: String?,
  @SerializedName("ramTotal")
  val ramTotal: String?,
  @SerializedName("ramUsed")
  val ramUsed: String?,
  @SerializedName("resolution")
  val resolution: String?,
  @SerializedName("type")
  val type: String?,
  @SerializedName("vendor")
  val vendor: String?
) {

  companion object {
    fun fromDevice(device: Device) = DeviceEntity(
      device.battery,
      device.batteryStatus,
      device.diskFree,
      device.model,
      device.network,
      device.orientation,
      device.ramTotal,
      device.ramUsed,
      device.resolution,
      device.type,
      device.vendor
    )
  }
}

data class OsEntity(
  @SerializedName("name")
  val name: String?,
  @SerializedName("version")
  val version: String?
) {

  companion object {
    fun fromOs(os: Os) = OsEntity(
      os.name.toLowerCase(),
      os.version
    )
  }
}

data class PackageInfoEntity(
  @SerializedName("name")
  val name: String?,
  @SerializedName("version")
  val version: String?,
  @SerializedName("versionName")
  val versionName: String?
) {

  companion object {
    fun fromPackageInfo(packageInfo: PackageInfo) = PackageInfoEntity(
      packageInfo.name,
      packageInfo.version.toString(),
      packageInfo.versionName
    )
  }
}