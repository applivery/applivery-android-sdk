package com.applivery.updates.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.applivery.base.util.AppliveryLog
import java.io.File

private const val APP_TYPE_ID = "application/vnd.android.package-archive"

object ApkInstaller {

    fun installApplication(context: Context, filePath: String) {

        val uri = AppliveryFileProvider().uriFromFile(context, File(filePath))

        val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
        intent.setDataAndType(uri, APP_TYPE_ID)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.grantUriPermission(
                context.packageName, uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            AppliveryLog.error("Error opening the apk")
        }
    }
}