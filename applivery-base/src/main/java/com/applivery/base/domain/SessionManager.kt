/*
 * Copyright (c) 2019 Applivery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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