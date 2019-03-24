package com.applivery.applvsdklib.tools.injection

import com.applivery.applvsdklib.AppliverySdk
import com.applivery.applvsdklib.domain.login.BindUserInteractor
import com.applivery.applvsdklib.domain.login.LoginInteractor
import com.applivery.applvsdklib.domain.model.AppConfig
import com.applivery.applvsdklib.network.api.AppliveryApiService
import com.applivery.applvsdklib.network.api.AppliveryApiServiceBuilder
import com.applivery.applvsdklib.network.api.interceptor.SessionInterceptor
import com.applivery.applvsdklib.tools.executor.InteractorExecutor
import com.applivery.applvsdklib.tools.executor.MainThread
import com.applivery.applvsdklib.tools.executor.MainThreadImp
import com.applivery.applvsdklib.tools.executor.ThreadExecutor
import com.applivery.applvsdklib.tools.session.SessionManager
import com.applivery.applvsdklib.ui.views.feedback.FeedbackView
import com.applivery.applvsdklib.ui.views.feedback.UserFeedbackPresenter
import com.applivery.applvsdklib.ui.views.login.LoginPresenter

internal object Injection {

  var appConfig: AppConfig? = null

  fun provideLoginPresenter(): LoginPresenter {
    return LoginPresenter(provideLoginInteractor())
  }

  fun provideFeedbackPresenter(feedbackView: FeedbackView): UserFeedbackPresenter {
    return UserFeedbackPresenter(appConfig, feedbackView, provideSessionManager())
  }

  private fun provideLoginInteractor(): LoginInteractor {
    val interactorExecutor = provideInteractorExecutor()
    val mainThread = provideMainThread()
    val apiService = provideAppliveryApiService()
    val sessionManager = provideSessionManager()

    return LoginInteractor(interactorExecutor, mainThread, apiService, sessionManager)
  }

  fun provideBindUserInteractor(): BindUserInteractor {
    val interactorExecutor = provideInteractorExecutor()
    val mainThread = provideMainThread()
    val apiService = provideAppliveryApiService()
    val sessionManager = provideSessionManager()

    return BindUserInteractor(interactorExecutor, mainThread, apiService, sessionManager)
  }

  private fun provideInteractorExecutor(): InteractorExecutor {
    return ThreadExecutor()
  }

  private fun provideMainThread(): MainThread {
    return MainThreadImp()
  }

  private fun provideAppliveryApiService(): AppliveryApiService {
    return AppliveryApiServiceBuilder.getAppliveryApiInstance()
  }

  fun provideSessionManager(): SessionManager {
    return SessionManager.create(AppliverySdk.getSharedPreferences())
  }

  fun provideSessionInterceptor(): SessionInterceptor {
    return SessionInterceptor(provideSessionManager())
  }
}