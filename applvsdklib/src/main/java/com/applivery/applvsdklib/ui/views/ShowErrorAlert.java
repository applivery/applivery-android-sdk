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

package com.applivery.applvsdklib.ui.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.R;
import com.applivery.applvsdklib.domain.model.ErrorObject;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class ShowErrorAlert {
  public void showError(ErrorObject error) {
    if (AppliverySdk.isContextAvailable()) {
      AlertDialog.Builder builder = new AlertDialog.Builder(AppliverySdk.getCurrentActivity());
      builder.setTitle(R.string.appliveryError)
          .setCancelable(true)
          .setMessage(error.getMessage())
          .setPositiveButton((R.string.appliveryAcceptButton),
              new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              })
          .show();
    } else {
      Toast.makeText(AppliverySdk.getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG)
          .show();
    }
  }
}
