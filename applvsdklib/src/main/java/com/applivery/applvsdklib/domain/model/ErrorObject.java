package com.applivery.applvsdklib.domain.model;

import com.applivery.applvsdklib.network.api.responses.ApiAppliveryServerErrorResponse;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 8/11/15.
 */
public class ErrorObject implements BusinessObject<ErrorObject> {

  private boolean isBusinessError;
  private String message;
  private int businessCode;

  public ErrorObject(ApiAppliveryServerErrorResponse serverResponse) {
    businessCode = serverResponse.getCode();
    message = serverResponse.getMsg();
    isBusinessError = serverResponse.isBusinessError();
  }

  @Override public ErrorObject getObject() {
    return this;
  }

  public String getMessage() {
    return message;
  }
}
