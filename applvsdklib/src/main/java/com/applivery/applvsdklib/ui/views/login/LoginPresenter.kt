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
package com.applivery.applvsdklib.ui.views.login

import android.net.Uri
import androidx.core.net.toUri
import com.applivery.applvsdklib.BuildConfig
import com.applivery.applvsdklib.features.auth.GetAuthenticationUriUseCase
import com.applivery.base.AppliveryDataManager
import com.applivery.base.domain.PreferencesManager
import com.applivery.base.domain.SessionManager

class LoginPresenter(
    private val getAuthenticationUri: GetAuthenticationUriUseCase,
    private val sessionManager: SessionManager,
    private val preferencesManager: PreferencesManager
) {

    suspend fun getAuthenticationUri(): Result<Uri> {
        val applicationId = AppliveryDataManager.callingPackage
        val redirectScheme = "${applicationId}.${BuildConfig.AuthSchemeSuffix}"
        return getAuthenticationUri.invoke().map {
            it.uri.toUri()
                .buildUpon()
                .appendQueryParameter("scheme", redirectScheme)
                .build()
        }
    }

    fun onAuthenticated(bearer: String) {
        sessionManager.saveSession(bearer)
        preferencesManager.anonymousEmail = null
    }
}
