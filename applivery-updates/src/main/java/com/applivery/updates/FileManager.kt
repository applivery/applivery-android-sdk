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
package com.applivery.updates

import android.content.Context
import android.content.Context.MODE_WORLD_READABLE
import android.os.Build
import com.applivery.base.util.AppliveryLog
import com.applivery.updates.domain.DownloadInfo
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.pow
import kotlin.math.roundToInt

private const val ONE_SECOND_IN_MILLIS = 1000
private const val MAX_PROGRESS = 100
private const val ONE_MB_IN_BYTES = 1024.0
private const val BYTE_ARRAY_SIZE = 4096

class FileManager(private val context: Context) {

    internal fun writeResponseBodyToDisk(
        body: ResponseBody,
        file: File,
        onUpdate: (DownloadInfo) -> Unit,
        onFinish: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val fileReader = ByteArray(BYTE_ARRAY_SIZE)

            val fileSize = body.contentLength()
            var fileSizeDownloaded: Long = 0
            val totalFileSize = (fileSize / ONE_MB_IN_BYTES.pow(2.0)).toInt()

            inputStream = body.byteStream()
            outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileOutputStream(file)
            } else {
                context.openFileOutput(file.name, MODE_WORLD_READABLE)
            }

            val startTime = System.currentTimeMillis()
            var timeCount = 1
            onUpdate(DownloadInfo(totalFileSize = totalFileSize))

            while (true) {
                val read = inputStream!!.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream?.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()

                val current =
                    (fileSizeDownloaded / ONE_MB_IN_BYTES.pow(2.0)).roundToInt().toDouble()
                val progress = (fileSizeDownloaded * MAX_PROGRESS / fileSize).toInt()
                val currentTime = System.currentTimeMillis() - startTime

                if (currentTime > ONE_SECOND_IN_MILLIS * timeCount) {
                    onUpdate(
                        DownloadInfo(
                            progress = progress,
                            currentFileSize = current.toInt(),
                            totalFileSize = totalFileSize
                        )
                    )
                    timeCount++
                }
                AppliveryLog.debug("File download: $fileSizeDownloaded of $fileSize")
            }

            outputStream?.flush()
            onFinish()
        } catch (e: IOException) {
            onError(e)
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }
}
