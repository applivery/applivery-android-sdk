package com.applivery.applvsdklib.tools.androidimplementations;

import android.app.Activity;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/1/16.
 */
public class ActivityLifecyleWrapper {
  private Activity activity;
  private boolean isPaused;
  private boolean isStopped;


  public ActivityLifecyleWrapper(Activity activity, boolean isPaused, boolean isStopped) {
    this.activity = activity;
    this.isPaused = isPaused;
    this.isStopped = isStopped;
  }

  public Activity getActivity() {
    return activity;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public boolean isPaused() {
    return isPaused;
  }

  public void setIsPaused(boolean isPaused) {
    this.isPaused = isPaused;
  }

  public boolean isStopped() {
    return isStopped;
  }

  public void setIsStopped(boolean isStopped) {
    this.isStopped = isStopped;
  }

}
