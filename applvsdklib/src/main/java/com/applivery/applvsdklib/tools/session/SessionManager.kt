package com.applivery.applvsdklib.tools.session

import android.content.SharedPreferences

interface SessionManager {

  fun saveSession(token: String)

  fun getSession(): String

  fun hasSession(): Boolean

  fun clearSession()

  companion object Factory {

    var sessionManager: SessionManager? = null

    fun create(sharedPreferences: SharedPreferences): SessionManager {
      if (sessionManager == null) {
        sessionManager = SessionManagerImp(sharedPreferences)
      }

      return sessionManager as SessionManager
    }
  }
}