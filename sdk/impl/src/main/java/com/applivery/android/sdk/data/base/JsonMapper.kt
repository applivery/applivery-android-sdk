package com.applivery.android.sdk.data.base

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

internal class JsonMapper(val gson: Gson) {

    inline fun <reified T : Any> T.toJson(): String = gson.toJson(this)

    inline fun <reified T : Any> String.fromJson(): T? = try {
        gson.fromJson(this, object : TypeToken<T>() {}.type)
    } catch (e: Throwable) {
        null
    }
}
