package com.applivery.updates.data

import com.applivery.base.data.HeadersInterceptor
import com.applivery.updates.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DownloadApiServiceBuilder {

    fun build(): DownloadApiService {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.interceptors().add(HeadersInterceptor())
        // TODO get the interceptor properly
//        okHttpClientBuilder.interceptors().add(Injection.provideSessionInterceptor())

        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.interceptors().add(loggingInterceptor)
        }

        return Retrofit.Builder().baseUrl(BuildConfig.DOWNLOAD_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClientBuilder.build())
            .build()
            .create(DownloadApiService::class.java)
    }
}