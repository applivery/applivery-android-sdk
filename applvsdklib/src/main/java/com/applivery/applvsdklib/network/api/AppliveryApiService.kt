/*
 * Copyright (c) 2016 Applivery
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

package com.applivery.applvsdklib.network.api

import com.applivery.applvsdklib.network.api.model.FeedbackEntity
import com.applivery.applvsdklib.network.api.model.LoginEntity
import com.applivery.applvsdklib.network.api.requests.BindUserRequest
import com.applivery.applvsdklib.network.api.responses.ApiAppConfigResponse
import com.applivery.applvsdklib.network.api.responses.ApiBuildTokenResponse
import com.applivery.applvsdklib.network.api.responses.ApiFeedbackResponse
import com.applivery.applvsdklib.network.api.responses.ApiLoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

private const val API_VERSION = "/v1"

interface AppliveryApiService {

  @GET("$API_VERSION/app")
  fun obtainAppConfig(): Call<ApiAppConfigResponse>

  @POST("$API_VERSION/feedback")
  fun sendFeedback(@Body feedback: FeedbackEntity): Call<ApiFeedbackResponse>

  @GET("$API_VERSION/build/{build_id}/downloadToken")
  fun obtainBuildToken(@Path("build_id") buildId: String): Call<ApiBuildTokenResponse>

  @POST("$API_VERSION//auth/login")
  fun makeLogin(@Body loginEntity: LoginEntity): Call<ApiLoginResponse>

  @POST("$API_VERSION//auth/check")
  fun bindUser(@Body bindUserRequest: BindUserRequest): Call<ApiLoginResponse>
}

