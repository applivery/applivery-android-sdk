package com.applivery.android.sdk.data.service

import android.os.Build
import com.applivery.android.sdk.BuildConfig
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.Locale

class HeadersInterceptor(
    private val packageInfoProvider: HostAppPackageInfoProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(composeRequest(chain))
    }

    private fun composeRequest(chain: Interceptor.Chain): Request {
        val original = chain.request()
        val packageInfo = packageInfoProvider.packageInfo
        return original.newBuilder()
            .addHeader("Accept-Language", Locale.getDefault().language)
            .addHeader("x-sdk-version", "ANDROID_" + BuildConfig.LibraryVersion)
            .addHeader("x-app-version", packageInfo.versionName)
            .addHeader("x-os-version", Build.VERSION.RELEASE)
            .addHeader("x-os-name", "android")
            .addHeader("x-device-vendor", Build.MANUFACTURER)
            .addHeader("x-device-model", Build.MODEL)
            .addHeader("x-package-name", packageInfo.packageName)
            .addHeader("x-package-version", packageInfo.versionCode.toString())
            .addHeader("x-os-minsdkversion", packageInfo.minSdkVersion.toString())
            .addHeader("x-os-targetsdkversion", packageInfo.targetSdkVersion.toString())
            .build()
    }
}
