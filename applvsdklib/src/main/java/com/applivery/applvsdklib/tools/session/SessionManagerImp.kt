package com.applivery.applvsdklib.tools.session

import android.annotation.SuppressLint
import android.content.SharedPreferences

class SessionManagerImp(private val sharedPreferences: SharedPreferences) : SessionManager {

  private val TOKEN_KEY = "token_key"
  private var token = ""

  @SuppressLint("ApplySharedPref")
  override fun saveSession(token: String) {
    this.token = token
    val editor = sharedPreferences.edit()
    editor?.putString(TOKEN_KEY, token)
    editor?.commit()
  }

  override fun getSession(): String {
    return if (token.isNotEmpty()) {
      token
    } else {
      sharedPreferences.getString(TOKEN_KEY, "")
    }
  }

  override fun hasSession(): Boolean = getSession().isNotEmpty()

  @SuppressLint("ApplySharedPref")
  override fun clearSession() {
    token = ""
    val editor = sharedPreferences.edit()
    editor?.putString(TOKEN_KEY, token)
    editor?.commit()
  }
}