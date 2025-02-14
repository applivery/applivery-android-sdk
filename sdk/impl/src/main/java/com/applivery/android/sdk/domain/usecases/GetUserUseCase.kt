package com.applivery.android.sdk.domain.usecases

import arrow.core.Either
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.domain.repository.AppliveryRepository

internal interface GetUserUseCase {

    suspend operator fun invoke(): Either<DomainError, User>
}

internal class GetUser(
    private val repository: AppliveryRepository
) : GetUserUseCase {

    override suspend fun invoke(): Either<DomainError, User> {
        return repository.getUser()
    }
}