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

import com.applivery.base.di.InterceptorsProvider
import com.applivery.updates.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object ApiServiceProvider {

    fun getApiService(): UpdatesApiService {
        return Retrofit.Builder().baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient())
            .build()
            .create(UpdatesApiService::class.java)
    }

    fun getDownloadApiService(): DownloadApiService {
        return Retrofit.Builder().baseUrl(BuildConfig.DOWNLOAD_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient())
            .build()
            .create(DownloadApiService::class.java)
    }

    private fun getOkHttpClient(): OkHttpClient {

        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.interceptors().add(InterceptorsProvider.provideHeadersInterceptor())
        okHttpClientBuilder.interceptors().add(InterceptorsProvider.provideSessionInterceptor())

        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.interceptors()
                .add(InterceptorsProvider.provideHttpLoggingInterceptor())
        }

        return okHttpClientBuilder.build()
    }
}
