package com.applivery.android.sdk.domain.model

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull

internal inline fun <A, B> Either<DomainError, A>.mapNotNull(map: (A) -> B?): Either<DomainError, B> {
    return flatMap { either { ensureNotNull(map(it)) { DomainMapping() } } }
}

internal fun <A> Either<DomainError, A>.ignore(): Either<DomainError, Unit> {
    return map { }
}

internal class DomainMapping : DomainError()