package com.applivery.android.sdk.feedback

import android.net.Uri
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.feedback.video.VideoReporter
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

internal interface FeedbackLauncher {

    fun startFeedbackScreenshot(uri: Uri? = null)

    fun startFeedbackVideo()

    fun openFeedbackSelector()
}

internal class FeedbackLauncherImpl(
    private val hostActivityProvider: HostActivityProvider,
    private val logger: DomainLogger,
    private val videoReporter: VideoReporter,
) : FeedbackLauncher {

    var recordingJob: Job? = null
    val coroutineScope = MainScope()

    override fun startFeedbackScreenshot(uri: Uri?) {
        onStartScreenshotBehavior(uri)
    }

    override fun startFeedbackVideo() {
        onStartVideoBehavior()
    }

    override fun openFeedbackSelector() {
        val activity = hostActivityProvider.activity
        if (activity == null) {
            logger.noActivityFoundForFeedbackView()
            return
        }

        activity.startActivity(FeedbackSelectorActivity.getIntent(context = activity))
    }

    private fun onStartScreenshotBehavior(uri: Uri?) {
        val activity = hostActivityProvider.activity
        if (activity == null) {
            logger.noActivityFoundForFeedbackView()
            return
        }

        val arguments = FeedbackArguments.Screenshot(uri = uri)
        activity.startActivity(FeedbackActivity.getIntent(activity, arguments))
    }

    private fun onStartVideoBehavior() {
        if (recordingJob?.isActive == true) {
            logger.onAlreadyRecording()
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