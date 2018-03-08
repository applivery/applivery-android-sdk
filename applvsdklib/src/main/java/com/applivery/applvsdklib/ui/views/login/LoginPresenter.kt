package com.applivery.applvsdklib.ui.views.login

import android.util.Log
import com.applivery.applvsdklib.domain.model.UserData

class LoginPresenter {

  var view: LoginView? = null

  fun makeLogin(userData: UserData) {
    Log.d(TAG, "UserData: $userData")
  }

  fun requestLogin() {
    view?.showLoginDialog()
  }

  companion object {
    private const val TAG = "LoginPresenter"
  }
}