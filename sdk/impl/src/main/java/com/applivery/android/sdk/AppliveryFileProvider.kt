package com.applivery.android.sdk

import android.content.Context
import android.content.pm.ProviderInfo
import androidx.core.content.FileProvider

internal class AppliveryFileProvider : FileProvider(R.xml.applivery_file_paths) {

    override fun attachInfo(context: Context, info: ProviderInfo) {
        super.attachInfo(context, info)
        authority = info.authority
    }

    companion object {
        lateinit var authority: String private set
    }
}
