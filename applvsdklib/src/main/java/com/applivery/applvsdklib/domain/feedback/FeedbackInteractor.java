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

import android.app.Activity;
import com.applivery.applvsdklib.domain.BaseInteractor;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.domain.model.Feedback;
import com.applivery.applvsdklib.domain.model.FeedbackResult;
import com.applivery.applvsdklib.domain.model.FeedbackWrapper;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.network.api.requests.FeedbackRequest;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class FeedbackInteractor extends BaseInteractor<FeedbackResult> {

  //TODO next release stuff

  private final FeedbackRequest feedbackRequest;
  private final InteractorCallback feedbackCallback;
  private final FeedbackWrapper feedbackWrapper;

  public FeedbackInteractor(AppliveryApiService appliveryApiService, Feedback feedback) {
    this.feedbackWrapper = FeedbackWrapper.createWrapper(feedback);
    this.feedbackRequest = new FeedbackRequest(appliveryApiService, feedbackWrapper);
    this.feedbackCallback = new FeedbackInteractorCallback();
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
    //TODO transform fee
    return feedbackRequest.execute();
  }

  public static Runnable getInstance(AppliveryApiService service, Feedback feedback) {
    FeedbackInteractor feedbackInteractor = new FeedbackInteractor(service, feedback);
    return feedbackInteractor;
  }
}
