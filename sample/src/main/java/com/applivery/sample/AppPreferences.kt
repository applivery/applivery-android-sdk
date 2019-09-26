/*
 * Copyright (c) 2019 Applivery
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
package com.applivery.sample

import android.content.Context
import android.content.SharedPreferences

private const val CHECK_FOR_UPDATES_KEY = "CHECK_FOR_UPDATES_KEY"
private const val PREFERENCES_NAME = "com.applivery.sample"

class AppPreferences(context: Context) {
  private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0)

  var checkForUpdatesBackground: Boolean
    get() = preferences.getBoolean(CHECK_FOR_UPDATES_KEY, false)
    set(value) = preferences.edit().putBoolean(CHECK_FOR_UPDATES_KEY, value).apply()
}
