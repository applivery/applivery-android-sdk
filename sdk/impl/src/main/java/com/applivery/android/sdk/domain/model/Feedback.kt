package com.applivery.android.sdk.domain.model

import com.applivery.android.sdk.feedback.FeedbackType

internal data class Feedback(
    val deviceInfo: DeviceInfo,
    val packageInfo: PackageInfo,
    val message: String?,
    val screenshotBase64: String?,
    val type: FeedbackType,
    val email: String?
)

internal data class DeviceInfo(
    val device: Device,
    val os: Os
)

internal data class Device(
    val batteryStatus: BatteryStatus,
    val availableDiskPercentage: Long,
    val model: String,
    val networkType: String,
    val orientation: String,
    val ramStatus: RamStatus,
    val screenResolution: ScreenResolution,
    val deviceType: String,
    val vendor: String
)

internal data class Os(
    val name: String,
    val version: String
)

internal data class BatteryStatus(
    val percentage: Int,
    val isCharging: Boolean,
)

internal data class ScreenResolution(
    val width: Int,
    val height: Int
) {
    companion object {
        fun empty() = ScreenResolution(0, 0)
    }
}

internal data class RamStatus(
    val total: Long,
    val used: Long
) {
    companion object {
        fun empty() = RamStatus(0, 0)
    }
}