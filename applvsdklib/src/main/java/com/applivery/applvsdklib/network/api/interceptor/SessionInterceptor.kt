package com.applivery.applvsdklib.network.api.interceptor

import com.applivery.applvsdklib.AppliverySdk
import com.applivery.applvsdklib.tools.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

class SessionInterceptor(private val sessionManager: SessionManager) : Interceptor {

  override fun intercept(chain: Chain): Response {

    val requestBuilder = chain.request().newBuilder()

    if (sessionManager.hasSession()) {
      requestBuilder.addHeader("x_account_token", sessionManager.getSession())
    }

    requestBuilder.addHeader("Authorization", AppliverySdk.getToken())


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