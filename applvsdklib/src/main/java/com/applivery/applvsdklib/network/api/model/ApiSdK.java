package com.applivery.applvsdklib.network.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 8/11/15.
 */
public class ApiSdK {

  @SerializedName("android") @Expose private ApiAndroid android;

  public ApiAndroid getAndroid() {
    return android;
  }

  public void setAndroid(ApiAndroid android) {
    this.android = android;
  }
}
