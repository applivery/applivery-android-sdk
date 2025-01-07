package com.applivery.android.sdk.domain.model

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull

inline fun <A, B> Either<DomainError, A>.mapNotNull(map: (A) -> B?): Either<DomainError, B> {
    return flatMap { either { ensureNotNull(map(it)) { DomainMapping() } } }
}

class DomainMapping : DomainError()