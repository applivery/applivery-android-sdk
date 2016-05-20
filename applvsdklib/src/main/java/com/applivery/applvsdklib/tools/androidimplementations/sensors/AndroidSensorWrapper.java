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

package com.applivery.applvsdklib.tools.androidimplementations.sensors;

import android.hardware.SensorEventListener;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/4/16.
 */
public class AndroidSensorWrapper {

  private boolean enabled;
  private final AppliverySensorType appliverySensorType;
  private final SensorEventListener sensorEventListener;
  private boolean registered;

  public AndroidSensorWrapper(AppliverySensorType appliverySensorType, SensorEventListener listener) {
    this.appliverySensorType = appliverySensorType;
    this.enabled = false;
    this.sensorEventListener = listener;
  }

  public void enableSensor(){
    this.enabled = true;
  }

  public void disableSensor(){
    this.enabled = false;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public AppliverySensorType getAppliverySensorType() {
    return appliverySensorType;
  }

  public int getAndroidSensorType() {
    return appliverySensorType.toAndroidSensor();
  }

  public SensorEventListener getSensorEventListener() {
    return sensorEventListener;
  }

  public void setRegistered(boolean registered) {
    this.registered = registered;
  }

  public boolean isRegistered() {
    return registered;
  }
}
