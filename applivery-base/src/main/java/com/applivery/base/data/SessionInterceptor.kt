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