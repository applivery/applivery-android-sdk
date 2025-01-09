package com.applivery.android.sdk.updates

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.applivery.android.sdk.BaseActivity
import com.applivery.android.sdk.R
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import org.koin.core.component.inject

internal class SuggestedUpdateActivity : BaseActivity(), DialogInterface.OnDismissListener {

    private val hostAppPackageInfoProvider: HostAppPackageInfoProvider by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AlertDialog.Builder(this)
            .setTitle(hostAppPackageInfoProvider.packageInfo.appName)
            .setMessage(R.string.appliveryUpdateMsg)
            .setCancelable(true)
            .setPositiveButton(R.string.appliveryUpdate, onPositiveButtonClick())
            .setNegativeButton(R.string.appliveryLater, onNegativeButtonClick())
            .setOnDismissListener(this)
            .create()
            .show()
    }

    private fun onPositiveButtonClick(): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { _, _ ->
            ContextCompat.startForegroundService(
                this,
                DownloadBuildService.getIntent(this)
            )
        }
    }

    private fun onNegativeButtonClick(): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { _, _ -> }
    }

    override fun onDismiss(dialog: DialogInterface?) = finish()

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, SuggestedUpdateActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
    }
}