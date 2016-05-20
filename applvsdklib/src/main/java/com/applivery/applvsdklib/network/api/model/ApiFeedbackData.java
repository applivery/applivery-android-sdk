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
 * Date 3/1/16.
 */
public class ApiFeedbackData {

  @SerializedName("app") @Expose private final String app;
  @SerializedName("type") @Expose private final String type;
  @SerializedName("screen") @Expose private final String screen;
  @SerializedName("message") @Expose private final String message;

  @SerializedName("packageInfo") @Expose private final ApiPackageInfoData packageInfo;
  @SerializedName("deviceInfo") @Expose private final ApiDeviceInfoData deviceInfo;

  @SerializedName("screenshot") @Expose private final String screenshot;

  public ApiFeedbackData(String app, String type, String message,
      String screen, ApiPackageInfoData packageInfo,
      ApiDeviceInfoData deviceInfo, String screenshot) {
    this.app = app;
    this.type = type;
    this.message = message;
    this.screen = screen;
    this.packageInfo = packageInfo;
    this.deviceInfo = deviceInfo;
    this.screenshot = screenshot;
  }
}
