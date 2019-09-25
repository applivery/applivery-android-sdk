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