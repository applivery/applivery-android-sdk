package com.applivery.android.sdk.domain

import android.content.Context
import android.os.Build
import com.applivery.android.sdk.domain.model.PackageInfo
import android.content.pm.PackageInfo as AndroidPackageInfo


internal interface HostAppPackageInfoProvider {

    val packageInfo: PackageInfo
}

internal class AndroidHostAppPackageInfoProvider(
    private val context: Context
) : HostAppPackageInfoProvider {

    override val packageInfo: PackageInfo
        get() {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
            return PackageInfo(
                appName = context.applicationName(),
                packageName = pInfo.packageName,
                versionCode = pInfo.versionCodeCompat,
                versionName = pInfo.versionName,
                minSdkVersion = appInfo.minSdkVersion,
                targetSdkVersion = appInfo.targetSdkVersion
            )
        }

    private val AndroidPackageInfo.versionCodeCompat: Long
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                longVersionCode
            } else {
                versionCode.toLong()
            }
        }

    private fun Context.applicationName(): String {
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) {
            applicationInfo.nonLocalizedLabel.toString()
        } else {
            context.getString(stringId)
        }
    }
}