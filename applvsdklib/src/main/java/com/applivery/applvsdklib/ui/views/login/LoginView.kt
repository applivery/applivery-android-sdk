package com.applivery.applvsdklib.ui.views.login

import android.app.FragmentManager
import com.applivery.applvsdklib.domain.model.UserData

class LoginView(private val fragmentManager: FragmentManager) {

  val presenter: LoginPresenter = LoginPresenter()

  init {
    presenter.view = this
  }

  fun showLoginDialog() {
    val loginDialog = LoginDialog()
    loginDialog.listener = { username, password ->
      presenter.makeLogin(UserData(username, password))
    }
    loginDialog.show(fragmentManager, "login_dialog")
  }
}