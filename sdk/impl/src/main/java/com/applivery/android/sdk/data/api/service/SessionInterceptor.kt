package com.applivery.android.sdk.data.api.service

import com.applivery.android.sdk.data.auth.SessionManager
import com.applivery.android.sdk.domain.AppPreferences
import com.applivery.android.sdk.login.LoginHandler
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response

internal class SessionInterceptor(
    private val sessionManager: SessionManager,
    private val appPreferences: AppPreferences,
    private val appToken: String,
    private val loginHandler: LoginHandler
) : Interceptor {

    override fun intercept(chain: Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request.intercept())
        if (response.code == HttpCodeUnauthorized) {
            return synchronized(Lock) {
                if (sessionManager.isLoggedIn) {
                    chain.proceed(request.intercept())
                } else {
                    runBlocking { loginHandler.invoke() }.fold(
                        ifLeft = { response },
                        ifRight = { chain.proceed(request.intercept()) }
                    )
                }
            }
        }
        return response
    }

    private fun Request.intercept(): Request {
        return newBuilder()
            .addHeader("Authorization", "Bearer $appToken")
            .addHeader("x-installation-token", appPreferences.installationId)
            .apply { sessionManager.getToken().onSome { addHeader("x-sdk-auth-token", it) } }
            .build()
    }

    companion object {
        private val Lock = Any()
        private const val HttpCodeUnauthorized = 401
    }
}
