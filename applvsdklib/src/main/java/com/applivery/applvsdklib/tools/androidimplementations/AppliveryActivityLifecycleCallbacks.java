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
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.appconfig.update.AppConfigChecker;
import com.applivery.applvsdklib.domain.appconfig.update.LastConfigReader;
import com.applivery.applvsdklib.tools.androidimplementations.sensors.SensorEventsController;
import com.applivery.applvsdklib.tools.permissions.ContextProvider;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 4/1/16.
 */
public class AppliveryActivityLifecycleCallbacks
    implements Application.ActivityLifecycleCallbacks, ContextProvider {

  private Stack<ActivityLifecyleWrapper> activityStack = new Stack<>();
  private final AppConfigChecker appConfigChecker;
  private final Context applicationContext;
  private final SensorEventsController sensorEventsController;
  private final Set<String> activitiesOnRotation = new HashSet();

  public AppliveryActivityLifecycleCallbacks(Context applicationContext) {
    LastConfigReader lastConfigReader = new AndroidLastConfigReaderImpl();
    this.appConfigChecker = new AppConfigChecker(lastConfigReader);
    this.applicationContext = applicationContext;
    this.sensorEventsController = SensorEventsController.getInstance(applicationContext);
  }

  public Activity getCurrentActivity() {

    for (ActivityLifecyleWrapper activityLifecyleWrapper : activityStack) {
      if (!activityLifecyleWrapper.isPaused()) {
        return activityLifecyleWrapper.getActivity();
      }
    }

    for (ActivityLifecyleWrapper activityLifecyleWrapper : activityStack) {
      if (!activityLifecyleWrapper.isStopped()) {
        return activityLifecyleWrapper.getActivity();
      }
    }
    return null;
  }

  public boolean isActivityContextAvailable() {
    return (getCurrentActivity() != null);
  }

  @Override public Context getApplicationContext() {
    return applicationContext;
  }

  @Override public boolean isApplicationContextAvailable() {
    return (applicationContext != null) ? true : false;
  }

  //region Activity lifecycle Management

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

  @Override public void onActivityStarted(Activity activity) {

    if (activitiesOnRotation.contains(activity.getPackageName() + activity.getLocalClassName())){
      activitiesOnRotation.remove(activity.getPackageName() + activity.getLocalClassName());
    }else{
      if (activityStack.empty() && !activity.isChangingConfigurations()){
        appWillReturnfromBackground();
      }
    }

    this.activityStack.push(new ActivityLifecyleWrapper(activity, true, false));
  }

  private void appWillReturnfromBackground() {
    checkForUpdates();
    sensorEventsController.registerAllSensorsForApplication();
  }

  private void checkForUpdates() {
    if (appConfigChecker.shouldCheckAppConfigForUpdate()) {
      AppliverySdk.obtainAppConfigForCheckUpdates();
      AppliverySdk.continuePendingPermissionsRequestsIfPossible();
    }
  }

  @Override public void onActivityResumed(Activity activity) {
    try {
      activity.getChangingConfigurations();
      activityStack.peek().setIsPaused(false);
    } catch (Exception e) {
      AppliverySdk.Logger.log(e.getMessage());
    }

  }

  @Override public void onActivityPaused(Activity activity) {
    try {
      activityStack.peek().setIsPaused(true);
    } catch (Exception e) {
      AppliverySdk.Logger.log(e.getMessage());
    }
  }

  @Override public void onActivityStopped(Activity activity) {
    try {
      if (activity.isChangingConfigurations()){
        activitiesOnRotation.add(activity.getPackageName() + activity.getLocalClassName());
      }else if (activityStack.size() <= 1){
          appWillEnterBackground();
      }
      removeActivityFromStack(activity);
    } catch (Exception e) {
      AppliverySdk.Logger.log(e.getMessage());
    }
  }

  private void appWillEnterBackground() {
    sensorEventsController.unRegisterAllSensorsForApplication();
  }

  @Override public void onActivityDestroyed(Activity activity) {}

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
  }

  //endregion

  //region StackUtils

  private void removeActivityFromStack(Activity activity) {
    Iterator<ActivityLifecyleWrapper> iter = activityStack.iterator();
    while (iter.hasNext()) {
      ActivityLifecyleWrapper activityLifecyleWrapper = iter.next();
      if (activityLifecyleWrapper.getActivity().equals(activity)) {
        activityStack.remove(activityLifecyleWrapper);
      }
    }
  }

  //endregion
}
