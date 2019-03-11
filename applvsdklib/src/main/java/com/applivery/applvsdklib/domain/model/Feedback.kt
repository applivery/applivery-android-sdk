package com.applivery.applvsdklib.domain.model

data class Feedback(
  val deviceInfo: DeviceInfo,
  val message: String?,
  val packageInfo: PackageInfo,
  val screenshot: String?,
  val type: String
)

data class DeviceInfo(
  val device: Device,
  val os: Os
)

data class Device(
  val battery: String,
  val batteryStatus: Boolean,
  val diskFree: String,
  val model: String,
  val network: String,
  val orientation: String,
  val ramTotal: String,
  val ramUsed: String,
  val resolution: String,
  val type: String,
  val vendor: String
)

data class Os(
  val name: String,
  val version: String
)

data class PackageInfo(
  val name: String,
  val version: Int,
  val versionName: String
)