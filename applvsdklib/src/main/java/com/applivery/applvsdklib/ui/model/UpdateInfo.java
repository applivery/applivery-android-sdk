package com.applivery.applvsdklib.ui.model;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/1/16.
 */
public class UpdateInfo {

  private String appName;
  private String appUpdateMessage;

  public UpdateInfo(String appName, String appUpdateMessage) {
    this.appName = appName;
    this.appUpdateMessage = appUpdateMessage;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppUpdateMessage() {
    return appUpdateMessage;
  }

  public void setAppUpdateMessage(String appUpdateMessage) {
    this.appUpdateMessage = appUpdateMessage;
  }
}
