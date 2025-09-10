package com.applivery.android.sdk.data.repository.identifier

import arrow.core.Either
import com.applivery.android.sdk.domain.model.DomainError

internal interface DeviceIdProvider {

    suspend fun getDeviceId(): Either<DomainError, DeviceId>
}

internal class DeviceIdNotAvailableError(type: String, cause: Throwable?) : DomainError(
    "Device ID $type not available: ${cause?.message}",
)
