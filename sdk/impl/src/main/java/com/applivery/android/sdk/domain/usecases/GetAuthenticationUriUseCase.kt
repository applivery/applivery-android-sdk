package com.applivery.android.sdk.domain.usecases

import arrow.core.Either
import com.applivery.android.sdk.domain.model.AuthenticationUri
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.repository.AppliveryRepository

internal interface GetAuthenticationUriUseCase {

    suspend operator fun invoke(): Either<DomainError, AuthenticationUri>
}

internal class GetAuthenticationUri(
    private val repository: AppliveryRepository
) : GetAuthenticationUriUseCase {

    override suspend fun invoke(): Either<DomainError, AuthenticationUri> {
        return repository.getAuthenticationUri()
    }
}