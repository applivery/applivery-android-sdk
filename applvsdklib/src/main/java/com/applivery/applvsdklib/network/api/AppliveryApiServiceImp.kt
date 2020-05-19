package com.applivery.applvsdklib.network.api

import com.applivery.applvsdklib.BuildConfig
import com.applivery.applvsdklib.network.api.interceptor.HeadersInterceptor
import com.applivery.applvsdklib.tools.injection.Injection.provideSessionInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppliveryApiServiceImp {

    companion object {
        @Volatile
        private var instance: AppliveryApiService? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: buildAppliveryApiService().also {
                    instance = it
                }
            }

        private fun buildAppliveryApiService(): AppliveryApiService {

            val okHttpClientBuilder = OkHttpClient.Builder()
            okHttpClientBuilder.interceptors().add(HeadersInterceptor())
            okHttpClientBuilder.interceptors().add(provideSessionInterceptor())

            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                okHttpClientBuilder.interceptors().add(loggingInterceptor)
            }

            return Retrofit.Builder().baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build()
                .create(AppliveryApiService::class.java)
        }
    }
}