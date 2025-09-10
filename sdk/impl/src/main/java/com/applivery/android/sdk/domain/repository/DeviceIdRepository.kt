package com.applivery.android.sdk.domain.repository

import arrow.core.Either
import com.applivery.android.sdk.domain.model.DomainError

internal interface DeviceIdRepository {

    suspend fun getDeviceId(): Either<DomainError, String>
}