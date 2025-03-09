package com.applivery.android.sdk.data.models

import android.graphics.Bitmap
import android.util.Base64
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.AuthenticationUri
import com.applivery.android.sdk.domain.model.BindUser
import com.applivery.android.sdk.domain.model.Device
import com.applivery.android.sdk.domain.model.DeviceInfo
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.Feedback
import com.applivery.android.sdk.domain.model.InternalError
import com.applivery.android.sdk.domain.model.LimitExceededError
import com.applivery.android.sdk.domain.model.Os
import com.applivery.android.sdk.domain.model.PackageInfo
import com.applivery.android.sdk.domain.model.SubscriptionError
import com.applivery.android.sdk.domain.model.UnauthorizedError
import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.domain.model.UserType
import com.applivery.android.sdk.feedback.FeedbackType
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

private const val ApiDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

internal fun <T> Either<ApiError, ApiResponseSchema<T>>.toDomain(): Either<DomainError, T> {
    return mapLeft { it.toDomain() }.flatMap { it.data?.right() ?: InternalError().left() }
}

internal fun ApiError.toDomain(): DomainError {
    return when (this) {
        is ApiError.LimitExceeded -> LimitExceededError()
        is ApiError.Subscription -> SubscriptionError()
        is ApiError.Unauthorized -> UnauthorizedError()
        is ApiError.IO,
        is ApiError.Internal -> InternalError()
    }
}

internal fun AppConfigApi.toDomain(): AppConfig {
    return AppConfig(
        forceAuth = sdk.android.forceAuth ?: false,
        forceUpdate = sdk.android.forceUpdate ?: false,
        lastBuildId = sdk.android.lastBuildId.orEmpty(),
        lastBuildVersion = sdk.android.lastBuildVersion?.toIntOrNull() ?: -1,
        minVersion = sdk.android.minVersion?.toIntOrNull() ?: -1,
        ota = sdk.android.ota ?: false
    )
}

internal fun AuthenticationUriApi.toDomain(): AuthenticationUri? {
    return AuthenticationUri(uri = uri ?: return null)
}

internal fun BindUser.toApi(): BindUserApi {
    return BindUserApi(
        email = email,
        firstName = firstName,
        lastName = lastName,
        tags = tags.takeIf { it.isNotEmpty() }
    )
}

internal fun UserApi.toDomain(): User {
    return User(
        id = id.orEmpty(),
        email = email,
        firstName = firstName,
        lastName = lastName,
        fullName = fullName,
        type = type?.toDomain(),
        createdAt = runCatching {
            SimpleDateFormat(ApiDateFormat, Locale.getDefault()).parse(createdAt.orEmpty())
        }.getOrNull()
    )
}

internal fun UserTypeApi.toDomain(): UserType {
    return when (this) {
        UserTypeApi.User -> UserType.User
        UserTypeApi.Employee -> UserType.Employee
    }
}

internal fun PackageInfo.toApi(): PackageInfoApi {
    return PackageInfoApi(
        name = appName,
        version = versionCode.toString(),
        versionName = versionName
    )
}

internal fun Os.toApi(): OsApi {
    return OsApi(
        name = name,
        version = version
    )
}

internal fun Device.toApi(): DeviceApi {
    return DeviceApi(
        battery = batteryStatus.percentage.toString(),
        batteryStatus = batteryStatus.isCharging,
        diskFree = availableDiskPercentage.toString(),
        model = model,
        network = networkType,
        orientation = orientation,
        ramTotal = ramStatus.total.toString(),
        ramUsed = ramStatus.used.toString(),
        resolution = "${screenResolution.width}x${screenResolution.height}",
        type = deviceType,
        vendor = vendor
    )
}

internal fun DeviceInfo.toApi(): DeviceInfoApi {
    return DeviceInfoApi(
        device = device.toApi(),
        os = os.toApi()
    )
}

internal fun FeedbackType.toApi(): String {
    return when (this) {
        FeedbackType.Feedback -> "feedback"
        FeedbackType.Bug -> "bug"
    }
}

internal fun Feedback.toApi(): FeedbackApi {
    val screenshot = when (this) {
        is Feedback.Screenshot -> "data:image/png;base64,${screenshot.asB64()}"
        is Feedback.Simple,
        is Feedback.Video -> null
    }
    val hasVideo = this is Feedback.Video
    return FeedbackApi(
        deviceInfo = deviceInfo.toApi(),
        message = message,
        packageInfo = packageInfo.toApi(),
        screenshot = screenshot,
        hasVideo = hasVideo,
        type = type.toApi(),
        email = email
    )
}

@Suppress("MagicNumber")
private fun Bitmap.asB64(): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}
