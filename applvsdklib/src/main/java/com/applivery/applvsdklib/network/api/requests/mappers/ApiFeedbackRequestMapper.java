package com.applivery.applvsdklib.network.api.requests.mappers;

import com.applivery.applvsdklib.domain.model.FeedbackWrapper;
import com.applivery.applvsdklib.network.api.requests.ApiFeedbackRequestData;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class ApiFeedbackRequestMapper
    implements RequestMapper<FeedbackWrapper, ApiFeedbackRequestData> {

  @Override public ApiFeedbackRequestData modelToData(FeedbackWrapper feedbackWrapper) {
    ApiFeedbackRequestData apiFeedbackData = new ApiFeedbackRequestData();
    //TODO implement mapping of fields
    //TODO next release stuff
    return apiFeedbackData;
  }
}
