/*
 * Copyright (c) 2020 Applivery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.applivery.data.request

import com.applivery.base.domain.model.Device
import com.applivery.base.domain.model.DeviceInfo
import com.applivery.base.domain.model.Feedback
import com.applivery.base.domain.model.Os
import com.applivery.base.domain.model.PackageInfo
import com.google.gson.annotations.SerializedName

data class FeedbackRequest(
    @SerializedName("deviceInfo") val deviceInfo: DeviceInfoEntity?,
    @SerializedName("message") val message: String?,
    @SerializedName("packageInfo") val packageInfo: PackageInfoEntity?,
    @SerializedName("screenshot") val screenshot: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("email") val email: String?
) {
    companion object {
        fun fromFeedback(feedback: Feedback) = FeedbackRequest(
            DeviceInfoEntity.fromDeviceInfo(feedback.deviceInfo),
            feedback.message,
            PackageInfoEntity.fromPackageInfo(feedback.packageInfo),
            getScreenshotData(feedback.screenshot),
            feedback.type,
            feedback.email
        )

        private fun getScreenshotData(screenshot: String?): String? {
            return if (screenshot != null) {
                "data:image/png;base64,$screenshot"
            } else {
                null
            }
        }
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
            device.battery.toString(),
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
