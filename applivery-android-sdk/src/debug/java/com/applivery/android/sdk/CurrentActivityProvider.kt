package com.applivery.android.sdk

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

internal interface CurrentActivityProvider {

    val activity: Activity?

    fun start()
}

internal class CurrentActivityProviderImpl(
    private val application: Application
) : CurrentActivityProvider, Application.ActivityLifecycleCallbacks {

    private var activityRef = WeakReference<Activity>(null)

    override val activity: Activity? get() = activityRef.get()

    override fun start() {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityRef = WeakReference(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        activityRef = WeakReference(activity)
    }
}