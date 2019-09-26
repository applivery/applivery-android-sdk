/*
 * Copyright (c) 2019 Applivery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, APP_TYPE_ID)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            AppliveryLog.error("Error opening the apk")
        }
    }
}
