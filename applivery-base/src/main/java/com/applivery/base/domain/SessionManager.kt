package com.applivery.base.domain

import android.annotation.SuppressLint
import android.content.SharedPreferences

class SessionManager(private val sharedPreferences: SharedPreferences) {

    private var token = ""

    @SuppressLint("ApplySharedPref")
    fun saveSession(token: String) {
        this.token = token
        val editor = sharedPreferences.edit()
        editor?.putString(TOKEN_KEY, token)
        editor?.commit()
    }

    fun getSession(): String {
        return if (token.isNotEmpty()) {
            token
        } else {
            sharedPreferences.getString(TOKEN_KEY, "") ?: ""
        }
    }

    fun hasSession(): Boolean = getSession().isNotEmpty()

    @SuppressLint("ApplySharedPref")
    fun clearSession() {
        token = ""
        val editor = sharedPreferences.edit()
        editor?.remove(TOKEN_KEY)
        editor?.commit()
    }

    companion object {
        private const val TOKEN_KEY = "token_key"
    }
}