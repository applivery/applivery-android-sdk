package com.applivery.android.sdk.updates

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.applivery.android.sdk.R
import com.applivery.android.sdk.SdkBaseActivity
import com.applivery.android.sdk.configuration.Configuration
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.PostponedUpdateLogic
import com.applivery.android.sdk.ui.configureIf
import org.koin.core.component.inject
import kotlin.time.Duration

internal class SuggestedUpdateActivity : SdkBaseActivity() {

    private val hostAppPackageInfoProvider: HostAppPackageInfoProvider by inject()
    private val sdkConfiguration: Configuration by inject()
    private val postponedUpdateLogic: PostponedUpdateLogic by inject()

    private val postponeDurations: List<Duration>
        get() = sdkConfiguration.postponeDurations.take(MaxDurationItems)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AlertDialog.Builder(this)
            .setTitle(hostAppPackageInfoProvider.packageInfo.appName)
            .setMessage(R.string.appliveryUpdateMsg)
            .setCancelable(true)
            .setPositiveButton(R.string.appliveryUpdate, onPositiveButtonClick())
            .configureIf(postponeDurations.isNotEmpty()) {
                setNeutralButton(R.string.appliveryPostpone, onNeutralButtonClick())
            }
            .setOnCancelListener { finish() }
            .create()
            .show()
    }

    private fun onPositiveButtonClick(): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { _, _ ->
            DownloadBuildService.start(this)
            finish()
        }
    }

    private fun onNeutralButtonClick(): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { _, _ ->
            AlertDialog.Builder(this)
                .setTitle(R.string.appliveryPostponeMessage)
                .setCancelable(true)
                .setItems(
                    postponeDurations.map { it.toString() }.toTypedArray(),
                    onItemClicked(postponeDurations)
                )
                .setPositiveButton(R.string.appliveryCancel) { _, _ -> }
                .setOnDismissListener { finish() }
                .create()
                .show()
        }
    }

    private fun onItemClicked(items: List<Duration>): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { _, index ->
            val selectedOption = items.getOrNull(index) ?: return@OnClickListener
            postponedUpdateLogic.onUpdatePostponedFor(selectedOption)
        }
    }

    companion object {

        private const val MaxDurationItems = 3

        fun getIntent(context: Context): Intent {
            return Intent(context, SuggestedUpdateActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
    }
}