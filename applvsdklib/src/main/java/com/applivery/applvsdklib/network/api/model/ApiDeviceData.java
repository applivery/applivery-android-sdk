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
 * Date 10/4/16.
 */
public class ApiDeviceData {

  @SerializedName("model") @Expose private final String model;
  @SerializedName("vendor") @Expose private final String vendor;
  @SerializedName("type") @Expose private final String type;
  @SerializedName("id") @Expose private final String id;
  @SerializedName("battery") @Expose private final String battery;
  @SerializedName("batteryStatus") @Expose private final boolean batteryStatus;
  @SerializedName("network") @Expose private final String network;
  @SerializedName("resolution") @Expose private final String resolution;
  @SerializedName("ramUsed") @Expose private final String ramUsed;
  @SerializedName("ramTotal") @Expose private final String ramTotal;
  @SerializedName("diskFree") @Expose private final String diskFree;
  @SerializedName("orientation") @Expose private final String orientation;

  public ApiDeviceData(String model, String vendor, String type, String id, String battery,
      boolean batteryStatus, String network, String resolution, String ramUsed, String ramTotal,
      String diskFree, String orientation) {
    this.model = model;
    this.vendor = vendor;
    this.type = type;
    this.id = id;
    this.battery = battery;
    this.batteryStatus = batteryStatus;
    this.network = network;
    this.resolution = resolution;
    this.ramUsed = ramUsed;
    this.ramTotal = ramTotal;
    this.diskFree = diskFree;
    this.orientation = orientation;
  }
}
