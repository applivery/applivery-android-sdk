package com.applivery.android.sdk.domain.repository

import arrow.core.Either
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.AuthenticationUri
import com.applivery.android.sdk.domain.model.DomainError

internal interface AppliveryRepository {

    suspend fun getConfig(): Either<DomainError, AppConfig>

    suspend fun getAuthenticationUri(): Either<DomainError, AuthenticationUri>
}