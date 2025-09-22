package com.applivery.android.sdk.domain

import androidx.core.content.edit

internal interface AppPreferences {

    var anonymousEmail: String?
}

internal class AppPreferencesImpl(
    private val sharedPreferencesProvider: SharedPreferencesProvider
) : AppPreferences {

    private val preferences get() = sharedPreferencesProvider.sharedPreferences

    override var anonymousEmail: String?
        get() = preferences.getString(KeyAnonymousEmail, null)
        set(value) = preferences.edit { putString(KeyAnonymousEmail, value) }

    companion object {
        private const val KeyAnonymousEmail = "preferences:anonymous_email"
    }
}