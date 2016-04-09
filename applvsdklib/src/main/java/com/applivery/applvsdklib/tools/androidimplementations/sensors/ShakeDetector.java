package com.applivery.applvsdklib.tools.androidimplementations.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Sergio Martinez Rodriguez
 * Despite this implementation is taken from Jason Mcreynolds, have a look at
 * http://jasonmcreynolds.com/?p=388 for having the complete implementation in a blog post
 * so, thanks to Jason and all the people from stackoverflow that has contributed to this example:
 * Peterdk and Akos Cz.
 * Date 3/1/16.
 */
public class ShakeDetector implements SensorEventListener {

  /*
   * The gForce that is necessary to register as shake.
   * Must be greater than 1G (one earth gravity unit).
   * You can install "G-Force", by Blake La Pierre
   * from the Google Play Store and run it to see how
   *  many G's it takes to register a shake
   */
  private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
  private static final int SHAKE_SLOP_TIME_MS = 500;
  private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

  private OnShakeListener mListener;
  private long mShakeTimestamp;
  private int mShakeCount;

  public ShakeDetector(OnShakeListener listener) {
    this.mListener = listener;
  }

  public interface OnShakeListener {
    void onShake(int count);
  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  @Override public void onSensorChanged(SensorEvent event) {

    if (mListener != null) {
      float x = event.values[0];
      float y = event.values[1];
      float z = event.values[2];

      float gX = x / SensorManager.GRAVITY_EARTH;
      float gY = y / SensorManager.GRAVITY_EARTH;
      float gZ = z / SensorManager.GRAVITY_EARTH;

      // gForce will be close to 1 when there is no movement.
      double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

      if (gForce > SHAKE_THRESHOLD_GRAVITY) {
        final long now = System.currentTimeMillis();
        // ignore shake events too close to each other (500ms)
        if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
          return;
        }

        // reset the shake count after 3 seconds of no shakes
        if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
          mShakeCount = 0;
        }

        mShakeTimestamp = now;
        mShakeCount++;

        mListener.onShake(mShakeCount);
      }
    }
  }
}
