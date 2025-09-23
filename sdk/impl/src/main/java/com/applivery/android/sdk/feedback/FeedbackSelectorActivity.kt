package com.applivery.android.sdk.feedback

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.applivery.android.sdk.R
import com.applivery.android.sdk.SdkBaseActivity
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.feedback.screenshot.HostAppScreenshotFormat
import com.applivery.android.sdk.feedback.screenshot.HostAppScreenshotProvider
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.inject

internal class FeedbackSelectorActivity : SdkBaseActivity() {

    private val hostAppPackageInfoProvider: HostAppPackageInfoProvider by inject()
    private val hostAppScreenshotProvider: HostAppScreenshotProvider by inject()
    private val feedbackLauncher: FeedbackLauncher by inject()
    private val coroutineScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AlertDialog.Builder(this)
            .setTitle(hostAppPackageInfoProvider.packageInfo.appName)
            .setCancelable(true)
            .setMessage(R.string.appliveryFeedbackEventDialogBody)
            .setPositiveButton(R.string.appliveryFeedbackScreenshotEvent, onNormalFeedbackClick())
            .setNeutralButton(R.string.appliveryFeedbackVideoEvent, onRecordingClick())
            .setOnCancelListener { finish() }
            .create()
            .show()
    }

    private fun onNormalFeedbackClick(): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { _, _ ->
            coroutineScope.launch {
                val uri = hostAppScreenshotProvider
                    .get(format = HostAppScreenshotFormat.AsUri)
                    .getOrNull()
                feedbackLauncher.launchWith(behavior = FeedbackBehavior.Screenshot(uri))
            }
            finish()
        }
    }

    private fun onRecordingClick(): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { _, _ ->
            feedbackLauncher.launchWith(behavior = FeedbackBehavior.Video)
            finish()
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, FeedbackSelectorActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
    }
}
