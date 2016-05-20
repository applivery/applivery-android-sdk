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
import android.content.res.Configuration;
import android.os.Build;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.feedback.DeviceDetailsInfo;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/4/16.
 */
public class AndroidDeviceDetailsInfo implements DeviceDetailsInfo {

  private static final String ANDROID = "Android";
  private static final String TABLET = "tablet";
  private static final String MOBILE = "mobile";


  @Override public String getOsName() {
    return ANDROID;
  }

  @Override public String getVendor() {
    return Build.MANUFACTURER;
  }

  @Override public String getModel() {
    String model = Build.MODEL;
    if (model.contains(Build.MANUFACTURER+" ")){
      return model.replace(Build.MANUFACTURER+" ", "");
    } else {
      return model;
    }
  }

  @Override public String getDeviceType() {
    Context context = AppliverySdk.getApplicationContext();

    boolean isTablet = false;

    isTablet = (context.getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;

    if (isTablet){
      return TABLET;
    } else{
      return MOBILE;
    }
  }

  @Override public String getOsversion() {
    return Build.VERSION.RELEASE;
  }
}
