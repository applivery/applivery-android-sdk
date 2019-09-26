/*
 * Copyright (c) 2019 Applivery
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
package com.applivery.updates.data

import com.applivery.base.data.ServerResponse
import com.applivery.updates.data.response.ApiBuildToken
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

private const val API_VERSION = "/v1"

interface UpdatesApiService {

    @GET("$API_VERSION/build/{build_id}/downloadToken")
    fun obtainBuildToken(@Path("build_id") buildId: String): Call<ServerResponse<ApiBuildToken>>
}
