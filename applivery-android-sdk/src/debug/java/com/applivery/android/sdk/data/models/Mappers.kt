package com.applivery.android.sdk.data.models

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.AuthenticationUri
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.InternalError
import com.applivery.android.sdk.domain.model.LimitExceededError
import com.applivery.android.sdk.domain.model.SubscriptionError
import com.applivery.android.sdk.domain.model.UnauthorizedError

fun <T> Either<ApiError, ApiResponseSchema<T>>.toDomain(): Either<DomainError, T> {
    return mapLeft { it.toDomain() }.flatMap { it.data?.right() ?: InternalError().left() }
}

fun ApiError.toDomain(): DomainError {
    return when (this) {
        is ApiError.LimitExceeded -> LimitExceededError()
        is ApiError.Subscription -> SubscriptionError()
        is ApiError.Unauthorized -> UnauthorizedError()
        is ApiError.IO,
        is ApiError.Internal -> InternalError()
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

fun AuthenticationUriApi.toDomain(): AuthenticationUri? {
    return AuthenticationUri(uri ?: return null)
}
