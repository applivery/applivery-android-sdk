package com.applivery.applvsdklib.domain.model;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class DownloadResult implements BusinessObject<DownloadResult>{

  private final boolean success;
  private final String path;

  public DownloadResult(boolean status, String path) {
    this.success = status;
    this.path = path;
  }

  public DownloadResult(boolean status) {
    this.success = status;
    this.path = "";
  }

  @Override public DownloadResult getObject() {
    return this;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getPath() {
    return path;
  }

}
