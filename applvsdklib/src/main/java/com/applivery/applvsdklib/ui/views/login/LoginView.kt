package com.applivery.applvsdklib.ui.views.login

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import com.applivery.applvsdklib.domain.model.UserData
import com.applivery.applvsdklib.tools.injection.Injection


class LoginView(private val activity: Activity) {

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
    Log.d(TAG, "showLoginSuccess()")
  }

  fun showLoginError() {
    val alertDialog = AlertDialog.Builder(activity).create()
    alertDialog.setTitle("Login error")
    alertDialog.setMessage("The email or password you entered is not valid")
    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", { dialog, _ ->
      showLoginDialog()
      dialog.dismiss()
    })
    alertDialog.show()
  }

  companion object {
    private const val TAG = "LoginView"
  }
}