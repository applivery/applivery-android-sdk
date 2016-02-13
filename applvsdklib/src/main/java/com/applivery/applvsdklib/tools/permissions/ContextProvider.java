package com.applivery.applvsdklib.tools.permissions;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/1/16.
 */
public interface ContextProvider {

  Activity getCurrentActivity();

  boolean isActivityContextAvailable();

  Context getApplicationContext();

  boolean isApplicationContextAvailable();
}
