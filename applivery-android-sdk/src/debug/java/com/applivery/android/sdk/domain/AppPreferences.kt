package com.applivery.android.sdk.domain

import androidx.core.content.edit
import java.util.UUID

internal interface AppPreferences {

    val installationId: String
    var anonymousEmail: String?
}

internal class AppPreferencesImpl(
    private val sharedPreferencesProvider: SharedPreferencesProvider
) : AppPreferences {

    private val preferences get() = sharedPreferencesProvider.sharedPreferences

    override val installationId: String
        get() {
            return preferences.getString(KeyInstallationId, null).orEmpty().ifEmpty {
                UUID.randomUUID().toString().also {
                    preferences.edit { putString(KeyInstallationId, it) }
                }
            }
        }

    override var anonymousEmail: String?
        get() = preferences.getString(KeyAnonymousEmail, null)
        set(value) = preferences.edit { putString(KeyAnonymousEmail, value) }

    companion object {
        private const val KeyInstallationId = "applivery_id_token_key"
        private const val KeyAnonymousEmail = "preferences:anonymous_email"
    }
}