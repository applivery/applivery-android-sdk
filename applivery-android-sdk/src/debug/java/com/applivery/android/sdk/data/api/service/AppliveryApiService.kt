package com.applivery.android.sdk.data.api.service

import arrow.core.Either
import com.applivery.android.sdk.data.models.ApiError
import com.applivery.android.sdk.data.models.ApiResponseSchema
import com.applivery.android.sdk.data.models.AppConfigApi
import com.applivery.android.sdk.data.models.AuthenticationUriApi
import com.applivery.android.sdk.data.models.BuildTokenApi
import retrofit2.http.GET
import retrofit2.http.Path

internal interface AppliveryApiService {

    @GET("v1/app")
    suspend fun getConfig(): Either<ApiError, ApiResponseSchema<AppConfigApi>>

    @GET("v1/auth/redirect")
    suspend fun getAuthenticationUri(): Either<ApiError, ApiResponseSchema<AuthenticationUriApi>>

    @GET("v1/build/{build_id}/downloadToken")
    suspend fun getBuildToken(@Path("build_id") buildId: String): Either<ApiError, ApiResponseSchema<BuildTokenApi>>
}
