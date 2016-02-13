package com.applivery.applvsdklib.network.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 8/11/15.
 */
public class ApiAndroid {

  @SerializedName("minVersion") @Expose private String minVersion;
  @SerializedName("lastBuildId") @Expose private String lastBuildId;
  @SerializedName("lastBuildVersion") @Expose private String lastBuildVersion;
  @SerializedName("updateMsg") @Expose private String updateMsg;
  @SerializedName("mustUpdateMsg") @Expose private String mustUpdateMsg;
  @SerializedName("forceUpdate") @Expose private boolean forceUpdate;
  @SerializedName("ota") @Expose private boolean ota;

  public String getMinVersion() {
    return minVersion;
  }

  public void setMinVersion(String minVersion) {
    this.minVersion = minVersion;
  }

  public String getLastBuildId() {
    return lastBuildId;
  }

  public void setLastBuildId(String lastBuildId) {
    this.lastBuildId = lastBuildId;
  }

  public String getLastBuildVersion() {
    return lastBuildVersion;
  }

  public void setLastBuildVersion(String lastBuildVersion) {
    this.lastBuildVersion = lastBuildVersion;
  }

  public String getUpdateMsg() {
    return updateMsg;
  }

  public void setUpdateMsg(String updateMsg) {
    this.updateMsg = updateMsg;
  }

  public String getMustUpdateMsg() {
    return mustUpdateMsg;
  }

  public void setMustUpdateMsg(String mustUpdateMsg) {
    this.mustUpdateMsg = mustUpdateMsg;
  }

  public boolean isForceUpdate() {
    return forceUpdate;
  }

  public void setForceUpdate(boolean forceUpdate) {
    this.forceUpdate = forceUpdate;
  }

  public boolean isOta() {
    return ota;
  }

  public void setOta(boolean ota) {
    this.ota = ota;
  }
}