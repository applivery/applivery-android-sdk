package com.applivery.android.sdk.data.service

import arrow.core.Either
import com.applivery.android.sdk.data.models.ApiError
import com.applivery.android.sdk.data.models.AppConfigApi
import com.applivery.android.sdk.data.models.ServerResponseSchema
import retrofit2.http.GET

interface AppliveryApiService {

    @GET("v1/app")
    suspend fun getConfig(): Either<ApiError, ServerResponseSchema<AppConfigApi>>
}
