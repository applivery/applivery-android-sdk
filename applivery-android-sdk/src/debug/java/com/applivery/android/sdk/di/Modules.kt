package com.applivery.android.sdk.di

import android.app.Application
import android.content.Context
import com.applivery.android.sdk.CurrentActivityProvider
import com.applivery.android.sdk.CurrentActivityProviderImpl
import com.applivery.android.sdk.data.api.ApiDataSource
import com.applivery.android.sdk.data.api.service.AppliveryApiService
import com.applivery.android.sdk.data.api.service.AppliveryDownloadService
import com.applivery.android.sdk.data.api.service.HeadersInterceptor
import com.applivery.android.sdk.data.api.service.ServiceBuilder
import com.applivery.android.sdk.data.api.service.ServiceUriBuilder.buildUponTenant
import com.applivery.android.sdk.data.api.service.SessionInterceptor
import com.applivery.android.sdk.data.auth.SessionManager
import com.applivery.android.sdk.data.auth.SessionManagerImpl
import com.applivery.android.sdk.data.base.JsonMapper
import com.applivery.android.sdk.data.memory.MemoryDataSource
import com.applivery.android.sdk.data.repository.AppliveryRepositoryImpl
import com.applivery.android.sdk.data.repository.DownloadsRepositoryImpl
import com.applivery.android.sdk.domain.AndroidHostAppPackageInfoProvider
import com.applivery.android.sdk.domain.AndroidLogger
import com.applivery.android.sdk.domain.AndroidSharedPreferencesProvider
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.InstallationIdProvider
import com.applivery.android.sdk.domain.InstallationIdProviderImpl
import com.applivery.android.sdk.domain.Logger
import com.applivery.android.sdk.domain.SharedPreferencesProvider
import com.applivery.android.sdk.domain.UnifiedErrorHandler
import com.applivery.android.sdk.domain.repository.AppliveryRepository
import com.applivery.android.sdk.domain.repository.DownloadsRepository
import com.applivery.android.sdk.domain.usecases.CheckUpdates
import com.applivery.android.sdk.domain.usecases.CheckUpdatesUseCase
import com.applivery.android.sdk.domain.usecases.DownloadLastBuild
import com.applivery.android.sdk.domain.usecases.DownloadLastBuildUseCase
import com.applivery.android.sdk.domain.usecases.GetAppConfig
import com.applivery.android.sdk.domain.usecases.GetAppConfigUseCase
import com.applivery.android.sdk.domain.usecases.GetAuthenticationUri
import com.applivery.android.sdk.domain.usecases.GetAuthenticationUriUseCase
import com.applivery.android.sdk.domain.usecases.IsUpToDate
import com.applivery.android.sdk.domain.usecases.IsUpToDateUseCase
import com.applivery.android.sdk.login.LoginHandler
import com.applivery.android.sdk.login.LoginViewModel
import com.applivery.android.sdk.updates.BuildInstaller
import com.applivery.android.sdk.updates.IntentBuildInstaller
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModelOf
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
                    appToken = getProperty(Properties.AppToken),
                    loginHandler = get()
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
    factoryOf(::ApiDataSource)
}

private val cacheModule = module {
    singleOf(::MemoryDataSource)
}

private val useCasesModule = module {
    factoryOf(::IsUpToDate).bind<IsUpToDateUseCase>()
    factoryOf(::GetAuthenticationUri).bind<GetAuthenticationUriUseCase>()
    factoryOf(::GetAppConfig).bind<GetAppConfigUseCase>()
    factoryOf(::CheckUpdates).bind<CheckUpdatesUseCase>()
    factoryOf(::DownloadLastBuild).bind<DownloadLastBuildUseCase>()
}

private val repositoriesModule = module {
    factoryOf(::AppliveryRepositoryImpl).bind<AppliveryRepository>()
    factoryOf(::DownloadsRepositoryImpl).bind<DownloadsRepository>()
}

private val viewModelsModule = module {
    viewModelOf(::LoginViewModel)
}

internal val dataModules = module {
    includes(networkModule)
    includes(cacheModule)
}

internal val domainModules = module {
    includes(useCasesModule)
    includes(repositoriesModule)
    factoryOf(::AndroidHostAppPackageInfoProvider).bind<HostAppPackageInfoProvider>()
    factoryOf(::AndroidSharedPreferencesProvider).bind<SharedPreferencesProvider>()
    factoryOf(::InstallationIdProviderImpl).bind<InstallationIdProvider>()
    factoryOf(::UnifiedErrorHandler)
}

internal val appModules = module {
    includes(viewModelsModule)
    factory<Application> { get<Context>() as Application }
    singleOf(::CurrentActivityProviderImpl).bind<CurrentActivityProvider>()
    factoryOf(::AndroidLogger).bind<Logger>()
    singleOf(::LoginHandler)
    factoryOf(::IntentBuildInstaller).bind<BuildInstaller>()
}
