package com.applivery.android.sdk.data.repository.identifier

import androidx.core.content.edit
import arrow.core.Either
import arrow.core.raise.either
import com.applivery.android.sdk.domain.SharedPreferencesProvider
import com.applivery.android.sdk.domain.model.DomainError
import java.util.UUID

internal class InstallationIdProvider(
    private val preferencesProvider: SharedPreferencesProvider
) : DeviceIdProvider {

    private val preferences get() = preferencesProvider.sharedPreferences

    private val randomId get() = UUID.randomUUID().toString()

    override suspend fun getDeviceId(): Either<DomainError, String> = either {
        preferences.getString(KeyInstallationId, null).orEmpty().ifEmpty {
            randomId.also { preferences.edit { putString(KeyInstallationId, it) } }
        }
    }
}

private const val KeyInstallationId = "applivery_id_token_key"