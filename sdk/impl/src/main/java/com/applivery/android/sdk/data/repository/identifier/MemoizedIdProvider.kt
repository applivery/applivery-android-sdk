package com.applivery.android.sdk.data.repository.identifier

import arrow.core.Either
import com.applivery.android.sdk.domain.model.DomainError

internal abstract class MemoizedIdProvider : DeviceIdProvider {

    private var memoized: Either<DomainError, String>? = null

    final override suspend fun getDeviceId(): Either<DomainError, String> {
        return memoized ?: getActualDeviceId().also { memoized = it }
    }

    abstract suspend fun getActualDeviceId(): Either<DomainError, String>

}