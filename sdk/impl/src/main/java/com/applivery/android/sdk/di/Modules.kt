package com.applivery.android.sdk.di

import android.app.Application
import android.content.Context
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.HostActivityProviderImpl
import com.applivery.android.sdk.configuration.Configuration
import com.applivery.android.sdk.data.api.ApiDataSource
import com.applivery.android.sdk.data.api.service.AppliveryApiService
import com.applivery.android.sdk.data.api.service.AppliveryDownloadService
import com.applivery.android.sdk.data.api.service.HeadersInterceptor
import com.applivery.android.sdk.data.api.service.ServiceBuilder
import com.applivery.android.sdk.data.api.service.ServiceUriBuilder.withTenant
import com.applivery.android.sdk.data.api.service.SessionInterceptor
import com.applivery.android.sdk.data.auth.SessionManager
import com.applivery.android.sdk.data.auth.SessionManagerImpl
import com.applivery.android.sdk.data.base.JsonMapper
import com.applivery.android.sdk.data.persistence.BuildMetadataDatastore
import com.applivery.android.sdk.data.repository.AppliveryRepositoryImpl
import com.applivery.android.sdk.data.repository.DeviceIdRepositoryImpl
import com.applivery.android.sdk.data.repository.DownloadsRepositoryImpl
import com.applivery.android.sdk.data.repository.identifier.AndroidIdProvider
import com.applivery.android.sdk.data.repository.identifier.GsfIdProvider
import com.applivery.android.sdk.data.repository.identifier.InstallationIdProvider
import com.applivery.android.sdk.data.repository.identifier.MediaDrmIdProvider
import com.applivery.android.sdk.domain.AndroidHostAppPackageInfoProvider
import com.applivery.android.sdk.domain.AndroidLogger
import com.applivery.android.sdk.domain.AndroidSharedPreferencesProvider
import com.applivery.android.sdk.domain.AppPreferences
import com.applivery.android.sdk.domain.AppPreferencesImpl
import com.applivery.android.sdk.domain.DeviceInfoProvider
import com.applivery.android.sdk.domain.DeviceInfoProviderImpl
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.FeedbackProgressProvider
import com.applivery.android.sdk.domain.FeedbackProgressProviderImpl
import com.applivery.android.sdk.domain.FeedbackProgressUpdater
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.Logger
import com.applivery.android.sdk.domain.PostponedUpdateLogic
import com.applivery.android.sdk.domain.PostponedUpdateLogicImpl
import com.applivery.android.sdk.domain.SharedPreferencesProvider
import com.applivery.android.sdk.domain.UnifiedErrorHandler
import com.applivery.android.sdk.domain.repository.AppliveryRepository
import com.applivery.android.sdk.domain.repository.DeviceIdRepository
import com.applivery.android.sdk.domain.repository.DownloadsRepository
import com.applivery.android.sdk.domain.usecases.BindUser
import com.applivery.android.sdk.domain.usecases.BindUserUseCase
import com.applivery.android.sdk.domain.usecases.CheckUpdates
import com.applivery.android.sdk.domain.usecases.CheckUpdatesUseCase
import com.applivery.android.sdk.domain.usecases.DownloadBuild
import com.applivery.android.sdk.domain.usecases.DownloadBuildUseCase
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
import com.applivery.android.sdk.domain.usecases.PurgeDownloads
import com.applivery.android.sdk.domain.usecases.PurgeDownloadsUseCase
import com.applivery.android.sdk.domain.usecases.SendFeedback
import com.applivery.android.sdk.domain.usecases.SendFeedbackUseCase
import com.applivery.android.sdk.domain.usecases.UnbindUser
import com.applivery.android.sdk.domain.usecases.UnbindUserUseCase
import com.applivery.android.sdk.feedback.ContentUriImageDecoder
import com.applivery.android.sdk.feedback.ContentUriImageDecoderImpl
import com.applivery.android.sdk.feedback.FeedbackLauncher
import com.applivery.android.sdk.feedback.FeedbackLauncherImpl
import com.applivery.android.sdk.feedback.FeedbackViewModel
import com.applivery.android.sdk.feedback.screenshot.HostAppScreenshotProvider
import com.applivery.android.sdk.feedback.screenshot.HostAppScreenshotProviderImpl
import com.applivery.android.sdk.feedback.screenshot.ScreenshotFeedbackChecker
import com.applivery.android.sdk.feedback.screenshot.ScreenshotFeedbackCheckerImpl
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
                    deviceIdRepository = get(),
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
        serviceUrl.withTenant(tenant)
    }
    factory(DownloadServiceUrl) {
        val serviceUrl = getProperty<String>(Properties.DownloadUrl)
        val tenant = getProperty<String>(Properties.AppTenant)
        serviceUrl.withTenant(tenant)
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

private val persistenceModule = module {
    singleOf(::BuildMetadataDatastore)
}

private val useCasesModule = module {
    factoryOf(::IsUpToDate).bind<IsUpToDateUseCase>()
    factoryOf(::GetAuthenticationUri).bind<GetAuthenticationUriUseCase>()
    factoryOf(::GetAppConfig).bind<GetAppConfigUseCase>()
    factoryOf(::CheckUpdates).bind<CheckUpdatesUseCase>()
    factoryOf(::DownloadLastBuild).bind<DownloadLastBuildUseCase>()
    factoryOf(::DownloadBuild).bind<DownloadBuildUseCase>()
    factoryOf(::BindUser).bind<BindUserUseCase>()
    factoryOf(::UnbindUser).bind<UnbindUserUseCase>()
    factoryOf(::GetUser).bind<GetUserUseCase>()
    factoryOf(::SendFeedback).bind<SendFeedbackUseCase>()
    factoryOf(::PurgeDownloads).bind<PurgeDownloadsUseCase>()
}

private val repositoriesModule = module {
    factoryOf(::AppliveryRepositoryImpl).bind<AppliveryRepository>()
    factoryOf(::DownloadsRepositoryImpl).bind<DownloadsRepository>()
    factoryOf(::DeviceIdRepositoryImpl).bind<DeviceIdRepository>()
    singleOf(::GsfIdProvider)
    singleOf(::MediaDrmIdProvider)
    singleOf(::AndroidIdProvider)
    singleOf(::InstallationIdProvider)
}

private val viewModelsModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::FeedbackViewModel)
}

internal val dataModules = module {
    includes(networkModule)
    includes(persistenceModule)
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
    singleOf(::HostActivityProviderImpl).bind<HostActivityProvider>()
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
    singleOf(::ScreenshotFeedbackCheckerImpl).bind<ScreenshotFeedbackChecker>()
    factoryOf(::ContentUriImageDecoderImpl).bind<ContentUriImageDecoder>()
    singleOf(::HostAppScreenshotProviderImpl).bind<HostAppScreenshotProvider>()
    singleOf(::VideoReporterImpl).bind<VideoReporter>()
    singleOf(::FeedbackProgressProviderImpl).binds(
        arrayOf(
            FeedbackProgressProvider::class,
            FeedbackProgressUpdater::class
        )
    )
    factory<Configuration> { getProperty(Properties.Configuration) }
    singleOf(::PostponedUpdateLogicImpl).bind<PostponedUpdateLogic>()
    singleOf(::FeedbackLauncherImpl).bind<FeedbackLauncher>()
}
