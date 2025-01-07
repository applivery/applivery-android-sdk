package com.applivery.android.sdk.login.customtabs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi

interface CustomTabIntentResolver {

    fun launch(uri: Uri): Boolean

    companion object {
        fun create(context: Context): CustomTabIntentResolver {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                CustomTabIntentResolverApi30(context)
            } else {
                CustomTabIntentResolverDefault(context)
            }
        }
    }
}

class CustomTabIntentResolverDefault(private val context: Context) : CustomTabIntentResolver {

    override fun launch(uri: Uri): Boolean {
        val pm = context.packageManager

        // Get all Apps that resolve a generic url
        val browserIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.fromParts("http", "", null))
        val genericPackages = pm.queryIntentActivities(browserIntent, 0)
            .map { it.activityInfo.packageName }

        // Get all apps that resolve the specific Url
        val specializedIntent = Intent(Intent.ACTION_VIEW, uri)
            .addCategory(Intent.CATEGORY_BROWSABLE)
        val specializedPackages = pm.queryIntentActivities(specializedIntent, 0)
            .map { it.activityInfo.packageName }

        // Keep only the Urls that resolve the specific, but not the generic
        // urls.
        val packages = specializedPackages - genericPackages.toSet()

        // If the list is empty, no native app handlers were found.
        if (packages.isEmpty()) return false

        // We found native handlers. Launch the Intent.
        specializedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(specializedIntent)
        return true
    }
}

@RequiresApi(Build.VERSION_CODES.R)
class CustomTabIntentResolverApi30(private val context: Context) : CustomTabIntentResolver {

    override fun launch(uri: Uri): Boolean {
        val nativeAppIntent = Intent(Intent.ACTION_VIEW, uri)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER)

        return runCatching { context.startActivity(nativeAppIntent) }.isSuccess
    }
}
