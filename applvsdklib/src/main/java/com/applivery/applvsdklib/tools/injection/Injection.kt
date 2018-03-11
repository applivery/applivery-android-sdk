package com.applivery.applvsdklib.tools.injection

import com.applivery.applvsdklib.AppliverySdk
import com.applivery.applvsdklib.domain.login.LoginInteractor
import com.applivery.applvsdklib.network.api.AppliveryApiService
import com.applivery.applvsdklib.network.api.AppliveryApiServiceBuilder
import com.applivery.applvsdklib.tools.executor.InteractorExecutor
import com.applivery.applvsdklib.tools.executor.MainThread
import com.applivery.applvsdklib.tools.executor.MainThreadImp
import com.applivery.applvsdklib.tools.executor.ThreadExecutor
import com.applivery.applvsdklib.tools.session.SessionManager
import com.applivery.applvsdklib.ui.views.login.LoginPresenter


object Injection {

  fun provideLoginPresenter(): LoginPresenter {
    return LoginPresenter(provideLoginInteractor())
  }

  private fun provideLoginInteractor(): LoginInteractor {
    val interactorExecutor = provideInteractorExecutor()
    val mainThread = provideMainThread()
    val apiService = provideAppliveryApiService()
    val sessionManager = provideSessionManager()

    return LoginInteractor(interactorExecutor, mainThread, apiService, sessionManager)
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

  private fun provideSessionManager(): SessionManager {
    return SessionManager.create(AppliverySdk.getSharedPreferences())
  }
}