package com.applivery.android.sdk.feedback

import android.net.Uri
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.feedback.video.VideoReporter
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

internal sealed interface FeedbackBehavior {
    data class Screenshot(val uri: Uri) : FeedbackBehavior
    object Video : FeedbackBehavior
    object Normal : FeedbackBehavior
}

internal interface FeedbackLauncher {

    fun launchWith(behavior: FeedbackBehavior? = null)
}

internal class FeedbackLauncherImpl(
    private val hostActivityProvider: HostActivityProvider,
    private val logger: DomainLogger,
    private val videoReporter: VideoReporter,
) : FeedbackLauncher {

    var recordingJob: Job? = null
    val coroutineScope = MainScope()

    override fun launchWith(behavior: FeedbackBehavior?) {
        val activity = hostActivityProvider.activity
        if (activity == null) {
            logger.noActivityFoundForFeedbackView()
            return
        }
        if (behavior == null) {
            activity.startActivity(FeedbackSelectorActivity.getIntent(context = activity))
            return
        }
        when (behavior) {
            is FeedbackBehavior.Screenshot,
            is FeedbackBehavior.Normal -> {
                val screenShorUri = when (behavior) {
                    is FeedbackBehavior.Screenshot -> behavior.uri
                    else -> null
                }
                val arguments = FeedbackArguments.Screenshot(uri = screenShorUri)
                activity.startActivity(FeedbackActivity.getIntent(activity, arguments))
            }

            is FeedbackBehavior.Video -> onStartRecordingScreen()
        }
    }

    private fun onStartRecordingScreen() {
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
