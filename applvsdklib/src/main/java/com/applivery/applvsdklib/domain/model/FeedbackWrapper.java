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
  final private String screen;
  final private String screenShotBase64;
  final private String feedBackMessage;
  final private String vendor;
  final private String model;
  final private String type;
  final private String appId;
  final private String bugType;
  final private String osVersion;
  final private String deviceId;
  final private String batteryPercentage;
  final private boolean isBatteryCharging;
  final private String networkConnectivity;
  final private String screenResolution;
  final private String ramUsed;
  final private String ramTotal;
  final private String freeDiskPercentage;
  final private String screenOrientation;

  public FeedbackWrapper(String packageName, String appVersionName, String osName, String screen,
      String screenShotBase64, String feedBackMessage, String vendor, String model, String type,
      String appId, String bugType, String osVersion, String deviceId, String batteryPercentage,
      boolean isBatteryCharging, String networkConnectivity, String screenResolution, String ramUsed,
      String ramTotal, String freeDiskPercentage, String screenOrientation) {

    this.packageName = packageName;
    this.appVersionName = appVersionName;
    this.osName = osName;
    this.screen = screen;
    this.screenShotBase64 = screenShotBase64;
    this.feedBackMessage = feedBackMessage;
    this.vendor = vendor;
    this.model = model;
    this.type = type;
    this.appId = appId;
    this.bugType = bugType;
    this.osVersion = osVersion;
    this.deviceId = deviceId;
    this.batteryPercentage = batteryPercentage;
    this.isBatteryCharging = isBatteryCharging;
    this.networkConnectivity = networkConnectivity;
    this.screenResolution = screenResolution;
    this.ramUsed = ramUsed;
    this.ramTotal = ramTotal;
    this.freeDiskPercentage = freeDiskPercentage;
    this.screenOrientation = screenOrientation;
  }

  public static FeedbackWrapper createWrapper(Feedback feedback, CurrentAppInfo currentAppInfo,
      DeviceDetailsInfo deviceDetailsInfo) {

    FeedbackWrapper feedbackWrapper =
        new FeedbackWrapper(
            currentAppInfo.getPackageName(),
            String.valueOf(currentAppInfo.getVersionCode()),
            deviceDetailsInfo.getOsName(),
            feedback.getScreen(),
            feedback.getBase64ScreenCapture(),
            feedback.getMessage(),
            deviceDetailsInfo.getVendor(),
            deviceDetailsInfo.getModel(),
            deviceDetailsInfo.getDeviceType(),
            AppliverySdk.getApplicationId(),
            feedback.getType().getStringValue(),
            deviceDetailsInfo.getOsversion(),
            deviceDetailsInfo.getDeviceId(),
            deviceDetailsInfo.getBatteryPercentage(),
            deviceDetailsInfo.isBatteryCharging(),
            deviceDetailsInfo.getNetworkConnectivity(),
            deviceDetailsInfo.getScreenResolution(),
            deviceDetailsInfo.getUsedRam(),
            deviceDetailsInfo.getTotalRam(),
            deviceDetailsInfo.getFreeDiskPercentage(),
            deviceDetailsInfo.getScreenOrientation());

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

  public String getScreen() {
    return screen;
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

  public String getDeviceId() {
    return deviceId;
  }

  public String getBatteryPercentage() {
    return batteryPercentage;
  }

  public boolean isBatteryCharging() {
    return isBatteryCharging;
  }

  public String getNetworkConnectivity() {
    return networkConnectivity;
  }

  public String getScreenResolution() {
    return screenResolution;
  }

  public String getRamUsed() {
    return ramUsed;
  }

  public String getRamTotal() {
    return ramTotal;
  }

  public String getFreeDiskPercentage() {
    return freeDiskPercentage;
  }

  public String getScreenOrientation() {
    return screenOrientation;
  }
}
