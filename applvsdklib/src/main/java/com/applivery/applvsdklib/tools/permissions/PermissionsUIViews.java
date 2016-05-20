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

package com.applivery.applvsdklib.tools.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 15/1/16.
 */
public class PermissionsUIViews {

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  public static void showRationaleView(final RationaleResponse rationaleResponse, Context context,
      int rationaleTitleStringId, int rationaleMessageStringId) {
    new AlertDialog.Builder(context).setTitle(rationaleTitleStringId)
        .setMessage(rationaleMessageStringId)
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            rationaleResponse.cancelPermissionRequest();
          }
        })
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            rationaleResponse.continuePermissionRequest();
          }
        })
        .setOnDismissListener(new DialogInterface.OnDismissListener() {
          @Override public void onDismiss(DialogInterface dialog) {
            rationaleResponse.cancelPermissionRequest();
          }
        })
        .show();
  }

  public static void showPermissionToast(Context context, int stringId) {
    Toast.makeText(context, stringId, Toast.LENGTH_LONG).show();
  }

  public static ViewGroup getAppContainer(Activity activity) throws NullContainerException {
    if (activity != null) {
      return (ViewGroup) activity.findViewById(android.R.id.content);
    } else {
      throw new NullContainerException("Container is null");
    }
  }
}
