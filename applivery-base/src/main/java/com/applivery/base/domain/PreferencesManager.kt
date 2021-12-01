package com.applivery.base.domain

import android.content.SharedPreferences
import androidx.core.content.edit
import com.applivery.base.di.InjectorUtils

class PreferencesManager(private val preferences: SharedPreferences) {

    var anonymousEmail: String?
        get() = preferences.getString(KeyEmail, null)
        set(value) = preferences.edit { putString(KeyEmail, value) }

    companion object {
        private const val KeyEmail = "preferences:anonymous_email"

        @Volatile
        private var instance: PreferencesManager? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: PreferencesManager(InjectorUtils.provideSharedPreferences()).also {
                    instance = it
                }
            }
    }
}
