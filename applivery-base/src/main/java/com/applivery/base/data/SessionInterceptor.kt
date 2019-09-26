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
package com.applivery.base.data

import com.applivery.base.AppliveryDataManager
import com.applivery.base.domain.SessionManager
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

class SessionInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Chain): Response {

        val requestBuilder = chain.request().newBuilder()

        requestBuilder.addHeader("Authorization", "Bearer " + AppliveryDataManager.appToken)

        if (sessionManager.hasSession()) {
            requestBuilder.addHeader("x-sdk-auth-token", sessionManager.getSession())
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        if (response.code() == UNAUTHORIZED) {
            sessionManager.clearSession()
        }

        return response
    }

    companion object {
        private const val UNAUTHORIZED = 401
    }
}
