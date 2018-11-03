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

package com.applivery.applvsdklib.domain.feedback;

import android.support.annotation.NonNull;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.BaseInteractor;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.appconfig.update.CurrentAppInfo;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.domain.model.Feedback;
import com.applivery.applvsdklib.domain.model.FeedbackResult;
import com.applivery.applvsdklib.domain.model.FeedbackWrapper;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.network.api.requests.FeedbackRequest;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidCurrentAppInfo;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidDeviceDetailsInfo;
import com.applivery.applvsdklib.ui.views.feedback.UserFeedbackPresenter;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class FeedbackInteractor extends BaseInteractor<FeedbackResult> {

  private final FeedbackRequest feedbackRequest;
  private final InteractorCallback feedbackCallback;

  private FeedbackInteractor(@NonNull AppliveryApiService appliveryApiService,
      @NonNull Feedback feedback, @NonNull CurrentAppInfo currentAppInfo,
      @NonNull DeviceDetailsInfo deviceDetailsInfo,
      @NonNull InteractorCallback interactorCallback) {

    FeedbackWrapper feedbackWrapper =
        FeedbackWrapper.createWrapper(feedback, currentAppInfo, deviceDetailsInfo);
    this.feedbackRequest = new FeedbackRequest(appliveryApiService, feedbackWrapper);
    this.feedbackCallback = interactorCallback;
  }

  @Override protected void receivedResponse(BusinessObject result) {
    super.receivedResponse(result, FeedbackResult.class);
  }

  @Override protected void error(ErrorObject error) {
    feedbackCallback.onError(error);
  }

  @Override protected void success(FeedbackResult response) {
    feedbackCallback.onSuccess(response);
  }

  @Override protected BusinessObject performRequest() {
    return feedbackRequest.execute();
  }

  public static Runnable getInstance(@NonNull AppliveryApiService service,
      @NonNull Feedback feedback, @NonNull UserFeedbackPresenter userFeedbackPresenter) {

    CurrentAppInfo currentAppInfo = new AndroidCurrentAppInfo(AppliverySdk.getApplicationContext());
    DeviceDetailsInfo deviceDetailsInfo = new AndroidDeviceDetailsInfo();

    return new FeedbackInteractor(service, feedback, currentAppInfo, deviceDetailsInfo,
        userFeedbackPresenter);
  }
}
