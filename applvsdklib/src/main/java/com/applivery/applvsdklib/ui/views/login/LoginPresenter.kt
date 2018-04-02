package com.applivery.applvsdklib.ui.views.login

import com.applivery.applvsdklib.domain.login.LoginInteractor
import com.applivery.applvsdklib.domain.model.UserData

class LoginPresenter(private val loginInteractor: LoginInteractor) {

  var view: LoginView? = null

  fun makeLogin(userData: UserData) {
    loginInteractor.makeLogin(userData,
        onSuccess = { view?.showLoginSuccess() },
        onError = { view?.showLoginError() })
  }

  fun requestLogin() {
    view?.showLoginDialog()
  }
}