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

package com.applivery.applvsdklib.domain.download.token;

import com.applivery.applvsdklib.domain.BaseInteractor;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.model.BuildTokenInfo;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.network.api.requests.ObtainBuildDownloadTokenRequest;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public class ObtainAppBuildDownloadTokenInteractor extends BaseInteractor<BuildTokenInfo> {

  private final ObtainBuildDownloadTokenRequest obtainBuildDownloadTokenRequest;
  private final InteractorCallback<BuildTokenInfo> infoInteractorCallback;

  public ObtainAppBuildDownloadTokenInteractor(AppliveryApiService appliveryApiService,
      String buildId, InteractorCallback<BuildTokenInfo> infoInteractorCallback) {

    this.obtainBuildDownloadTokenRequest =
        new ObtainBuildDownloadTokenRequest(appliveryApiService, buildId);
    this.infoInteractorCallback = infoInteractorCallback;
  }

  @Override protected void receivedResponse(BusinessObject obj) {
    super.receivedResponse(obj, BuildTokenInfo.class);
  }

  @Override protected void error(ErrorObject serverResponse) {
    infoInteractorCallback.onError(serverResponse);
  }

  @Override protected void success(BuildTokenInfo response) {
    infoInteractorCallback.onSuccess(response);
  }

  @Override protected BusinessObject performRequest() {
    return obtainBuildDownloadTokenRequest.execute();
  }

  public static Runnable getInstance(AppliveryApiService appliveryApiService, String buildId,
      InteractorCallback interactorCallback) {

    ObtainAppBuildDownloadTokenInteractor obtainAppBuildDownloadTokenInteractor =
        new ObtainAppBuildDownloadTokenInteractor(appliveryApiService, buildId, interactorCallback);

    return obtainAppBuildDownloadTokenInteractor;
  }
}
