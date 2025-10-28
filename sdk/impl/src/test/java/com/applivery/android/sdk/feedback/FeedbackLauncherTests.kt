package com.applivery.android.sdk.feedback

import arrow.core.left
import arrow.core.right
import com.applivery.android.sdk.base.BaseUnitTest
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.FeedbackProgressProvider
import com.applivery.android.sdk.domain.FeedbackProgressUpdater
import com.applivery.android.sdk.domain.ScreenRouter
import com.applivery.android.sdk.domain.model.InternalError
import com.applivery.android.sdk.fakes.FakeLogger
import com.applivery.android.sdk.feedback.video.VideoReporter
import io.mockk.Called
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FeedbackLauncherTests : BaseUnitTest() {

    @Test
    fun `Given feedback is in progress then no more interactions are made`() = runTest {

        val screenRouter = mockk<ScreenRouter>()
        val logger = DomainLogger(FakeLogger())
        val videoReporter = mockk<VideoReporter>()
        val feedbackProgressProvider = mockk<FeedbackProgressProvider> {
            every { isFeedbackInProgress } returns true
        }
        val feedbackProgressUpdater = mockk<FeedbackProgressUpdater>()
        val feedbackLauncher = FeedbackLauncherImpl(
            screenRouter,
            logger,
            videoReporter,
            feedbackProgressProvider,
            feedbackProgressUpdater
        )
        feedbackLauncher.launchWith(null)

        val dependencies = listOf(
            screenRouter,
            videoReporter,
            feedbackProgressUpdater
        )
        verify { dependencies wasNot Called }
    }

    @Test
    fun `Given null behavior then feedback selector screen is launched`() = runTest {

        val behavior: FeedbackBehavior? = null
        val screenRouter = mockk<ScreenRouter> {
            every { toFeedbackSelectorScreen() } returns true
        }
        val logger = DomainLogger(FakeLogger())
        val videoReporter = mockk<VideoReporter>()
        val feedbackProgressProvider = mockk<FeedbackProgressProvider> {
            every { isFeedbackInProgress } returns false
        }
        val feedbackProgressUpdater = mockk<FeedbackProgressUpdater>()
        val feedbackLauncher = FeedbackLauncherImpl(
            screenRouter,
            logger,
            videoReporter,
            feedbackProgressProvider,
            feedbackProgressUpdater
        )
        feedbackLauncher.launchWith(behavior)

        verify { screenRouter.toFeedbackSelectorScreen() }
    }

    @Test
    fun `Given screenshot behavior then feedback screen is launched`() = runTest {

        val behavior = FeedbackBehavior.Screenshot(uri = null)
        val screenRouter = mockk<ScreenRouter> {
            every { toFeedbackScreen(any()) } returns true
        }
        val logger = DomainLogger(FakeLogger())
        val videoReporter = mockk<VideoReporter>()
        val feedbackProgressProvider = mockk<FeedbackProgressProvider> {
            every { isFeedbackInProgress } returns false
        }
        val feedbackProgressUpdater = mockk<FeedbackProgressUpdater>()
        val feedbackLauncher = FeedbackLauncherImpl(
            screenRouter,
            logger,
            videoReporter,
            feedbackProgressProvider,
            feedbackProgressUpdater
        )
        feedbackLauncher.launchWith(behavior)

        verify { screenRouter.toFeedbackScreen(ofType<FeedbackArguments.Screenshot>()) }
        confirmVerified(screenRouter)
    }

    @Test
    fun `Given video behavior when recording fails then error is logged`() = runTest {

        val behavior = FeedbackBehavior.Video
        val screenRouter = mockk<ScreenRouter>()
        val logger = mockk<DomainLogger> {
            every { videoReportingError(any()) } just Runs
        }
        val videoReporter = mockk<VideoReporter> {
            coEvery { start() } returns InternalError().left()
        }
        val feedbackProgressProvider = mockk<FeedbackProgressProvider> {
            every { isFeedbackInProgress } returns false
        }
        val feedbackProgressUpdater = mockk<FeedbackProgressUpdater> {
            every { isFeedbackInProgress = any() } just Runs
        }
        val feedbackLauncher = FeedbackLauncherImpl(
            screenRouter,
            logger,
            videoReporter,
            feedbackProgressProvider,
            feedbackProgressUpdater
        )
        feedbackLauncher.launchWith(behavior)

        verify { screenRouter wasNot Called }
        verify { logger.videoReportingError(any()) }
    }

    @Test
    fun `Given video behavior when recording succeeds then feedback screen is launched`() =
        runTest {

            val videoFileUri = "/video.mp4"
            val behavior = FeedbackBehavior.Video
            val screenRouter = mockk<ScreenRouter> {
                every { toFeedbackScreen(any()) } returns true
            }
            val logger = DomainLogger(FakeLogger())
            val videoReporter = mockk<VideoReporter> {
                coEvery { start() } returns videoFileUri.right()
            }
            val feedbackProgressProvider = mockk<FeedbackProgressProvider> {
                every { isFeedbackInProgress } returns false
            }
            val feedbackProgressUpdater = mockk<FeedbackProgressUpdater> {
                every { isFeedbackInProgress = any() } just Runs
            }
            val feedbackLauncher = FeedbackLauncherImpl(
                screenRouter,
                logger,
                videoReporter,
                feedbackProgressProvider,
                feedbackProgressUpdater
            )
            feedbackLauncher.launchWith(behavior)

            verify {
                screenRouter.toFeedbackScreen(
                    withArg { it is FeedbackArguments.Video && it.uri == videoFileUri }
                )
            }
        }

    @Test
    fun `Given video recording when routing succeeds then feedback progress is updated`() =
        runTest {
            val routingSucceeds = true
            val videoFileUri = "/video.mp4"
            val behavior = FeedbackBehavior.Video
            val screenRouter = mockk<ScreenRouter> {
                every { toFeedbackScreen(any()) } returns routingSucceeds
            }
            val logger = DomainLogger(FakeLogger())
            val videoReporter = mockk<VideoReporter> {
                coEvery { start() } returns videoFileUri.right()
            }
            val feedbackProgressProvider = mockk<FeedbackProgressProvider> {
                every { isFeedbackInProgress } returns false
            }
            val feedbackProgressUpdater = mockk<FeedbackProgressUpdater> {
                every { isFeedbackInProgress = any() } just Runs
            }
            val feedbackLauncher = FeedbackLauncherImpl(
                screenRouter,
                logger,
                videoReporter,
                feedbackProgressProvider,
                feedbackProgressUpdater
            )
            feedbackLauncher.launchWith(behavior)

            verify { feedbackProgressUpdater.isFeedbackInProgress = routingSucceeds }
        }

    @Test
    fun `Given video recording when routing fails then feedback progress is updated`() =
        runTest {
            val routingSucceeds = false
            val videoFileUri = "/video.mp4"
            val behavior = FeedbackBehavior.Video
            val screenRouter = mockk<ScreenRouter> {
                every { toFeedbackScreen(any()) } returns routingSucceeds
            }
            val logger = DomainLogger(FakeLogger())
            val videoReporter = mockk<VideoReporter> {
                coEvery { start() } returns videoFileUri.right()
            }
            val feedbackProgressProvider = mockk<FeedbackProgressProvider> {
                every { isFeedbackInProgress } returns false
            }
            val feedbackProgressUpdater = mockk<FeedbackProgressUpdater> {
                every { isFeedbackInProgress = any() } just Runs
            }
            val feedbackLauncher = FeedbackLauncherImpl(
                screenRouter,
                logger,
                videoReporter,
                feedbackProgressProvider,
                feedbackProgressUpdater
            )
            feedbackLauncher.launchWith(behavior)

            verify { feedbackProgressUpdater.isFeedbackInProgress = routingSucceeds }
        }
}