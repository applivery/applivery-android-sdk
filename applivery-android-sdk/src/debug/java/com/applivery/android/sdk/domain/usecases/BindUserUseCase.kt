package com.applivery.android.sdk.domain.usecases

import arrow.core.Either
import com.applivery.android.sdk.domain.model.BindUser
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.repository.AppliveryRepository

internal interface BindUserUseCase {

    suspend operator fun invoke(bindUser: BindUser): Either<DomainError, Unit>
}

internal class BindUser(
    private val repository: AppliveryRepository
) : BindUserUseCase {

    override suspend fun invoke(bindUser: BindUser): Either<DomainError, Unit> {
        return repository.bindUser(bindUser)
    }
}