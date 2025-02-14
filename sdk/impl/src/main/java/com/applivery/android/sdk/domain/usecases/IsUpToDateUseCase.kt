package com.applivery.android.sdk.domain.usecases

import arrow.core.Either
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.repository.AppliveryRepository

internal interface IsUpToDateUseCase {

    suspend operator fun invoke(): Either<DomainError, Boolean>
}

internal class IsUpToDate(
    private val repository: AppliveryRepository,
    private val hostAppPackageInfoProvider: HostAppPackageInfoProvider
) : IsUpToDateUseCase {

    override suspend fun invoke(): Either<DomainError, Boolean> {
        val currentAppVersion = hostAppPackageInfoProvider.packageInfo.versionCode
        return repository.getConfig().map { currentAppVersion >= it.lastBuildVersion }
    }
}