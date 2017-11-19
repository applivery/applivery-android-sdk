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

package com.applivery.applvsdklib.ui.views.update;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.R;
import com.applivery.applvsdklib.ui.model.UpdateInfo;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class SuggestedUpdateViewImpl implements UpdateView {

  private final Builder builder;
  private final AlertDialog alertDialog;
  private final UpdateListener updateListener;
  private final Context context;
  private ProgressDialog progress;

  public SuggestedUpdateViewImpl(UpdateInfo updateInfo, UpdateListener updateListener) {
    this.context = AppliverySdk.getCurrentActivity();
    this.builder = buildDialog(context, updateInfo);
    this.alertDialog = createAlertDialog(context, updateInfo);
    this.updateListener = updateListener;
  }

  private AlertDialog createAlertDialog(Context context, UpdateInfo updateInfo) {
    final FrameLayout frameView = new FrameLayout(context);
    builder.setView(frameView);

    final AlertDialog alertDialog = builder.create();
    LayoutInflater inflater = alertDialog.getLayoutInflater();
    inflater.inflate(R.layout.applivery_suggested_update, frameView);
    TextView textView = (TextView) frameView.findViewById(R.id.suggested_update_text);

    String appliveryUpdateMsg = context.getString(R.string.appliveryUpdateMsg);

    if (!appliveryUpdateMsg.isEmpty()) {
      textView.setText(appliveryUpdateMsg);
    } else {
      if (updateInfo != null) {
        textView.setText(updateInfo.getAppUpdateMessage());
      }
    }
    return alertDialog;
  }

  private Builder buildDialog(Context context, UpdateInfo updateInfo) {
    Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(updateInfo.getAppName())
        .setCancelable(true)
        .setPositiveButton(context.getString(R.string.appliveryUpdate), onUpdateClick())
        .setNegativeButton(context.getString(R.string.appliveryLater), onCancelClick());
    return builder;
  }

  private DialogInterface.OnClickListener onUpdateClick() {
    return new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int id) {
        updateListener.onUpdateButtonClick();
        alertDialog.dismiss();
      }
    };
  }

  private DialogInterface.OnClickListener onCancelClick() {
    return new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int id) {
        alertDialog.dismiss();
      }
    };
  }

  @Override public void showUpdateDialog() {
    if (!AppliverySdk.isUpdating()) {
      alertDialog.show();
    }
  }

  @Override public void hideDownloadInProgress() {
    if (progress != null && progress.isShowing()) {
      AppliverySdk.isUpdating(false);
      progress.dismiss();
    }
  }

  @Override public void showDownloadInProgress() {
    if (AppliverySdk.isContextAvailable()) {
      AppliverySdk.isUpdating(true);
      progress = ProgressDialog.show(context, context.getString(R.string.applivery_download_title),
          context.getString(R.string.applivery_download_message) + " 0%", true);
    }
  }

  @Override public void updateProgress(double percent) {
    updatProcessTextView(percent, new Handler(getLooper()));
  }

  @Override public void downloadNotStartedPermissionDenied() {
    progress.dismiss();
  }

  private void updatProcessTextView(final double percent, Handler handler) {
    Runnable myRunnable = new Runnable() {
      @Override public void run() {
        progress.setMessage(
            context.getString(R.string.applivery_download_message) + " " + Double.valueOf(percent)
                .intValue() + "%");
      }
    };
    handler.post(myRunnable);
  }

  public Looper getLooper() {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      return Looper.myLooper();
    } else {
      return Looper.getMainLooper();
    }
  }
}
