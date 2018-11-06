/*
 * Copyright (c) 2016 Applivery
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

package com.applivery.applvsdklib.ui.views

import android.app.AlertDialog
import android.widget.Toast
import com.applivery.applvsdklib.AppliverySdk
import com.applivery.applvsdklib.R
import com.applivery.applvsdklib.domain.model.ErrorObject

class ShowErrorAlert {
  fun showError(error: ErrorObject) = if (error.isBusinessError) {
    showAlertDialog(error.message)
  } else {
    AppliverySdk.Logger.loge(error.message)
  }

  private fun showAlertDialog(message: String) {
    if (AppliverySdk.isContextAvailable()) {
      val builder = AlertDialog.Builder(AppliverySdk.getCurrentActivity())
      builder.setTitle(R.string.appliveryError)
          .setCancelable(true)
          .setMessage(message)
          .setPositiveButton(R.string.appliveryAcceptButton
          ) { dialog, _ -> dialog.dismiss() }
          .show()
    } else {
      Toast.makeText(AppliverySdk.getApplicationContext(), message, Toast.LENGTH_LONG)
          .show()
    }
  }
}
