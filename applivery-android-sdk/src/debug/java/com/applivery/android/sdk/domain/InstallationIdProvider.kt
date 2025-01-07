package com.applivery.android.sdk.domain

import androidx.core.content.edit
import java.util.UUID

internal interface InstallationIdProvider {

    val installationId: String
}

internal class InstallationIdProviderImpl(
    private val sharedPreferencesProvider: SharedPreferencesProvider
) : InstallationIdProvider {

    private val preferences get() = sharedPreferencesProvider.sharedPreferences

    override val installationId: String
        get() {
            return preferences.getString(KeyInstallationId, null).orEmpty().ifEmpty {
                UUID.randomUUID().toString().also {
                    preferences.edit { putString(KeyInstallationId, it) }
                }
            }
        }

    companion object {
        private const val KeyInstallationId = "applivery_id_token_key"
    }
}