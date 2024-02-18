package com.applivery.android.sdk.di

import com.applivery.android.sdk.data.auth.SessionManager
import com.applivery.android.sdk.data.auth.SessionManagerImpl
import com.applivery.android.sdk.data.base.JsonMapper
import com.applivery.android.sdk.data.service.AppliveryApiService
import com.applivery.android.sdk.data.service.EitherCallAdapterFactory
import com.applivery.android.sdk.data.service.HeadersInterceptor
import com.applivery.android.sdk.data.service.SessionInterceptor
import com.applivery.android.sdk.domain.AndroidHostAppPackageInfoProvider
import com.applivery.android.sdk.domain.AndroidSharedPreferencesProvider
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.InstallationIdProvider
import com.applivery.android.sdk.domain.InstallationIdProviderImpl
import com.applivery.android.sdk.domain.SharedPreferencesProvider
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


internal val networkModules = module {
    factory {
        Retrofit.Builder()
            .baseUrl(getProperty<String>(Properties.ApiUrl))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(EitherCallAdapterFactory(jsonMapper = get()))
            .client(get())
            .build()
    }

    factory {
        OkHttpClient.Builder().apply {
            addInterceptor(HeadersInterceptor(packageInfoProvider = get()))
            addInterceptor(
                SessionInterceptor(
                    sessionManager = get(),
                    idProvider = get(),
                    appToken = getProperty(Properties.AppToken)
                )
            )
        }
    }
    factory { get<Retrofit>().create<AppliveryApiService>() }
    factory { GsonBuilder().create() }
    factoryOf(::JsonMapper)
    factoryOf(::SessionManagerImpl).bind<SessionManager>()
}

internal val domainModules = module {
    factoryOf(::AndroidHostAppPackageInfoProvider).bind<HostAppPackageInfoProvider>()
    factoryOf(::AndroidSharedPreferencesProvider).bind<SharedPreferencesProvider>()
    factoryOf(::InstallationIdProviderImpl).bind<InstallationIdProvider>()
}
