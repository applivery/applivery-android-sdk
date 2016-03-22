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
