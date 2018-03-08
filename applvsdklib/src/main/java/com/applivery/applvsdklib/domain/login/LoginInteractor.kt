package com.applivery.applvsdklib.domain.login

import android.util.Log
import com.applivery.applvsdklib.domain.model.ErrorObject
import com.applivery.applvsdklib.domain.model.UserData

class LoginInteractor : Runnable {

  lateinit var userData: UserData
  lateinit var onSuccess: () -> Unit
  lateinit var onError: (error: ErrorObject) -> Unit

  fun makeLogin(userData: UserData,
      onSuccess: () -> Unit = {},
      onError: (error: ErrorObject) -> Unit = {}) {

    this.userData = userData
    this.onSuccess = onSuccess
    this.onError = onError


  }

  override fun run() {
    Log.d(TAG, "makeLogin($userData)")
  }

  companion object {
    private const val TAG = "LoginInteractor"
  }
}

