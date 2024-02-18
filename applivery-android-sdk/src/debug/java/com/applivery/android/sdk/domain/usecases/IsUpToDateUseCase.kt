package com.applivery.android.sdk.domain.usecases

import arrow.core.Either
import com.applivery.android.sdk.data.service.AppliveryApiService
import com.applivery.android.sdk.domain.model.Error
import java.util.concurrent.ExecutorService

interface IsUpToDateUseCase {

    suspend operator fun invoke(): Either<Error, Boolean>
}

class IsUpToDate(
    private val service: AppliveryApiService
) : IsUpToDateUseCase{

    override suspend fun invoke(): Either<Error, Boolean> {
        TODO()
    }

}