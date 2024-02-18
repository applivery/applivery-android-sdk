package com.applivery.android.sdk.data.models

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.DomainError

fun <T> Either<ApiError, ServerResponseSchema<T>>.toDomain(): Either<DomainError, T> {
    return mapLeft { it.toDomain() }.flatMap { it.data?.right() ?: DomainError.Internal().left() }
}

fun ApiError.toDomain(): DomainError {
    return when (this) {
        is ApiError.LimitExceeded -> DomainError.LimitExceeded()
        is ApiError.Subscription -> DomainError.Subscription()
        is ApiError.Unauthorized -> DomainError.Unauthorized()
        is ApiError.IO,
        is ApiError.Internal -> DomainError.Internal()
    }
}

fun AppConfigApi.toDomain(): AppConfig {
    return AppConfig(
        forceAuth = sdk.android.forceAuth ?: false,
        forceUpdate = sdk.android.forceUpdate ?: false,
        lastBuildId = sdk.android.lastBuildId.orEmpty(),
        lastBuildVersion = sdk.android.lastBuildVersion?.toIntOrNull() ?: -1,
        minVersion = sdk.android.minVersion?.toIntOrNull() ?: -1,
        ota = sdk.android.ota ?: false
    )
}