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

package com.applivery.applvsdklib.network.api.requests;

import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.FeedbackResult;
import com.applivery.applvsdklib.domain.model.FeedbackWrapper;
import com.applivery.applvsdklib.network.api.requests.mappers.ApiFeedbackRequestMapper;
import com.applivery.applvsdklib.network.api.responses.ApiFeedbackResponse;
import retrofit2.Call;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class FeedbackRequest extends ServerRequest {

  //TODO next release stuff

  private final AppliveryApiService apiService;
  private final FeedbackWrapper feedbackWrapper;
  private final ApiFeedbackRequestMapper apiFeedbackRequestMapper;

  public FeedbackRequest(AppliveryApiService apiService, FeedbackWrapper feedbackWrapper) {
    this.apiService = apiService;
    this.feedbackWrapper = feedbackWrapper;
    this.apiFeedbackRequestMapper = new ApiFeedbackRequestMapper();
  }

  @Override protected BusinessObject performRequest() {
    ApiFeedbackRequestData apiFeedbackData = apiFeedbackRequestMapper.modelToData(feedbackWrapper);
    Call<ApiFeedbackResponse> response = apiService.sendFeedback(apiFeedbackData);
    ApiFeedbackResponse apiFeedbackResponse = super.performRequest(response);
    return new FeedbackResult(apiFeedbackResponse.getStatus());
  }

}
