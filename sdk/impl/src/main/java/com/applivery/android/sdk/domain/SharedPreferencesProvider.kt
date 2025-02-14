package com.applivery.android.sdk.domain

import android.content.Context
import android.content.SharedPreferences

interface SharedPreferencesProvider {

    val sharedPreferences: SharedPreferences
}

internal class AndroidSharedPreferencesProvider(private val context: Context) : SharedPreferencesProvider {

    override val sharedPreferences: SharedPreferences
        get() = context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)

    companion object {
        private const val PreferencesName = "applivery-data"
    }
}