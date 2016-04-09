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
import java.util.List;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 7/11/15.
 */
public class ApiAppConfigData {

  @SerializedName("_id") @Expose private String Id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("sdk") @Expose private ApiSdK sdk;
  @SerializedName("crashesCount") @Expose private int crashesCount;
  @SerializedName("feedbackCount") @Expose private int feedbackCount;
  @SerializedName("sitesCount") @Expose private int sitesCount;
  @SerializedName("totalDownloads") @Expose private int totalDownloads;
  @SerializedName("buildsCount") @Expose private int buildsCount;
  @SerializedName("modified") @Expose private String modified;
  @SerializedName("created") @Expose private String created;
  @SerializedName("so") @Expose private List<String> so;
  @SerializedName("description") @Expose private String description;

  public String getId() {
    return Id;
  }

  public void setId(String id) {
    Id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ApiSdK getSdk() {
    return sdk;
  }

  public void setSdk(ApiSdK sdk) {
    this.sdk = sdk;
  }

  public int getCrashesCount() {
    return crashesCount;
  }

  public void setCrashesCount(int crashesCount) {
    this.crashesCount = crashesCount;
  }

  public int getFeedbackCount() {
    return feedbackCount;
  }

  public void setFeedbackCount(int feedbackCount) {
    this.feedbackCount = feedbackCount;
  }

  public int getSitesCount() {
    return sitesCount;
  }

  public void setSitesCount(int sitesCount) {
    this.sitesCount = sitesCount;
  }

  public int getTotalDownloads() {
    return totalDownloads;
  }

  public void setTotalDownloads(int totalDownloads) {
    this.totalDownloads = totalDownloads;
  }

  public int getBuildsCount() {
    return buildsCount;
  }

  public void setBuildsCount(int buildsCount) {
    this.buildsCount = buildsCount;
  }

  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public List<String> getSo() {
    return so;
  }

  public void setSo(List<String> so) {
    this.so = so;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
