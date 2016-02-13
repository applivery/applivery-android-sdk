package com.applivery.applvsdklib.domain.model;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 8/11/15.
 */
public class Android {

  private String minVersion;
  private String lastBuildId;
  private String lastBuildVersion;
  private boolean ota;
  private boolean forceUpdate;
  private String updateMsg;
  private String mustUpdateMsg;

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

  public boolean isOta() {
    return ota;
  }

  public void setOta(boolean ota) {
    this.ota = ota;
  }

  public boolean isForceUpdate() {
    return forceUpdate;
  }

  public void setForceUpdate(boolean forceUpdate) {
    this.forceUpdate = forceUpdate;
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
}
