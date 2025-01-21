package com.applivery.android.sdk.feedback

import android.content.Context
import android.net.Uri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.applivery.android.sdk.CurrentActivityProvider
import com.applivery.android.sdk.domain.DomainLogger

internal interface ScreenshotFeedbackChecker {

    fun start()

    fun enable(enable: Boolean)
}

internal class ScreenshotFeedbackCheckerImpl(
    private val context: Context,
    private val logger: DomainLogger,
    private val currentActivityProvider: CurrentActivityProvider
) : ScreenshotFeedbackChecker, DefaultLifecycleObserver {

    private var isEnabled: Boolean = false

    private val screenshotObserver = ScreenshotObserver(context, logger, ::onScreenshotDetected)

    override fun start() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun enable(enable: Boolean) {
        isEnabled = enable
        if (isEnabled) {
            screenshotObserver.register()
        } else {
            screenshotObserver.unregister()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        if (isEnabled) {
            screenshotObserver.register()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        screenshotObserver.unregister()
    }

    private fun onScreenshotDetected(uri: Uri) {
        val activity = currentActivityProvider.activity
        if (activity == null) {
            logger.noActivityFoundForFeedbackView()
            return
        }
        val arguments = FeedbackArguments(screenshotUri = uri)
        activity.startActivity(FeedbackActivity.getIntent(context, arguments))
    }
}