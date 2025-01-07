package com.applivery.android.sdk.login.customtabs

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class CustomTabsManager(private val context: Context) : DefaultLifecycleObserver {

    private val packageNameToUse: String? by lazy { getPackageNameToUse(context) }

    private val intentResolver = CustomTabIntentResolver.create(context)

    private var serviceConnection = object : CustomTabsServiceConnection() {

        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
            customTabsClient = client.apply { warmup(0L) }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            customTabsClient = null
        }
    }

    private var customTabsClient: CustomTabsClient? = null

    private var customTabsSession: CustomTabsSession? = null

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        packageNameToUse?.let {
            CustomTabsClient.bindCustomTabsService(
                context,
                it,
                serviceConnection
            )
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        customTabsClient?.let { context.unbindService(serviceConnection) }
    }

    fun mayLaunch(uri: Uri) {
        customTabsSession = createSession(uri)
    }

    fun launch(uri: Uri) {
        if (uri.toString().isBlank()) return

        if (!intentResolver.launch(uri)) {
            createTabBuilder().build().launchUrl(context, uri)
        }
    }

    private fun createTabBuilder(): CustomTabsIntent.Builder =
        CustomTabsIntent.Builder(customTabsSession)
            .setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder().build())

    private fun createSession(futureUri: Uri): CustomTabsSession? =
        customTabsClient?.let {
            it.newSession(null)?.apply {
                mayLaunchUrl(futureUri, null, null)
            }
        }

    companion object {

        private const val CHROME_PACKAGE = "com.android.chrome"
        private const val DUMMY_URI = "http://www.example.com"

        private var packageNameToUse: String? = null

        private fun getPackageNameToUse(context: Context): String? {
            if (packageNameToUse != null) return packageNameToUse

            val pm = context.packageManager
            // Get default VIEW intent handler.
            val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse(DUMMY_URI))
            val defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0)
            val defaultViewHandlerPackageName = defaultViewHandlerInfo?.let {
                defaultViewHandlerInfo.activityInfo.packageName
            }

            // Get all apps that can handle VIEW intents.
            val resolvedActivityList = pm.queryIntentActivities(activityIntent, 0)
            val packagesSupportingCustomTabs = mutableListOf<String>()
            for (info in resolvedActivityList) {
                val serviceIntent = Intent()
                serviceIntent.action = ACTION_CUSTOM_TABS_CONNECTION
                serviceIntent.`package` = info.activityInfo.packageName
                if (pm.resolveService(serviceIntent, 0) != null) {
                    packagesSupportingCustomTabs.add(info.activityInfo.packageName)
                }
            }

            // Now packagesSupportingCustomTabs contains all apps that can handle both VIEW intents
            // and service calls.
            if (packagesSupportingCustomTabs.isEmpty()) {
                packageNameToUse = null
            } else if (packagesSupportingCustomTabs.size == 1) {
                packageNameToUse = packagesSupportingCustomTabs.first()
            } else if (defaultViewHandlerPackageName.orEmpty().isNotBlank() &&
                !hasSpecializedHandlerIntents(context, activityIntent) &&
                packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)
            ) {
                packageNameToUse = defaultViewHandlerPackageName
            } else if (packagesSupportingCustomTabs.contains(CHROME_PACKAGE))
                packageNameToUse = CHROME_PACKAGE
            return packageNameToUse
        }

        /**
         * Used to check whether there is a specialized handler for a given intent.
         * @param intent The intent to check with.
         * @return Whether there is a specialized handler for the given intent.
         */
        private fun hasSpecializedHandlerIntents(context: Context, intent: Intent): Boolean {
            try {
                val pm = context.packageManager
                val handlers = pm.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER)
                if (handlers.isEmpty()) return false

                for (resolveInfo in handlers) {
                    val filter = resolveInfo.filter ?: continue
                    if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) continue
                    if (resolveInfo.activityInfo == null) continue
                    return true
                }
            } catch (e: RuntimeException) {
                return false
            }

            return false
        }
    }
}
