package com.applivery.android.sdk.di

import com.applivery.android.sdk.network.base.JsonMapper
import com.applivery.android.sdk.network.service.AppliveryApiService
import com.applivery.android.sdk.network.service.EitherCallAdapterFactory
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


internal val networkModules = module {

    // TODO: add interceptors
    factory { OkHttpClient.Builder() }
    factory { GsonBuilder().create() }
    factory { JsonMapper(gson = get()) }
    factory {
        Retrofit.Builder()
            .baseUrl(getProperty<String>(Properties.ApiUrl))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(EitherCallAdapterFactory(jsonMapper = get()))
            .client(get())
            .build()
    }

    factory { get<Retrofit>().create<AppliveryApiService>() }
}