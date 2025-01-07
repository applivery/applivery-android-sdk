package com.applivery.android.sdk.data.api.service

import arrow.core.Either
import com.applivery.android.sdk.data.models.ApiError
import com.applivery.android.sdk.data.models.ApiResponseSchema
import com.applivery.android.sdk.data.models.AppConfigApi
import com.applivery.android.sdk.data.models.AuthenticationUriApi
import retrofit2.http.GET

internal interface AppliveryApiService {

    @GET("v1/app")
    suspend fun getConfig(): Either<ApiError, ApiResponseSchema<AppConfigApi>>

    @GET("v1/auth/redirect")
    suspend fun getAuthenticationUri(): Either<ApiError, ApiResponseSchema<AuthenticationUriApi>>
}
