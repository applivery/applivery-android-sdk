package com.applivery.base.di

import android.content.Context
import com.applivery.base.domain.SessionManager
import com.applivery.base.util.AppliveryContentProvider

object SessionManagerProvider {
    internal fun provideSessionManager(): SessionManager {
        return SessionManager(
            AppliveryContentProvider.context
                .getSharedPreferences("applivery-session", Context.MODE_PRIVATE)
        )
    }
}