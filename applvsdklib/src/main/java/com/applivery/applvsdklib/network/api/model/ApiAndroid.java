/*
 * Copyright (c) 2016 Applivery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
  @SerializedName("authUpdate") @Expose private boolean authUpdate;
  @SerializedName("authFeedback") @Expose private boolean authFeedback;

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

  public boolean isAuthUpdate() {
    return authUpdate;
  }

  public void setAuthUpdate(boolean authUpdate) {
    this.authUpdate = authUpdate;
  }

  public boolean isAuthFeedback() {
    return authFeedback;
  }

  public void setAuthFeedback(boolean authFeedback) {
    this.authFeedback = authFeedback;
  }
}