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
