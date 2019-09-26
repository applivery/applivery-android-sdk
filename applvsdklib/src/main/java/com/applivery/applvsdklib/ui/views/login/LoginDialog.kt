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

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.widget.EditText
import com.applivery.applvsdklib.R

class LoginDialog : DialogFragment() {

    var listener: (username: String, password: String) -> Unit = { _, _ -> }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setCancelable(false)
        builder.setView(inflater.inflate(R.layout.applivery_dialog_login, null))
            .setPositiveButton(R.string.appliveryLogin) { dialog, _ ->
                dialog.dismiss()
                with(dialog as AlertDialog) {
                    val usernameEditText =
                        this.findViewById(R.id.appliveryUsernameEditText) as EditText
                    val passwordEditText =
                        this.findViewById(R.id.appliveryPasswordEditText) as EditText
                    listener(usernameEditText.text.toString(), passwordEditText.text.toString())
                }
            }
            .setNegativeButton(R.string.appliveryCancel, { dialog, _ -> dialog.cancel() })

        return builder.create()
    }

    companion object {
        private const val TAG = "LoginDialog"
    }
}
