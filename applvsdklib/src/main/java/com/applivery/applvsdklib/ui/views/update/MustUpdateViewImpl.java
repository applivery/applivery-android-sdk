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

import android.app.Dialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.R;
import com.applivery.applvsdklib.ui.model.UpdateInfo;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class MustUpdateViewImpl extends Dialog implements UpdateView {

  private Button update;
  private ProgressBar progressBar;
  private TextView appName;
  private TextView updateMessage;
  private final UpdateListener updateListener;

  public MustUpdateViewImpl(UpdateInfo updateInfo, UpdateListener updateListener) {

    super(AppliverySdk.getCurrentActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);

    this.updateListener = updateListener;

    setCancelable(false);

    setContentView(R.layout.must_update);
    initViewElements();
    initViewElementsData(updateInfo);
  }

  private void initViewElements() {
    this.appName = (TextView) findViewById(R.id.must_update_title);
    this.updateMessage = (TextView) findViewById(R.id.must_update_message);
    this.update = (Button) findViewById(R.id.must_update_button);
    this.progressBar = (ProgressBar) findViewById(R.id.must_update_progress_bar);
    LayerDrawable layerDrawable = (LayerDrawable) progressBar.getProgressDrawable();
    Drawable progressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress);
    progressDrawable.setColorFilter(
        ContextCompat.getColor(AppliverySdk.getCurrentActivity(), R.color.appliveryMainColor),
        PorterDuff.Mode.SRC_IN);
  }

  private void initViewElementsData(UpdateInfo updateInfo) {
    appName.setText(updateInfo.getAppName());
    updateMessage.setText(updateInfo.getAppUpdateMessage());

    update.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        updateListener.onUpdateButtonClick();
      }
    });
  }

  @Override public void showUpdateDialog() {
    show();
  }

  @Override public void hideDownloadInProgress() {
    update.setVisibility(View.VISIBLE);
    progressBar.setVisibility(View.GONE);
  }

  @Override public void showDownloadInProgress() {
    update.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void updateProgress(double percent) {
    updatProcessTextView(percent, new Handler(getLooper()));
    AppliverySdk.Logger.log("Updated progress to " + percent);
  }

  private void updatProcessTextView(final double percent, Handler handler) {
    Runnable myRunnable = new Runnable() {
      @Override
      public void run() {
        progressBar.setProgress(Double.valueOf(percent).intValue());
      }
    };
    handler.post(myRunnable);
  }

  public Looper getLooper() {
    if (Looper.myLooper() == Looper.getMainLooper()){
      return Looper.myLooper();
    }else{
      return Looper.getMainLooper();
    }
  }
}
