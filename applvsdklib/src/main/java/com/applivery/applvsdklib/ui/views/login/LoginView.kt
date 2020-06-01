/*
 * Copyright (c) 2019 Applivery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    if (activity.fragmentManager.findFragmentByTag(TAG) != null) {
      return
    }

    val loginDialog = LoginDialog()
    loginDialog.listener = { username, password ->
      presenter.makeLogin(UserData(username = username, password = password))
    }
    loginDialog.show(activity.fragmentManager, TAG)
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
