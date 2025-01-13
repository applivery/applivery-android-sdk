package com.applivery.android.sdk.domain

import arrow.core.Either
import com.applivery.android.sdk.domain.model.DomainError

internal fun <T> Either<DomainError, T>.asResult(): Result<T> {
    return fold(
        ifLeft = { Result.failure(Throwable(it.message)) },
        ifRight = { Result.success(it) }
    )
}