package com.applivery.updates.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.File

object ApkInstaller {

    fun installApplication(context: Context, filePath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            AppliveryFileProvider().uriFromFile(context, File(filePath)),
            "application/vnd.android.package-archive"
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Log.e("ApkInstaller", "Error in opening the file!")
        }
    }
}