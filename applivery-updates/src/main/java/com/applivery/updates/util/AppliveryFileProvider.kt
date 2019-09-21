package com.applivery.updates.util

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

class AppliveryFileProvider : FileProvider() {

    fun uriFromFile(context: Context, file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getUriForFile(
                context,
                context.packageName + ".fileprovider",
                file
            )
        } else {
            Uri.fromFile(file)
        }
    }
}