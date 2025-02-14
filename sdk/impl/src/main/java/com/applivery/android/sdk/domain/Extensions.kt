package com.applivery.android.sdk.domain

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.RaiseDSL
import com.applivery.android.sdk.domain.model.DomainError

internal fun <T> Either<DomainError, T>.asResult(): Result<T> {
    return fold(
        ifLeft = { Result.failure(Throwable(it.message)) },
        ifRight = { Result.success(it) }
    )
}

@RaiseDSL
internal fun <B : Any> Raise<DomainError>.ensureNotNull(value: B?): B {
    return value ?: raise(UnsatisfiedConditionError())
}

@RaiseDSL
internal fun Raise<DomainError>.ensure(condition: Boolean) {
    return if (condition) Unit else raise(UnsatisfiedConditionError())
}

internal class UnsatisfiedConditionError : DomainError()
