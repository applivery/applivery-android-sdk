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

package com.applivery.applvsdklib.tools.androidimplementations;

import android.app.Activity;
import java.lang.ref.WeakReference;


public class ActivityLifecyleWrapper {
  private WeakReference<Activity> activity;
  private boolean isPaused;
  private boolean isStopped;

  public ActivityLifecyleWrapper(Activity activity, boolean isPaused, boolean isStopped) {
    this.activity = new WeakReference<>(activity);
    this.isPaused = isPaused;
    this.isStopped = isStopped;
  }

  public Activity getActivity() {
    if (activity!=null){
      return activity.get();
    }else{
      return null;
    }
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

  public void setActivity(Activity activity) {
    if (activity == null){
      this.activity = new WeakReference<>(activity);
    }
  }

}
