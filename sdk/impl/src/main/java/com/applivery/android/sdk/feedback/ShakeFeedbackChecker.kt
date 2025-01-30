package com.applivery.android.sdk.feedback

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.domain.DomainLogger

internal interface ShakeFeedbackChecker {

    fun start()

    fun enable(enable: Boolean)
}

internal class ShakeFeedbackCheckerImpl(
    private val context: Context,
    private val logger: DomainLogger,
    private val shakeDetector: ShakeDetector,
    private val hostActivityProvider: HostActivityProvider
) : ShakeFeedbackChecker, ShakeDetector.Listener, DefaultLifecycleObserver {

    private var isEnabled: Boolean = false

    override fun start() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun enable(enable: Boolean) {
        isEnabled = enable
        if (isEnabled) {
            shakeDetector.start(this)
        } else {
            shakeDetector.stop()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        if (isEnabled) {
            shakeDetector.start(this)
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        shakeDetector.stop()
    }

    override fun onShake(count: Int) {
        val activity = hostActivityProvider.activity
        if (activity == null) {
            logger.noActivityFoundForFeedbackView()
            return
        }
        activity.startActivity(FeedbackActivity.getIntent(context, FeedbackArguments()))
    }
}