/*
 * Copyright (c) 2020 Applivery
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
package com.applivery.data.interceptor

import android.annotation.SuppressLint
import android.os.Build
import com.applivery.base.BuildConfig
import com.applivery.base.util.AndroidCurrentAppInfo
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.*

class HeadersInterceptor : Interceptor {

    private val userAgent: String
        @SuppressLint("DefaultLocale") get() =
            String.format(
                "Android/%s; vendor/%s; model/%s; build/%d;", Build.VERSION.RELEASE,
                Build.MANUFACTURER, Build.MODEL, BuildConfig.VERSION_CODE
            )

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(composeRequest(chain))
    }

    private fun composeRequest(chain: Interceptor.Chain): Request {
        val original = chain.request()

        val packageInfo = AndroidCurrentAppInfo.getPackageInfo()

        return original.newBuilder()
            .url(chain.request().url())
            .addHeader("Accept-Language", Locale.getDefault().language)
            .addHeader("x-sdk-version", "ANDROID_" + BuildConfig.VERSION_NAME)
            .addHeader("x-app-version", packageInfo.versionName)

            .addHeader("x-os-version", Build.VERSION.RELEASE)
            .addHeader("x-os-name", "android")
            .addHeader("x-device-vendor", Build.MANUFACTURER)
            .addHeader("x-device-model", Build.MODEL)
            .addHeader("x-package-name", packageInfo.name)
            .addHeader("x-package-version", packageInfo.version.toString())
            .addHeader("x-os-minsdkversion", Build.VERSION.SDK_INT.toString())
            .addHeader("x-os-targetsdkversion", BuildConfig.VERSION_NAME)

            .method(original.method(), original.body())
            .build()
    }
}
