package com.applivery.applvsdklib.tools.androidimplementations.sensors;

import android.hardware.SensorEventListener;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/4/16.
 */
public class AndroidSensorWrapper {

  private boolean enabled;
  private final AppliverySensor appliverySensor;
  private final SensorEventListener sensorEventListener;

  public AndroidSensorWrapper(AppliverySensor appliverySensor, SensorEventListener listener) {
    this.appliverySensor = appliverySensor;
    this.enabled = false;
    this.sensorEventListener = listener;
  }

  public void enableSensor(){
    this.enabled = true;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public AppliverySensor getAppliverySensor() {
    return appliverySensor;
  }

  public int getAndroidSensorType() {
    return appliverySensor.toAndroidSensor();
  }

  public SensorEventListener getSensorEventListener() {
    return sensorEventListener;
  }
}
