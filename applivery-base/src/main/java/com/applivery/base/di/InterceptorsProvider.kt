package com.applivery.base.di

import com.applivery.base.data.HeadersInterceptor
import com.applivery.base.data.SessionInterceptor
import okhttp3.logging.HttpLoggingInterceptor

object InterceptorsProvider {

    fun provideHeadersInterceptor(): HeadersInterceptor {
        return HeadersInterceptor()
    }

    fun provideSessionInterceptor(): SessionInterceptor {
        return SessionInterceptor(SessionManagerProvider.provideSessionManager())
    }

    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return loggingInterceptor
    }
}