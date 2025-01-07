package com.applivery.android.sdk.data.api

import arrow.core.Either
import com.applivery.android.sdk.data.api.service.AppliveryApiService
import com.applivery.android.sdk.data.models.AppConfigApi
import com.applivery.android.sdk.data.models.AuthenticationUriApi
import com.applivery.android.sdk.data.models.toDomain
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.AuthenticationUri
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.mapNotNull

internal class ApiDataSource(
    private val service: AppliveryApiService
) {

    suspend fun getAppConfig(): Either<DomainError, AppConfig> {
        return service.getConfig().toDomain().mapNotNull(AppConfigApi::toDomain)
    }

    suspend fun getAuthenticationUri(): Either<DomainError, AuthenticationUri> {
        return service.getAuthenticationUri().toDomain().mapNotNull(AuthenticationUriApi::toDomain)
    }
}