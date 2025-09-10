package com.applivery.android.sdk.data.repository

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.applivery.android.sdk.data.repository.identifier.AndroidIdProvider
import com.applivery.android.sdk.data.repository.identifier.GsfIdProvider
import com.applivery.android.sdk.data.repository.identifier.InstallationIdProvider
import com.applivery.android.sdk.data.repository.identifier.MediaDrmIdProvider
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.repository.DeviceIdRepository

internal class DeviceIdRepositoryImpl(
    private val gsfIdProvider: GsfIdProvider,
    private val mediaDrmIdProvider: MediaDrmIdProvider,
    private val androidIdProvider: AndroidIdProvider,
    private val installationIdProvider: InstallationIdProvider
) : DeviceIdRepository {

    override suspend fun getDeviceId(): Either<DomainError, String> {
        return gsfIdProvider.getDeviceId()
            .orElse { mediaDrmIdProvider.getDeviceId() }
            .orElse { androidIdProvider.getDeviceId() }
            .orElse { installationIdProvider.getDeviceId() }
    }
}

private inline fun <E, A> Either<E, A>.orElse(default: (E) -> Either<E, A>): Either<E, A> {
    return when (this) {
        is Left -> default(value)
        is Right -> this
    }
}