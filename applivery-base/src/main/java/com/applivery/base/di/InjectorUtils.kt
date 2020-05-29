package com.applivery.base.di

import android.content.Context
import android.content.SharedPreferences
import com.applivery.base.util.AppliveryContentProvider

internal object InjectorUtils {

    fun provideSharedPreferences(): SharedPreferences {
        return AppliveryContentProvider.context
            .getSharedPreferences("applivery-data", Context.MODE_PRIVATE)
    }
}