package com.applivery.applvsdklib.ui.views.login

import android.app.Activity
import android.app.AlertDialog
import com.applivery.applvsdklib.R
import com.applivery.applvsdklib.domain.model.UserData
import com.applivery.applvsdklib.tools.injection.Injection


class LoginView(private val activity: Activity, private val onSuccess: () -> Unit = {}) {

  val presenter: LoginPresenter = Injection.provideLoginPresenter()

  init {
    presenter.view = this
  }

  fun showLoginDialog() {
    val loginDialog = LoginDialog()
    loginDialog.listener = { username, password ->
      presenter.makeLogin(UserData(username, password))
    }
    loginDialog.show(activity.fragmentManager, "login_dialog")
  }

  fun showLoginSuccess() {
    onSuccess()
  }

  fun showLoginError() {
    val alertDialog = AlertDialog.Builder(activity).create()
    alertDialog.setTitle(activity.getString(R.string.appliveryLoginFailDielogTitle))
    alertDialog.setMessage(activity.getString(R.string.appliveryLoginFailDielogText))
    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK") { dialog, _ ->
      showLoginDialog()
      dialog.dismiss()
    }
    alertDialog.show()
  }

  companion object {
    private const val TAG = "LoginView"
  }
}