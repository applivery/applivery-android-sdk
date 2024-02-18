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
