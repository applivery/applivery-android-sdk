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

import android.hardware.Sensor;
import com.applivery.applvsdklib.tools.utils.StringValueEnum;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/4/16.
 */
public enum AppliverySensor implements StringValueEnum{
  ACCELEROMETER("ACCELEROMETER");

  private final String type;

  AppliverySensor(final String type) {
    this.type = type;
  }

  public String getStringValue() {
    return type;
  }

  public int toAndroidSensor(){
    switch (this){
      case ACCELEROMETER:
        return Sensor.TYPE_ACCELEROMETER;
      default:
        return -99;
    }
  }

  public static AppliverySensor toAppliverySensor(int sensor){
    switch (sensor){
      case Sensor.TYPE_ACCELEROMETER:
        return AppliverySensor.ACCELEROMETER;
      default:
        return null;
    }
  }
}
