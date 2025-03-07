package com.applivery.android.sdk.feedback

import android.net.Uri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.FeedbackProgressProvider
import com.applivery.android.sdk.domain.model.ShakeFeedbackBehavior
import com.applivery.android.sdk.feedback.video.VideoReporter
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

internal interface ShakeFeedbackChecker {

    fun start()

    fun enable(behavior: ShakeFeedbackBehavior)

    fun disable()
}

internal class ShakeFeedbackCheckerImpl(
    private val shakeDetector: ShakeDetector,
    private val logger: DomainLogger,
    private val hostActivityProvider: HostActivityProvider,
    private val videoReporter: VideoReporter,
    private val feedbackProgressProvider: FeedbackProgressProvider
) : ShakeFeedbackChecker, ShakeDetector.Listener, DefaultLifecycleObserver {

    private var isEnabled: Boolean = false
    private var currentBehavior: ShakeFeedbackBehavior = ShakeFeedbackBehavior.Normal

    private val coroutineScope = MainScope()
    private var recordingJob: Job? = null

    override fun start() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun enable(behavior: ShakeFeedbackBehavior) {
        isEnabled = true
        currentBehavior = behavior
        shakeDetector.start(this)
    }

    override fun disable() {
        isEnabled = false
        shakeDetector.stop()
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
        if (feedbackProgressProvider.isFeedbackInProgress) return

        when (currentBehavior) {
            ShakeFeedbackBehavior.Normal -> onShakeForNormalBehavior()
            ShakeFeedbackBehavior.Video -> onShakeForVideoBehavior()
        }
    }

    private fun onShakeForNormalBehavior() {
        val activity = hostActivityProvider.activity
        if (activity == null) {
            logger.noActivityFoundForFeedbackView()
            return
        }
        val arguments = FeedbackArguments.Screenshot()
        activity.startActivity(FeedbackActivity.getIntent(activity, arguments))
    }

    private fun onShakeForVideoBehavior() {
        if (recordingJob?.isActive == true) {
            logger.onShakeDetectedAlreadyRecording()
            return
        }

        recordingJob = coroutineScope.launch {
            videoReporter.start().fold(
                ifLeft = logger::videoReportingError,
                ifRight = ::onScreenRecordingReady
            )
        }
    }

    private fun onScreenRecordingReady(file: File) {
        val activity = hostActivityProvider.activity
        if (activity == null) {
            logger.noActivityFoundForFeedbackView()
            return
        }
        val arguments = FeedbackArguments.Video(uri = Uri.fromFile(file))
        activity.startActivity(FeedbackActivity.getIntent(activity, arguments))
    }
}