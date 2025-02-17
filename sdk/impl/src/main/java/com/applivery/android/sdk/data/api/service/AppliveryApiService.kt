package com.applivery.android.sdk.data.api.service

import arrow.core.Either
import com.applivery.android.sdk.data.models.ApiError
import com.applivery.android.sdk.data.models.ApiResponseSchema
import com.applivery.android.sdk.data.models.AppConfigApi
import com.applivery.android.sdk.data.models.AuthenticationUriApi
import com.applivery.android.sdk.data.models.BindUserApi
import com.applivery.android.sdk.data.models.BindUserResponseApi
import com.applivery.android.sdk.data.models.BuildTokenApi
import com.applivery.android.sdk.data.models.FeedbackApi
import com.applivery.android.sdk.data.models.SendFeedbackResponseApi
import com.applivery.android.sdk.data.models.UserApi
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

internal interface AppliveryApiService {

    @GET("v1/app")
    suspend fun getConfig(): Either<ApiError, ApiResponseSchema<AppConfigApi>>

    @GET("v1/auth/redirect")
    suspend fun getAuthenticationUri(): Either<ApiError, ApiResponseSchema<AuthenticationUriApi>>

    @GET("v1/build/{build_id}/downloadToken")
    suspend fun getBuildToken(@Path("build_id") buildId: String): Either<ApiError, ApiResponseSchema<BuildTokenApi>>

    @POST("v1/auth/customLogin")
    suspend fun bindUser(@Body body: BindUserApi): Either<ApiError, ApiResponseSchema<BindUserResponseApi>>

    @GET("v1/auth/profile")
    suspend fun getUser(): Either<ApiError, ApiResponseSchema<UserApi>>

    @POST("v1/feedback")
    suspend fun sendFeedback(@Body body: FeedbackApi): Either<ApiError, ApiResponseSchema<SendFeedbackResponseApi>>

    @PUT
    suspend fun uploadVideo(@Url url: String, @Body video: RequestBody): ResponseBody
}
