package com.applivery.android.sdk.domain.usecases

import arrow.core.Either
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.repository.AppliveryRepository

internal interface UnbindUserUseCase {

    suspend operator fun invoke(): Either<DomainError, Unit>
}

internal class UnbindUser(
    private val repository: AppliveryRepository
) : UnbindUserUseCase {

    override suspend fun invoke(): Either<DomainError, Unit> {
        return repository.unbindUser()
    }
}