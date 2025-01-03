/*
 * Copyright (c) 2020 Applivery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.applivery.data

import com.applivery.base.AppliveryDataManager
import com.applivery.data.ApiUriBuilder.buildUponTenant
import com.applivery.data.di.InjectorUtils
import com.applivery.data.request.BindUserRequest
import com.applivery.data.request.FeedbackRequest
import com.applivery.data.response.ApiAppConfig
import com.applivery.data.response.ApiBuildToken
import com.applivery.data.response.ApiFeedback
import com.applivery.data.response.AuthenticationUriApi
import com.applivery.data.response.ServerResponse
import com.applivery.data.response.UserDataResponse
import com.applivery.data.response.UserEntity
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

private const val API_VERSION = "/v1"

interface AppliveryApiService {

    @GET("$API_VERSION/app")
    suspend fun obtainAppConfig(): ServerResponse<ApiAppConfig>

    @POST("$API_VERSION/feedback")
    suspend fun sendFeedback(@Body feedback: FeedbackRequest): ServerResponse<ApiFeedback>

    @POST("$API_VERSION/auth/customLogin")
    fun bindUser(@Body bindUserRequest: BindUserRequest): Call<ServerResponse<UserDataResponse>>

    @GET("$API_VERSION/build/{build_id}/downloadToken")
    fun obtainBuildToken(@Path("build_id") buildId: String): Call<ServerResponse<ApiBuildToken>>

    @GET("$API_VERSION/auth/profile")
    fun getUser(): Call<ServerResponse<UserEntity>>

    @GET("$API_VERSION/auth/redirect")
    suspend fun getAuthenticationUri(): ServerResponse<AuthenticationUriApi>

    companion object {
        @Volatile
        private var instance: AppliveryApiService? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: provideAppliveryApiService().also {
                    instance = it
                }
            }

        private fun provideAppliveryApiService(): AppliveryApiService {
            val url = BuildConfig.API_URL.buildUponTenant(AppliveryDataManager.tenant)
            return Retrofit.Builder().baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(InjectorUtils.provideOkHttpClient())
                .build()
                .create(AppliveryApiService::class.java)
        }
    }
}
