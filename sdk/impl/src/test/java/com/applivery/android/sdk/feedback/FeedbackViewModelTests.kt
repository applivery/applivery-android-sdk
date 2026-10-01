package com.applivery.android.sdk.feedback

import android.graphics.Bitmap
import android.net.Uri
import arrow.core.left
import com.applivery.android.sdk.base.BaseUnitTest
import com.applivery.android.sdk.domain.model.InternalError
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FeedbackViewModelTests : BaseUnitTest() {

    private val screenshot = mockk<Bitmap>()

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk()
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    @Test
    fun `Given a screenshot uri then it is attached and can be re-attached`() {
        val viewModel = createViewModel(uri = "content://screenshot", decoded = screenshot)
        viewModel.load()

        assertEquals(FeedbackAttachment.Screenshot(screenshot), viewModel.getState().attachment)
        assertEquals(ScreenshotStatus.Available, viewModel.getState().screenshotStatus)

        viewModel.sendIntent(FeedbackIntent.AttachScreenshot(false))
        assertNull(viewModel.getState().attachment)

        viewModel.sendIntent(FeedbackIntent.AttachScreenshot(true))
        assertEquals(FeedbackAttachment.Screenshot(screenshot), viewModel.getState().attachment)
    }

    @Test
    fun `Given a modified screenshot then it is the one restored when re-attached`() {
        val modified = mockk<Bitmap>()
        val viewModel = createViewModel(uri = "content://screenshot", decoded = screenshot)
        viewModel.load()

        viewModel.sendIntent(FeedbackIntent.ScreenshotModified(modified))
        viewModel.sendIntent(FeedbackIntent.AttachScreenshot(false))
        viewModel.sendIntent(FeedbackIntent.AttachScreenshot(true))

        assertEquals(FeedbackAttachment.Screenshot(modified), viewModel.getState().attachment)
    }

    @Test
    fun `Given no screenshot then it can not be attached`() {
        val viewModel = createViewModel(uri = null, decoded = null)
        assertEquals(ScreenshotStatus.Loading, viewModel.getState().screenshotStatus)

        viewModel.load()

        assertEquals(ScreenshotStatus.Unavailable, viewModel.getState().screenshotStatus)

        viewModel.sendIntent(FeedbackIntent.AttachScreenshot(true))
        assertNull(viewModel.getState().attachment)
    }

    private fun createViewModel(uri: String?, decoded: Bitmap?): FeedbackViewModel {
        return FeedbackViewModel(
            arguments = FeedbackArguments.Screenshot(uri = uri),
            imageDecoder = mockk { coEvery { of(any()) } returns decoded },
            getUserUseCase = mockk { coEvery { this@mockk.invoke() } returns InternalError().left() },
            appPreferences = mockk(relaxed = true),
            sendFeedback = mockk(),
            deviceInfoProvider = mockk(),
            packageInfoProvider = mockk()
        )
    }
}
