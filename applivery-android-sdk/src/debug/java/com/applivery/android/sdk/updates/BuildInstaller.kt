package com.applivery.android.sdk.updates

import android.content.Context
import android.content.Intent
import java.io.File

internal interface BuildInstaller {

    fun install(file: File)
}

internal class IntentBuildInstaller(private val context: Context) : BuildInstaller {

    override fun install(file: File) {

        val contentUri = context.getContentUriForFile(file) ?: return
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(contentUri, "application/vnd.android.package-archive")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        runCatching { context.startActivity(intent) }
    }
}