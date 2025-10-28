package com.applivery.android.sdk.feedback

import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.FeedbackProgressProvider
import com.applivery.android.sdk.domain.FeedbackProgressUpdater
import com.applivery.android.sdk.domain.ScreenRouter
import com.applivery.android.sdk.feedback.video.VideoReporter
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal sealed interface FeedbackBehavior {
    data class Screenshot(val uri: String?) : FeedbackBehavior
    object Video : FeedbackBehavior
}

internal interface FeedbackLauncher {

    fun launchWith(behavior: FeedbackBehavior? = null)
}

internal class FeedbackLauncherImpl(
    private val screenRouter: ScreenRouter,
    private val logger: DomainLogger,
    private val videoReporter: VideoReporter,
    private val feedbackProgressProvider: FeedbackProgressProvider,
    private val feedbackProgressUpdater: FeedbackProgressUpdater,
) : FeedbackLauncher {

    private var recordingJob: Job? = null
    private val coroutineScope = MainScope()

    override fun launchWith(behavior: FeedbackBehavior?) {
        if (feedbackProgressProvider.isFeedbackInProgress) return

        when (behavior) {
            is FeedbackBehavior.Screenshot -> {
                val arguments = FeedbackArguments.Screenshot(uri = behavior.uri)
                screenRouter.toFeedbackScreen(arguments)
            }

            is FeedbackBehavior.Video -> onStartRecordingScreen()
            null -> screenRouter.toFeedbackSelectorScreen()
        }
    }

    private fun onStartRecordingScreen() {
        feedbackProgressUpdater.isFeedbackInProgress = true

        if (recordingJob?.isActive == true) {
            logger.onAlreadyRecording()
            return
        }

        recordingJob = coroutineScope.launch {
            videoReporter.start().fold(
                ifLeft = { error ->
                    logger.videoReportingError(error)
                    feedbackProgressUpdater.isFeedbackInProgress = false
                },
                ifRight = ::onScreenRecordingReady
            )
        }
    }

    private fun onScreenRecordingReady(videoUri: String) {
        val arguments = FeedbackArguments.Video(uri = videoUri)
        val isRoutingSuccess = screenRouter.toFeedbackScreen(arguments)
        feedbackProgressUpdater.isFeedbackInProgress = isRoutingSuccess
    }
}
