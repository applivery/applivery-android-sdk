package com.applivery.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.applivery.base.util.AppliveryLog

class AppliveryLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var activity: Activity? = null
            private set
    }

    override fun onActivityPaused(activity: Activity) {
        Companion.activity = null
    }

    override fun onActivityStarted(activity: Activity) {
        AppliveryLog.debug("onActivityStarted")
    }

    override fun onActivityDestroyed(activity: Activity) {
        AppliveryLog.debug("onActivityDestroyed")
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
        AppliveryLog.debug("onActivitySaveInstanceState")
    }

    override fun onActivityStopped(activity: Activity) {
        AppliveryLog.debug("onActivityStopped")
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        AppliveryLog.debug("onActivityCreated")
    }

    override fun onActivityResumed(activity: Activity) {
        Companion.activity = activity
    }
}
