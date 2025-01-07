package com.applivery.android.sdk.data.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import arrow.core.Option
import com.applivery.android.sdk.domain.SharedPreferencesProvider

internal interface SessionManager {

    val isLoggedIn: Boolean

    fun saveToken(token: String)

    fun getToken(): Option<String>

    fun logOut()
}

internal class SessionManagerImpl(
    private val preferencesProvider: SharedPreferencesProvider
) : SessionManager {

    private val preferences: SharedPreferences get() = preferencesProvider.sharedPreferences

    override val isLoggedIn: Boolean get() = getToken().isSome()

    override fun saveToken(token: String) {
        preferences.edit { putString(KeyToken, token) }
    }

    override fun getToken(): Option<String> {
        return Option.fromNullable(preferences.getString(KeyToken, null))
    }

    override fun logOut() {
        preferences.edit { remove(KeyToken) }
    }

    companion object {
        private const val KeyToken = "token_key"
    }
}