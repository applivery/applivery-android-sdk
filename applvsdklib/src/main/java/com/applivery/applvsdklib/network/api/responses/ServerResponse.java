package com.applivery.applvsdklib.network.api.responses;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("UnusedDeclaration") public class ServerResponse<JsonObject> {

  @SerializedName("status") private boolean status;

  @SerializedName("response") private JsonObject data;

  @SerializedName("error") private ApiAppliveryServerErrorResponse error;

  private int httpCode;

  public void setHttpCode(int httpCode) {
    this.httpCode = httpCode;
  }

  public ApiAppliveryServerErrorResponse getError() {
    return error;
  }

  public void setError(ApiAppliveryServerErrorResponse error) {
    this.error = error;
  }

  public boolean getStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public JsonObject getData() {
    return data;
  }

  public void setData(JsonObject data) {
    this.data = data;
  }
}
