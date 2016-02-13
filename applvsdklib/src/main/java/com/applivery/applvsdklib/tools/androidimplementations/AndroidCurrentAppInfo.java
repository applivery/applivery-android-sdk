package com.applivery.applvsdklib.tools.androidimplementations;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.applivery.applvsdklib.domain.appconfig.update.CurrentAppInfo;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/1/16.
 */
public class AndroidCurrentAppInfo implements CurrentAppInfo {

  private final Context context;

  public AndroidCurrentAppInfo(Context context) {
    this.context = context;
  }

  @Override public long getVersionCode() {
    try {
      PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      return pInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      return -1;
    }
  }
}
