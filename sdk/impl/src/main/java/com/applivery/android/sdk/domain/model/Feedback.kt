package com.applivery.android.sdk.domain.model

import android.graphics.Bitmap
import android.net.Uri
import com.applivery.android.sdk.feedback.FeedbackType

internal sealed interface Feedback {
    val deviceInfo: DeviceInfo
    val packageInfo: PackageInfo
    val message: String?
    val type: FeedbackType
    val email: String?

    data class Simple(
        override val deviceInfo: DeviceInfo,
        override val packageInfo: PackageInfo,
        override val message: String?,
        override val type: FeedbackType,
        override val email: String?,
    ) : Feedback {

        fun withScreenshot(screenshot: Bitmap) = Screenshot(
            deviceInfo = deviceInfo,
            packageInfo = packageInfo,
            message = message,
            type = type,
            email = email,
            screenshot = screenshot
        )

        fun withVideo(uri: Uri) = Video(
            deviceInfo = deviceInfo,
            packageInfo = packageInfo,
            message = message,
            type = type,
            email = email,
            uri = uri
        )
    }

    data class Screenshot(
        override val deviceInfo: DeviceInfo,
        override val packageInfo: PackageInfo,
        override val message: String?,
        override val type: FeedbackType,
        override val email: String?,
        val screenshot: Bitmap,
    ) : Feedback

    data class Video(
        override val deviceInfo: DeviceInfo,
        override val packageInfo: PackageInfo,
        override val message: String?,
        override val type: FeedbackType,
        override val email: String?,
        val uri: Uri,
    ) : Feedback
}

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