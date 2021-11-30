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

import android.os.Build
import com.applivery.base.util.AndroidCurrentAppInfo
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.Locale

class HeadersInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(composeRequest(chain))
    }

    private fun composeRequest(chain: Interceptor.Chain): Request {
        val original = chain.request()

        val packageInfo = AndroidCurrentAppInfo.getPackageInfo()

        return original.newBuilder()
            .addHeader("Accept-Language", Locale.getDefault().language)
            .addHeader("x-sdk-version", "ANDROID_" + Build.VERSION.SDK_INT)
            .addHeader("x-app-version", packageInfo.versionName)
            .addHeader("x-os-version", Build.VERSION.RELEASE)
            .addHeader("x-os-name", "android")
            .addHeader("x-device-vendor", Build.MANUFACTURER)
            .addHeader("x-device-model", Build.MODEL)
            .addHeader("x-package-name", packageInfo.name)
            .addHeader("x-package-version", packageInfo.version.toString())
            .addHeader("x-os-minsdkversion", "TBD") //TODO
            .addHeader("x-os-targetsdkversion", "TBD") //TODO
            .build()
    }
}
