package com.applivery.applvsdklib.domain.login

import android.util.Log
import com.applivery.applvsdklib.domain.model.ErrorObject
import com.applivery.applvsdklib.domain.model.UserData
import com.applivery.applvsdklib.network.api.AppliveryApiService
import com.applivery.applvsdklib.network.api.model.ApiLogin
import com.applivery.applvsdklib.tools.executor.InteractorExecutor
import com.applivery.applvsdklib.tools.executor.MainThread
import com.applivery.applvsdklib.tools.session.SessionManager

class LoginInteractor(
    private val interactorExecutor: InteractorExecutor,
    private val mainThread: MainThread,
    private val apiService: AppliveryApiService,
    private val sessionManager: SessionManager) : Runnable {

  lateinit var userData: UserData
  lateinit var onSuccess: () -> Unit
  lateinit var onError: (error: ErrorObject) -> Unit

  fun makeLogin(userData: UserData,
      onSuccess: () -> Unit = {},
      onError: (error: ErrorObject) -> Unit = {}) {

    this.userData = userData
    this.onSuccess = onSuccess
    this.onError = onError

    interactorExecutor.run(this)
  }

  override fun run() {

    try {
      val apiLoginResponse = apiService.makeLogin(
          ApiLogin(email = userData.username, password = userData.password)).execute().body()

      if (apiLoginResponse != null && apiLoginResponse.data != null) {
        sessionManager.saveSession(apiLoginResponse.data.accessToken)
        notifySuccess()
      } else {
        Log.e(TAG, "Make login response error")
        notifyError(ErrorObject())
      }
    } catch (exception: Exception) {
      Log.e(TAG, "makeLogin() -> ${exception.message}")
      notifyError(ErrorObject())
    }
  }

  private fun notifySuccess() {
    mainThread.execute(Runnable { onSuccess() })
  }

  private fun notifyError(error: ErrorObject) {
    mainThread.execute(Runnable { onError(error) })
  }

  companion object {
    private const val TAG = "LoginInteractor"
  }
}

