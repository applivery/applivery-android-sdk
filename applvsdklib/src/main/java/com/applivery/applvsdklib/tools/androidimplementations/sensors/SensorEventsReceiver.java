package com.applivery.applvsdklib.tools.androidimplementations.sensors;

import com.applivery.applvsdklib.AppliverySdk;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/4/16.
 */
public class SensorEventsReceiver implements ShakeDetector.OnShakeListener {

  @Override public void onShake(int count) {
    AppliverySdk.sendFeedbackOnShake();
  }

}
