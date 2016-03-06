package com.applivery.applvsdklib.domain.model;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class FeedbackResult implements BusinessObject<FeedbackResult> {

  //TODO next release stuff

  private boolean success = false;

  public FeedbackResult(boolean status) {
    success = status;
  }

  @Override public FeedbackResult getObject() {
    return this;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }
}
