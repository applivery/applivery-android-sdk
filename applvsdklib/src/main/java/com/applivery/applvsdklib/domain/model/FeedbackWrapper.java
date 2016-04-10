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

package com.applivery.applvsdklib.domain.model;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class FeedbackWrapper {
  private String packageName;
  private String appVersionName;
  private String osName;
  private String screenShotBase64;
  private String feedBackMessage;
  private String vendor;
  private String model;
  private String type;
  private String appId;
  private String bugType;
  private String osVersion;

  //TODO next release stuff

  public static FeedbackWrapper createWrapper(Feedback feedback) {
    FeedbackWrapper feedbackWrapper = new FeedbackWrapper();
    return feedbackWrapper;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getAppVersionName() {
    return appVersionName;
  }

  public String getOsName() {
    return osName;
  }

  public String getScreenShotBase64() {
    return screenShotBase64;
  }

  public String getFeedBackMessage() {
    return feedBackMessage;
  }

  public String getVendor() {
    return vendor;
  }

  public String getModel() {
    return model;
  }

  public String getType() {
    return type;
  }

  public String getAppId() {
    return appId;
  }

  public String getBugType() {
    return bugType;
  }

  public String getOsVersion() {
    return osVersion;
  }
}
