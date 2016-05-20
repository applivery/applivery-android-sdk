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

  @Override public String getPackageName() {
    return context.getPackageName();
  }

  public String getVersionName() {
    try {
      PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      return pInfo.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      return "0.0";
    }
  }
}
