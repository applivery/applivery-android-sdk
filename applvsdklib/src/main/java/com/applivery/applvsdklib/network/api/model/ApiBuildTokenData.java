package com.applivery.applvsdklib.network.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class ApiBuildTokenData {

  @SerializedName("token") @Expose private String token;
  @SerializedName("build") @Expose private String build;
  @SerializedName("exp") @Expose private long exp;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getBuild() {
    return build;
  }

  public void setBuild(String build) {
    this.build = build;
  }

  public long getExp() {
    return exp;
  }

  public void setExp(long exp) {
    this.exp = exp;
  }
}
