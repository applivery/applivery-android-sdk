package com.applivery.applvsdklib.domain.feedback;

import com.applivery.applvsdklib.domain.BaseInteractor;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.domain.model.Feedback;
import com.applivery.applvsdklib.domain.model.FeedbackResult;
import com.applivery.applvsdklib.domain.model.FeedbackWrapper;
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

  public FeedbackInteractor(FeedbackRequest feedbackRequest, InteractorCallback feedbackCallback,
      Feedback feedback, AppliveryApiService appliveryApiService) {
    this.feedbackWrapper = FeedbackWrapper.createWrapper(feedback);
    this.feedbackRequest = new FeedbackRequest(appliveryApiService, feedbackWrapper);
    this.feedbackCallback = feedbackCallback;
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
}
