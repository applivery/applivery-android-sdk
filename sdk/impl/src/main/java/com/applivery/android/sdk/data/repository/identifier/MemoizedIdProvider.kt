package com.applivery.android.sdk.data.repository.identifier

import arrow.core.Either
import com.applivery.android.sdk.domain.model.DomainError
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal abstract class MemoizedIdProvider : DeviceIdProvider {

    private var memoized: Either<DomainError, DeviceId>? = null

    private val mutex = Mutex()

    final override suspend fun getDeviceId(): Either<DomainError, DeviceId> {
        return mutex.withLock { memoized ?: getActualDeviceId().also { memoized = it } }
    }

    abstract suspend fun getActualDeviceId(): Either<DomainError, DeviceId>
}
