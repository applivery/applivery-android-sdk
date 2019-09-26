package com.applivery.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle

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
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
        Companion.activity = activity
    }
}