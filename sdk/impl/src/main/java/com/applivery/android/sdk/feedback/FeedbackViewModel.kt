package com.applivery.android.sdk.feedback

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import arrow.core.flatMap
import arrow.core.raise.either
import com.applivery.android.sdk.domain.AppPreferences
import com.applivery.android.sdk.domain.DeviceInfoProvider
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.ensureNotNull
import com.applivery.android.sdk.domain.model.Feedback
import com.applivery.android.sdk.domain.usecases.GetUserUseCase
import com.applivery.android.sdk.domain.usecases.SendFeedbackUseCase
import com.applivery.android.sdk.presentation.BaseViewModel
import com.applivery.android.sdk.presentation.ViewAction
import com.applivery.android.sdk.presentation.ViewIntent
import com.applivery.android.sdk.presentation.ViewState
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

private val EmailRegex =
    """[a-zA-Z0-9+._%\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\-]{0,64}(\.[a-zA-Z0-9][a-zA-Z0-9\-]{0,25})+""".toRegex()

internal sealed interface FeedbackArguments : Parcelable {

    val uri: Uri?

    @Parcelize
    class Screenshot(override val uri: Uri? = null) : FeedbackArguments

    @Parcelize
    class Video(override val uri: Uri) : FeedbackArguments
}

internal sealed interface FeedbackAction : ViewAction {
    data object Exit : FeedbackAction
    class ShowFeedbackSent(val success: Boolean) : FeedbackAction
}

internal sealed interface FeedbackIntent : ViewIntent {
    class FeedbackTypeChanged(val type: FeedbackType) : FeedbackIntent
    class FeedbackChanged(val feedback: String) : FeedbackIntent
    class EmailChanged(val email: String) : FeedbackIntent
    class AttachScreenshot(val attach: Boolean) : FeedbackIntent
    data object SendFeedback : FeedbackIntent
    data object CancelFeedback : FeedbackIntent
    class ScreenshotModified(val newScreenshot: Bitmap) : FeedbackIntent
}

internal data class FeedbackState(
    val isLoading: Boolean = false,
    val feedbackType: FeedbackType = FeedbackType.Feedback,
    val feedback: String? = null,
    val email: String? = null,
    val isEmailInvalid: Boolean = false,
    val isEmailReadOnly: Boolean = false,
    val attachment: FeedbackAttachment? = null,
    val isSendEnabled: Boolean = false,
) : ViewState

internal class FeedbackViewModel(
    private val arguments: FeedbackArguments,
    private val imageDecoder: ContentUriImageDecoder,
    private val getUserUseCase: GetUserUseCase,
    private val appPreferences: AppPreferences,
    private val sendFeedback: SendFeedbackUseCase,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val packageInfoProvider: HostAppPackageInfoProvider,
    private val hostAppScreenshotProvider: HostAppScreenshotProvider
) : BaseViewModel<FeedbackState, FeedbackIntent, FeedbackAction>() {

    override val initialViewState: FeedbackState = FeedbackState()

    override fun load() {
        viewModelScope.launch {
            getUserUseCase()
                .flatMap { either { ensureNotNull(it.email.takeUnless { it.isNullOrBlank() }) } }
                .onRight { setState { copy(isEmailReadOnly = true) } }
                .onRight { onEmailChanged(it) }
                .onLeft { onEmailChanged(appPreferences.anonymousEmail.orEmpty()) }
        }

        viewModelScope.launch {
            val attachment = when (arguments) {
                is FeedbackArguments.Screenshot -> arguments.uri
                    ?.let { imageDecoder.of(it) }
                    ?.let(FeedbackAttachment::Screenshot)

                is FeedbackArguments.Video -> FeedbackAttachment.Video(arguments.uri)
            }
            setState { copy(attachment = attachment) }
        }
    }

    override fun sendIntent(intent: FeedbackIntent) {
        when (intent) {
            is FeedbackIntent.FeedbackChanged -> onFeedbackChanged(intent.feedback)
            is FeedbackIntent.FeedbackTypeChanged -> onFeedbackTypeChanged(intent.type)
            is FeedbackIntent.EmailChanged -> onEmailChanged(intent.email)
            is FeedbackIntent.AttachScreenshot -> onAttachScreenshot(intent.attach)
            is FeedbackIntent.SendFeedback -> onSendFeedback()
            is FeedbackIntent.CancelFeedback -> onCancelFeedback()
            is FeedbackIntent.ScreenshotModified -> onScreenshotModified(intent.newScreenshot)
        }
    }

    private fun onFeedbackChanged(feedback: String) {
        setState { copy(feedback = feedback) }
        checkInputs()
    }

    private fun onFeedbackTypeChanged(type: FeedbackType) {
        setState { copy(feedbackType = type) }
    }

    private fun onEmailChanged(email: String) {
        setState { copy(email = email) }
        checkInputs()
    }

    private fun onAttachScreenshot(attach: Boolean) {
        if (attach) {
            viewModelScope.launch {
                setState { copy(isLoading = false) }
                val screenshot = hostAppScreenshotProvider.get().getOrNull()
                val attachment = screenshot?.let(FeedbackAttachment::Screenshot)
                setState { copy(attachment = attachment, isLoading = false) }
            }
        } else {
            setState { copy(attachment = null) }
        }
    }

    private fun checkInputs() {
        val feedback = getState().feedback.orEmpty()
        val email = getState().email.orEmpty()
        val isEmailValid = email.isValidEmail()
        val isSendEnabled = feedback.isNotBlank() && isEmailValid
        setState { copy(isEmailInvalid = !isEmailValid, isSendEnabled = isSendEnabled) }
    }

    private fun onSendFeedback() {

        val state = getState()
        val email = state.email.orEmpty()
        if (email.isNotBlank() && !state.isEmailReadOnly) {
            appPreferences.anonymousEmail = email
        }
        setState { copy(isLoading = true, isSendEnabled = false) }

        viewModelScope.launch {
            val baseFeedback = Feedback.Simple(
                deviceInfo = deviceInfoProvider.deviceInfo,
                packageInfo = packageInfoProvider.packageInfo,
                message = getState().feedback,
                type = getState().feedbackType,
                email = getState().email
            )
            val feedback = when (val attachment = getState().attachment) {
                is FeedbackAttachment.Video -> baseFeedback.withVideo(attachment.uri)
                is FeedbackAttachment.Screenshot -> baseFeedback.withScreenshot(attachment.screenshot)
                null -> baseFeedback
            }
            val result = sendFeedback(feedback)
            dispatchAction(FeedbackAction.ShowFeedbackSent(result.isRight()))
            dispatchAction(FeedbackAction.Exit)
        }
    }

    private fun onCancelFeedback() {
        dispatchAction(FeedbackAction.Exit)
    }

    private fun onScreenshotModified(newScreenshot: Bitmap) {
        setState { copy(attachment = FeedbackAttachment.Screenshot(newScreenshot)) }
    }

    private fun String.isValidEmail(): Boolean {
        return EmailRegex.matches(this)
    }
}