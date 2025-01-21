package com.applivery.android.sdk.feedback

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import arrow.core.flatMap
import arrow.core.raise.either
import com.applivery.android.sdk.domain.ensureNotNull
import com.applivery.android.sdk.domain.usecases.GetUserUseCase
import com.applivery.android.sdk.presentation.BaseViewModel
import com.applivery.android.sdk.presentation.ViewAction
import com.applivery.android.sdk.presentation.ViewIntent
import com.applivery.android.sdk.presentation.ViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
class FeedbackArguments(val screenshotUri: Uri? = null) : Parcelable

internal sealed interface FeedbackAction : ViewAction {
    data object Exit : FeedbackAction
}

internal sealed interface FeedbackIntent : ViewIntent {
    class FeedbackTypeChanged(val type: FeedbackType) : FeedbackIntent
    class FeedbackChanged(val feedback: String) : FeedbackIntent
    class EmailChanged(val email: String) : FeedbackIntent
    data object ClickScreenshot : FeedbackIntent
    class AttachScreenshot(val attach: Boolean) : FeedbackIntent
    data object SendFeedback : FeedbackIntent
    data object CancelFeedback : FeedbackIntent
}

internal data class FeedbackState(
    val isLoading: Boolean = false,
    val feedbackType: FeedbackType = FeedbackType.Feedback,
    val feedback: String? = null,
    val email: String? = null,
    val isEmailReadOnly: Boolean = false,
    val screenshot: Bitmap? = null
) : ViewState

internal class FeedbackViewModel(
    private val arguments: FeedbackArguments,
    private val imageDecoder: ContentUriImageDecoder,
    private val getUserUseCase: GetUserUseCase
) : BaseViewModel<FeedbackState, FeedbackIntent, FeedbackAction>() {

    override val initialViewState: FeedbackState = FeedbackState()

    override fun load() {
        viewModelScope.launch {
            getUserUseCase()
                .flatMap { either { ensureNotNull(it.email.takeUnless { it.isNullOrBlank() }) } }
                .onRight { setState { copy(isEmailReadOnly = true, email = it) } }
        }

        viewModelScope.launch {
            val uri = arguments.screenshotUri ?: return@launch
            imageDecoder.of(uri)?.let { setState { copy(screenshot = it) } }
        }
    }

    override fun sendIntent(intent: FeedbackIntent) {
        when (intent) {
            is FeedbackIntent.FeedbackChanged -> onFeedbackChanged(intent.feedback)
            is FeedbackIntent.FeedbackTypeChanged -> onFeedbackTypeChanged(intent.type)
            is FeedbackIntent.EmailChanged -> onEmailChanged(intent.email)
            is FeedbackIntent.ClickScreenshot -> onClickScreenshot()
            is FeedbackIntent.AttachScreenshot -> onAttachScreenshot(intent.attach)
            is FeedbackIntent.SendFeedback -> onSendFeedback()
            is FeedbackIntent.CancelFeedback -> onCancelFeedback()
        }
    }

    private fun onFeedbackChanged(feedback: String) {
        setState { copy(feedback = feedback) }
    }

    private fun onFeedbackTypeChanged(type: FeedbackType) {
        setState { copy(feedbackType = type) }
    }

    private fun onEmailChanged(email: String) {
        setState { copy(email = email) }
    }

    private fun onClickScreenshot() {
        // TODO:  
    }

    private fun onAttachScreenshot(attach: Boolean) {
        if (attach) {

        } else {
            setState { copy(screenshot = null) }
        }
    }

    private fun onSendFeedback() {
        // TODO:
    }

    private fun onCancelFeedback() {
        dispatchAction(FeedbackAction.Exit)
    }
}