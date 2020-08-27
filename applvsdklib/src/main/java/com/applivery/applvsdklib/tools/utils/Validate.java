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

package com.applivery.applvsdklib.tools.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import java.util.Collection;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/10/15.
 */
public final class Validate {

  private static final String TAG = Validate.class.getName();

  private static final String NO_INTERNET_PERMISSION_REASON =
      "No internet permissions granted for the app, please add " +
          "<uses-permission android:name=\"android.permission.INTERNET\" /> " +
          "to your AndroidManifest.xml.";

  public static <T> T notNull(T arg, String name) {
    if (arg == null) {
      throw new NullPointerException("Argument '" + name + "' cannot be null");
    }
    return arg;
  }

  public static <T> void notEmpty(Collection<T> container, String name) {
    if (container.isEmpty()) {
      throw new IllegalArgumentException("Container '" + name + "' cannot be empty");
    }
  }

  public static <T> void containsNoNulls(Collection<T> container, String name) {
    Validate.notNull(container, name);
    for (T item : container) {
      if (item == null) {
        throw new NullPointerException("Container '" + name +
            "' cannot contain null values");
      }
    }
  }

  public static void containsNoNullOrEmpty(Collection<String> container, String name) {
    Validate.notNull(container, name);
    for (String item : container) {
      if (item == null) {
        throw new NullPointerException("Container '" + name +
            "' cannot contain null values");
      }
      if (item.length() == 0) {
        throw new IllegalArgumentException("Container '" + name +
            "' cannot contain empty values");
      }
    }
  }

  public static <T> void notEmptyAndContainsNoNulls(Collection<T> container, String name) {
    Validate.containsNoNulls(container, name);
    Validate.notEmpty(container, name);
  }

  public static void hasInternetPermissions(Context context) {
    Validate.hasInternetPermissions(context, true);
  }

  public static void hasInternetPermissions(Context context, boolean shouldThrow) {
    Validate.notNull(context, "context");
    if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET)
        == PackageManager.PERMISSION_DENIED) {
      if (shouldThrow) {
        throw new IllegalStateException(NO_INTERNET_PERMISSION_REASON);
      } else {
        Log.w(TAG, NO_INTERNET_PERMISSION_REASON);
      }
    }
  }
}
