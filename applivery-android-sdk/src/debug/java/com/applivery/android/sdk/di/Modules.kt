package com.applivery.android.sdk.di

import android.app.Application
import android.content.Context
import com.applivery.android.sdk.CurrentActivityProvider
import com.applivery.android.sdk.CurrentActivityProviderImpl
import com.applivery.android.sdk.data.auth.SessionManager
import com.applivery.android.sdk.data.auth.SessionManagerImpl
import com.applivery.android.sdk.data.base.JsonMapper
import com.applivery.android.sdk.data.repository.AppliveryRepositoryImpl
import com.applivery.android.sdk.data.service.AppliveryApiService
import com.applivery.android.sdk.data.service.AppliveryDownloadService
import com.applivery.android.sdk.data.service.HeadersInterceptor
import com.applivery.android.sdk.data.service.ServiceBuilder
import com.applivery.android.sdk.data.service.ServiceUriBuilder.buildUponTenant
import com.applivery.android.sdk.data.service.SessionInterceptor
import com.applivery.android.sdk.domain.AndroidHostAppPackageInfoProvider
import com.applivery.android.sdk.domain.AndroidSharedPreferencesProvider
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.InstallationIdProvider
import com.applivery.android.sdk.domain.InstallationIdProviderImpl
import com.applivery.android.sdk.domain.SharedPreferencesProvider
import com.applivery.android.sdk.domain.repository.AppliveryRepository
import com.applivery.android.sdk.domain.usecases.IsUpToDate
import com.applivery.android.sdk.domain.usecases.IsUpToDateUseCase
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.create

private val ApiServiceBuilder = named("apiServiceBuilder")
private val DownloadServiceBuilder = named("downloadServiceBuilder")
private val ApiServiceUrl = named("apiServiceUrl")
private val DownloadServiceUrl = named("downloadServiceUrl")

private val networkModule = module {
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
            addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            )
        }.build()
    }
    factory(ApiServiceUrl) {
        val serviceUrl = getProperty<String>(Properties.ApiUrl)
        val tenant = getProperty<String>(Properties.AppTenant)
        serviceUrl.buildUponTenant(tenant)
    }
    factory(DownloadServiceUrl) {
        val serviceUrl = getProperty<String>(Properties.DownloadUrl)
        val tenant = getProperty<String>(Properties.AppTenant)
        serviceUrl.buildUponTenant(tenant)
    }
    factory(ApiServiceBuilder) {
        ServiceBuilder(
            baseUrl = get<String>(ApiServiceUrl),
            client = get()
        )
    }
    factory(DownloadServiceBuilder) {
        ServiceBuilder(
            baseUrl = get<String>(DownloadServiceUrl),
            client = get()
        )
    }
    factory { get<ServiceBuilder>(ApiServiceBuilder).retrofit.create<AppliveryApiService>() }
    factory { get<ServiceBuilder>(DownloadServiceBuilder).retrofit.create<AppliveryDownloadService>() }
    factory { JsonMapper(gson = GsonBuilder().create()) }
    factoryOf(::SessionManagerImpl).bind<SessionManager>()
}

private val useCasesModule = module {
    factoryOf(::IsUpToDate).bind<IsUpToDateUseCase>()
}

private val repositoriesModule = module {
    factoryOf(::AppliveryRepositoryImpl).bind<AppliveryRepository>()
}

internal val dataModules = module {
    includes(networkModule)
}

internal val domainModules = module {
    includes(useCasesModule)
    includes(repositoriesModule)
    factoryOf(::AndroidHostAppPackageInfoProvider).bind<HostAppPackageInfoProvider>()
    factoryOf(::AndroidSharedPreferencesProvider).bind<SharedPreferencesProvider>()
    factoryOf(::InstallationIdProviderImpl).bind<InstallationIdProvider>()
}

internal val appModules = module {
    factory<Application> { get<Context>() as Application }
    singleOf(::CurrentActivityProviderImpl).bind<CurrentActivityProvider>()
}
