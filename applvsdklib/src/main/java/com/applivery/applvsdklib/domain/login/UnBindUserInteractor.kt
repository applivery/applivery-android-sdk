package com.applivery.applvsdklib.domain.login


import android.util.Log
import com.applivery.applvsdklib.domain.model.ErrorObject
import com.applivery.applvsdklib.tools.executor.InteractorExecutor
import com.applivery.applvsdklib.tools.executor.MainThread
import com.applivery.applvsdklib.tools.session.SessionManager

class UnBindUserInteractor(
  private val interactorExecutor: InteractorExecutor,
  private val mainThread: MainThread,
  private val sessionManager: SessionManager
) : Runnable {

  lateinit var onSuccess: () -> Unit
  lateinit var onError: (error: ErrorObject) -> Unit

  fun unBindUser(
    onSuccess: () -> Unit = {},
    onError: (error: ErrorObject) -> Unit = {}
  ) {
    this.onSuccess = onSuccess
    this.onError = onError

    interactorExecutor.run(this)
  }

  override fun run() {

    try {

      sessionManager.clearSession()
      notifySuccess()
    } catch (exception: Exception) {

      Log.e(TAG, "unBindUser() -> ${exception.message}")
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
    private const val TAG = "UnBindUserInteractor"
  }
}

