package com.applivery.android.sdk.di

import android.app.Application
import android.content.Context
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.HostHostActivityProvider
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
import com.applivery.android.sdk.domain.AppPreferences
import com.applivery.android.sdk.domain.AppPreferencesImpl
import com.applivery.android.sdk.domain.DeviceInfoProvider
import com.applivery.android.sdk.domain.DeviceInfoProviderImpl
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.Logger
import com.applivery.android.sdk.domain.SharedPreferencesProvider
import com.applivery.android.sdk.domain.UnifiedErrorHandler
import com.applivery.android.sdk.domain.repository.AppliveryRepository
import com.applivery.android.sdk.domain.repository.DownloadsRepository
import com.applivery.android.sdk.domain.usecases.BindUser
import com.applivery.android.sdk.domain.usecases.BindUserUseCase
import com.applivery.android.sdk.domain.usecases.CheckUpdates
import com.applivery.android.sdk.domain.usecases.CheckUpdatesUseCase
import com.applivery.android.sdk.domain.usecases.DownloadLastBuild
import com.applivery.android.sdk.domain.usecases.DownloadLastBuildUseCase
import com.applivery.android.sdk.domain.usecases.GetAppConfig
import com.applivery.android.sdk.domain.usecases.GetAppConfigUseCase
import com.applivery.android.sdk.domain.usecases.GetAuthenticationUri
import com.applivery.android.sdk.domain.usecases.GetAuthenticationUriUseCase
import com.applivery.android.sdk.domain.usecases.GetUser
import com.applivery.android.sdk.domain.usecases.GetUserUseCase
import com.applivery.android.sdk.domain.usecases.IsUpToDate
import com.applivery.android.sdk.domain.usecases.IsUpToDateUseCase
import com.applivery.android.sdk.domain.usecases.SendFeedback
import com.applivery.android.sdk.domain.usecases.SendFeedbackUseCase
import com.applivery.android.sdk.domain.usecases.UnbindUser
import com.applivery.android.sdk.domain.usecases.UnbindUserUseCase
import com.applivery.android.sdk.feedback.AndroidShakeDetector
import com.applivery.android.sdk.feedback.ContentUriImageDecoder
import com.applivery.android.sdk.feedback.ContentUriImageDecoderImpl
import com.applivery.android.sdk.feedback.FeedbackViewModel
import com.applivery.android.sdk.feedback.HostAppScreenshotProvider
import com.applivery.android.sdk.feedback.HostAppScreenshotProviderImpl
import com.applivery.android.sdk.feedback.ScreenshotFeedbackChecker
import com.applivery.android.sdk.feedback.ScreenshotFeedbackCheckerImpl
import com.applivery.android.sdk.feedback.ShakeDetector
import com.applivery.android.sdk.feedback.ShakeFeedbackChecker
import com.applivery.android.sdk.feedback.ShakeFeedbackCheckerImpl
import com.applivery.android.sdk.feedback.video.VideoReporter
import com.applivery.android.sdk.feedback.video.VideoReporterImpl
import com.applivery.android.sdk.login.LoginHandler
import com.applivery.android.sdk.login.LoginViewModel
import com.applivery.android.sdk.updates.AndroidBuildInstaller
import com.applivery.android.sdk.updates.BuildInstaller
import com.applivery.android.sdk.updates.UpdateInstallProgressObserver
import com.applivery.android.sdk.updates.UpdateInstallProgressSender
import com.applivery.android.sdk.updates.UpdateInstallProgressSenderImpl
import com.applivery.android.sdk.updates.UpdatesBackgroundChecker
import com.applivery.android.sdk.updates.UpdatesBackgroundCheckerImpl
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
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
                    appPreferences = get(),
                    appToken = getProperty(Properties.AppToken),
                    loginHandler = get()
                )
            )
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
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
    factoryOf(::BindUser).bind<BindUserUseCase>()
    factoryOf(::UnbindUser).bind<UnbindUserUseCase>()
    factoryOf(::GetUser).bind<GetUserUseCase>()
    factoryOf(::SendFeedback).bind<SendFeedbackUseCase>()
}

private val repositoriesModule = module {
    factoryOf(::AppliveryRepositoryImpl).bind<AppliveryRepository>()
    factoryOf(::DownloadsRepositoryImpl).bind<DownloadsRepository>()
}

private val viewModelsModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::FeedbackViewModel)
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
    factoryOf(::AppPreferencesImpl).bind<AppPreferences>()
    factoryOf(::UnifiedErrorHandler)
    factoryOf(::DeviceInfoProviderImpl).bind<DeviceInfoProvider>()
}

internal val appModules = module {
    includes(viewModelsModule)
    factory<Application> { get<Context>() as Application }
    singleOf(::HostHostActivityProvider).bind<HostActivityProvider>()
    singleOf(::UpdatesBackgroundCheckerImpl).bind<UpdatesBackgroundChecker>()
    factoryOf(::AndroidLogger).bind<Logger>()
    factoryOf(::DomainLogger)
    singleOf(::LoginHandler)
    factoryOf(::AndroidBuildInstaller).bind<BuildInstaller>()
    singleOf(::UpdateInstallProgressSenderImpl).binds(
        arrayOf(
            UpdateInstallProgressSender::class,
            UpdateInstallProgressObserver::class
        )
    )
    factoryOf(::AndroidShakeDetector).bind<ShakeDetector>()
    singleOf(::ShakeFeedbackCheckerImpl).bind<ShakeFeedbackChecker>()
    singleOf(::ScreenshotFeedbackCheckerImpl).bind<ScreenshotFeedbackChecker>()
    factoryOf(::ContentUriImageDecoderImpl).bind<ContentUriImageDecoder>()
    singleOf(::HostAppScreenshotProviderImpl).bind<HostAppScreenshotProvider>()
    singleOf(::VideoReporterImpl).bind<VideoReporter>()
}
