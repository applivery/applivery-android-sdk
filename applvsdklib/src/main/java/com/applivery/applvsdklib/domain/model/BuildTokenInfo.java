package com.applivery.applvsdklib.domain.model;

import java.util.Date;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public class BuildTokenInfo implements BusinessObject<BuildTokenInfo>{

  private String token;
  private  String build;
  private Date exp;

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

  public Date getExp() {
    return exp;
  }

  public void setExp(Date exp) {
    this.exp = exp;
  }

  @Override public BuildTokenInfo getObject() {
    return this;
  }
}
