package com.applivery.android.sdk.data.service

import com.applivery.android.sdk.data.auth.SessionManager
import com.applivery.android.sdk.domain.InstallationIdProvider
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

class SessionInterceptor(
    private val sessionManager: SessionManager,
    private val idProvider: InstallationIdProvider,
    private val appToken: String
) : Interceptor {

    override fun intercept(chain: Chain): Response {
        val requestBuilder = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $appToken")
            .addHeader("x-installation-token", idProvider.installationId)

        sessionManager.getToken().onSome {
            requestBuilder.addHeader("x-sdk-auth-token", it)
        }
        val response = chain.proceed(requestBuilder.build())
        if (response.code == CodeUnauthorized) {
            sessionManager.logOut()
        }
        return response
    }

    companion object {
        private const val CodeUnauthorized = 401
    }
}
