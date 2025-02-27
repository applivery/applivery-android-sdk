package com.applivery.android.sdk.feedback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.applivery.android.sdk.R
import com.applivery.android.sdk.SdkBaseActivity
import com.applivery.android.sdk.presentation.launchAndCollectIn
import com.applivery.android.sdk.ui.parcelable
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

internal class FeedbackActivity : SdkBaseActivity() {

    private val arguments get() = intent.parcelable<FeedbackArguments>(ExtraArguments)
    private val viewModel: FeedbackViewModel by viewModel { parametersOf(arguments) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.viewState.collectAsState(FeedbackState())
            FeedbackScreen(
                state = state,
                intentSender = viewModel
            )
        }

        viewModel.viewActions.launchAndCollectIn(this) { action ->
            when (action) {
                is FeedbackAction.Exit -> finish()
                is FeedbackAction.ShowFeedbackSent -> showFeedbackSentToast(action.success)
            }
        }

        if (savedInstanceState == null) {
            viewModel.load()
        }
    }

    private fun showFeedbackSentToast(success: Boolean) {
        val messageResId = if (success) {
            R.string.appliveryFeedbackSentSuccess
        } else {
            R.string.appliveryFeedbackSentError
        }
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
    }

    companion object {

        private const val ExtraArguments = "extra:arguments"

        fun getIntent(context: Context, arguments: FeedbackArguments): Intent {
            return Intent(context, FeedbackActivity::class.java)
                .putExtra(ExtraArguments, arguments)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
    }
}
