package com.applivery.android.sdk.data.api.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class ServiceBuilder(
    baseUrl: String,
    client: OkHttpClient
) {

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(EitherCallAdapterFactory.create())
        .client(client)
        .build()
}