package com.applivery.android.sdk.feedback.screenshot

import android.content.Context
import android.net.Uri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.FeedbackProgressProvider
import com.applivery.android.sdk.feedback.FeedbackBehavior
import com.applivery.android.sdk.feedback.FeedbackLauncher
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal interface ScreenshotFeedbackChecker {

    fun start()

    fun enable(enable: Boolean)
}

internal class ScreenshotFeedbackCheckerImpl(
    context: Context,
    logger: DomainLogger,
    private val feedbackProgressProvider: FeedbackProgressProvider,
    private val feedbackLauncher: FeedbackLauncher
) : ScreenshotFeedbackChecker, DefaultLifecycleObserver {

    private var isEnabled: Boolean = false
    private val coroutineScope = MainScope()

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
        if (feedbackProgressProvider.isFeedbackInProgress) return
        coroutineScope.launch { feedbackLauncher.launchWith(behavior = FeedbackBehavior.Screenshot(uri)) }
    }
}
