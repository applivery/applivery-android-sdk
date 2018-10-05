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

package com.applivery.applvsdklib.domain.download.app;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.BaseInteractor;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.model.BuildTokenInfo;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.DownloadResult;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.network.api.requests.DownloadBuildRequest;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidAppInstallerImpl;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public class DownloadBuildInteractor extends BaseInteractor<DownloadResult> {

  private final DownloadBuildRequest downloadBuildRequest;
  private final InteractorCallback<DownloadResult> interactorCallback;
  private final DownloadStatusListener downloadStatusListener;
  private final AppInstaller appInstaller;

  private DownloadBuildInteractor(AppliveryApiService appliveryApiService, String appName,
      BuildTokenInfo buildTokenInfo, final DownloadBuildInteractorCallback interactorCallback,
      ExternalStorageWriter externalStorageWriter) {

    this.downloadStatusListener = new DownloadStatusListener() {
      @Override public void updateDownloadPercentStatus(double percent) {
        interactorCallback.updateDownloadProgress(percent);
      }

      @Override public void downloadCompleted(DownloadResult downloadResult) {
        sendDelayedResponse(downloadResult);
      }

      @Override public void downloadNotStartedPermissionDenied() {
        interactorCallback.downloadNotStartedPermissionDenied();
      }
    };

    this.downloadBuildRequest =
        new DownloadBuildRequest(appliveryApiService, buildTokenInfo, appName,
            downloadStatusListener, externalStorageWriter);
    this.interactorCallback = interactorCallback;
    this.appInstaller = new AndroidAppInstallerImpl(AppliverySdk.getApplicationContext(),
        AppliverySdk.getFileProviderAuthority());
  }

  @Override protected void receivedResponse(BusinessObject downloadResult) {
    super.receivedResponse(downloadResult, DownloadResult.class);
  }

  @Override protected void error(ErrorObject serverResponse) {
    interactorCallback.onError(serverResponse);
  }

  @Override protected void success(final DownloadResult response) {
    if (response.isSuccess()) {
      interactorCallback.onSuccess(response);
      appInstaller.installApp(response.getPath());
    } else {
      if (response.getError() != null && !response.getError().isEmpty()) {
        interactorCallback.onError(new ErrorObject("Download error: " + response.getError()));
      }
    }
  }

  @Override protected BusinessObject performRequest() {
    return downloadBuildRequest.execute();
  }

  public static Runnable getInstance(AppliveryApiService appliveryApiService, String appName,
      BuildTokenInfo buildTokenInfo, DownloadBuildInteractorCallback interactorCallback,
      ExternalStorageWriter externalStorageWriter) {

    return new DownloadBuildInteractor(appliveryApiService, appName, buildTokenInfo,
        interactorCallback, externalStorageWriter);
  }
}
