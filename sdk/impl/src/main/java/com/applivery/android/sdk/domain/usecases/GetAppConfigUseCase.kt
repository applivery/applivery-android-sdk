package com.applivery.android.sdk.domain.usecases

import arrow.core.Either
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.repository.AppliveryRepository

internal interface GetAppConfigUseCase {

    suspend operator fun invoke(): Either<DomainError, AppConfig>
}

internal class GetAppConfig(private val repository: AppliveryRepository) : GetAppConfigUseCase {

    override suspend fun invoke(): Either<DomainError, AppConfig> {
        return repository.getConfig()
    }
}