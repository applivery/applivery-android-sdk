package com.applivery.android.sdk.data.repository

import arrow.core.Either
import com.applivery.android.sdk.data.models.AppConfigApi
import com.applivery.android.sdk.data.models.toDomain
import com.applivery.android.sdk.data.service.AppliveryApiService
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.repository.AppliveryRepository

class AppliveryRepositoryImpl(
    private val service: AppliveryApiService
) : AppliveryRepository {

    override suspend fun getConfig(): Either<DomainError, AppConfig> {
        return service.getConfig().toDomain().map(AppConfigApi::toDomain)
    }
}