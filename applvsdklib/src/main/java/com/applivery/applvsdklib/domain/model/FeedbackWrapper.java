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

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.appconfig.update.CurrentAppInfo;
import com.applivery.applvsdklib.domain.feedback.DeviceDetailsInfo;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class FeedbackWrapper {

  final private String packageName;
  final private String appVersionName;
  final private String osName;
  final private String screenShotBase64;
  final private String feedBackMessage;
  final private String vendor;
  final private String model;
  final private String type;
  final private String appId;
  final private String bugType;
  final private String osVersion;

  public FeedbackWrapper(String packageName, String appVersionName, String osName,
      String screenShotBase64, String feedBackMessage, String vendor, String model, String type,
      String appId, String bugType, String osVersion) {

    this.packageName = packageName;
    this.appVersionName = appVersionName;
    this.osName = osName;
    this.screenShotBase64 = screenShotBase64;
    this.feedBackMessage = feedBackMessage;
    this.vendor = vendor;
    this.model = model;
    this.type = type;
    this.appId = appId;
    this.bugType = bugType;
    this.osVersion = osVersion;

  }

  public static FeedbackWrapper createWrapper(Feedback feedback, CurrentAppInfo currentAppInfo,
      DeviceDetailsInfo deviceDetailsInfo) {

    FeedbackWrapper feedbackWrapper =
        new FeedbackWrapper(
            currentAppInfo.getPackageName(),
            String.valueOf(currentAppInfo.getVersionCode()),
            deviceDetailsInfo.getOsName(),
            feedback.getBase64ScreenCapture(),
            feedback.getMessage(),
            deviceDetailsInfo.getVendor(),
            deviceDetailsInfo.getModel(),
            deviceDetailsInfo.getDeviceType(),
            AppliverySdk.getApplicationId(),
            feedback.getType().getStringValue(),
            deviceDetailsInfo.getOsversion());

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
