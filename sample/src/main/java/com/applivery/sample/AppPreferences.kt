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
