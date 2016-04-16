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

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.ui.model.UpdateInfo;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class UpdateViewPresenter {

  private UpdateView updateView;
  private final UpdateListener updateListener;

  public UpdateViewPresenter(UpdateListener updateListener) {
    this.updateListener = updateListener;
  }

  public void showForcedUpdate(String appName, String updateMessage) {
    if (AppliverySdk.isContextAvailable()) {
      UpdateInfo updateInfo = new UpdateInfo(appName, updateMessage);
      MustUpdateViewImpl mustUpdateView = new MustUpdateViewImpl();
      mustUpdateView.setUpdateInfo(updateInfo);
      mustUpdateView.setUpdateListener(updateListener);
      mustUpdateView.lockRotationOnParentScreen();
      this.updateView = mustUpdateView;
      this.updateListener.setUpdateView(updateView);
      this.updateView.showUpdateDialog();
    }
  }

  public void showSuggestedUpdate(String appName, String updateMessage) {
    if (AppliverySdk.isContextAvailable()) {
      UpdateInfo updateInfo = new UpdateInfo(appName, updateMessage);
      this.updateView = new SuggestedUpdateViewImpl(updateInfo, updateListener);
      this.updateListener.setUpdateView(updateView);
      this.updateView.showUpdateDialog();
    }
  }
}
