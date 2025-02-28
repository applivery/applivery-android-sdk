package com.applivery.android.sdk.feedback

import android.net.Uri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.feedback.video.VideoReporter
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

internal interface ShakeFeedbackChecker {

    fun start()

    fun enable(enable: Boolean)
}

internal class ShakeFeedbackCheckerImpl(
    private val shakeDetector: ShakeDetector,
    private val logger: DomainLogger,
    private val hostActivityProvider: HostActivityProvider,
    private val videoReporter: VideoReporter,
) : ShakeFeedbackChecker, ShakeDetector.Listener, DefaultLifecycleObserver {

    private var isEnabled: Boolean = false

    private val coroutineScope = MainScope()
    private var recordingJob: Job? = null

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
        if (recordingJob?.isActive == true) {
            logger.onShakeDetectedAlreadyRecording()
            return
        }

        // TODO: if we are in FeedbackActivity do not start recording
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