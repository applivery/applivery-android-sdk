package com.applivery.applvsdklib.tools.androidimplementations;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.appconfig.update.AppConfigChecker;
import com.applivery.applvsdklib.domain.appconfig.update.LastConfigReader;
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
  private final Set<String> activitiesOnRotation = new HashSet();

  public AppliveryActivityLifecycleCallbacks(Context applicationContext) {
    LastConfigReader lastConfigReader = new AndroidLastConfigReaderImpl();
    this.appConfigChecker = new AppConfigChecker(lastConfigReader);
    this.applicationContext = applicationContext;
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
        checkForUpdates();
      }
    }

    this.activityStack.push(new ActivityLifecyleWrapper(activity, true, false));
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
      //TODO Register for phase 2 shake detector
    } catch (Exception e) {
      AppliverySdk.Logger.log(e.getMessage());
    }

  }

  @Override public void onActivityPaused(Activity activity) {
    try {
      activityStack.peek().setIsPaused(true);
      //TODO Unregister for phase 2 shake detector
    } catch (Exception e) {
      AppliverySdk.Logger.log(e.getMessage());
    }
  }

  @Override public void onActivityStopped(Activity activity) {
    try {
      if (activity.isChangingConfigurations()){
        activitiesOnRotation.add(activity.getPackageName() + activity.getLocalClassName());
      }
      removeActivityFromStack(activity);
    } catch (Exception e) {
      AppliverySdk.Logger.log(e.getMessage());
    }
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
