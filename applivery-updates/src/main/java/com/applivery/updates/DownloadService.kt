package com.applivery.updates

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.applivery.base.AppliveryDataManager
import com.applivery.updates.data.ApiServiceProvider
import com.applivery.updates.data.DownloadApiService
import com.applivery.updates.data.UpdatesApiService
import com.applivery.updates.domain.DownloadInfo
import com.applivery.updates.util.ApkInstaller
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException

private const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_87234"
private const val NOTIFICATION_ID = 0x21
private const val BYTE_LENGTH = 1024

class DownloadService : IntentService("DownloadInfo Service") {

    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private val fileManager = FileManager()
    private lateinit var downloadApiService: DownloadApiService
    private lateinit var updatesApiService: UpdatesApiService

    override fun onHandleIntent(intent: Intent?) {
        AppliveryDataManager.appData?.run {

            updatesApiService = ApiServiceProvider.getApiService()
            downloadApiService = ApiServiceProvider.getDownloadApiService()

            val apkFileName = name.replace(" ", "-") +
                    "-" + appConfig.lastBuildId + ".apk"

            // TODO check if file exits

            val buildToken = getBuildToken(appConfig.lastBuildId)
            if (buildToken.isNotEmpty()) {
                initDownload(apkFileName, buildToken)
            }
        } ?: also {
            // TODO update log
            Log.e("DownloadService", "null AppliveryDataManager.appData")
        }
    }

    private fun getBuildToken(buildId: String): String {
        return try {
            val tokenResponse = updatesApiService.obtainBuildToken(buildId).execute()

            if (tokenResponse.isSuccessful) {
                tokenResponse.body()?.data?.token as String
            } else {
                // TODO parse error
                ""
            }
        } catch (e: Exception) {
            // TODO update log
            Log.e("DownloadService", "getBuildToken", e)
            ""
        }
    }

    private fun initDownload(apkFileName: String, buildToken: String) {
        showNotification()
        try {
            val request = downloadApiService.downloadBuild(buildToken)
            request.execute().body()?.run { downloadFile(apkFileName, this) }
        } catch (e: IOException) {
            notificationManager?.cancel(0)
            // TODO update log
            Log.e("DownloadService", "initDownload", e)
        }
    }

    @Throws(IOException::class)
    private fun downloadFile(apkFileName: String, body: ResponseBody) {

        val outputFile = File(cacheDir, apkFileName)

        fileManager.writeResponseBodyToDisk(body = body,
            file = outputFile,
            onUpdate = { downloadInfo -> sendNotification(downloadInfo) },
            onFinish = { onDownloadComplete(outputFile.path) },
            onError = {
                Log.d("ERROR", "file download error")
            })
    }

    private fun showNotification() {
        createNotificationChannel()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle("DownloadInfo")
            .setContentText("Downloading File")
            .setAutoCancel(true)
        notificationManager?.notify(NOTIFICATION_ID, notificationBuilder?.build())
    }

    private fun sendNotification(downloadInfo: DownloadInfo) {
        notificationBuilder?.setProgress(100, downloadInfo.progress, false)
        notificationBuilder?.setContentText(
            String.format(
                "Downloaded (%d/%d) MB",
                downloadInfo.currentFileSize,
                downloadInfo.totalFileSize
            )
        )
        notificationManager?.notify(NOTIFICATION_ID, notificationBuilder!!.build())
    }

    private fun onDownloadComplete(filePath: String) {
        clearNotification()
        ApkInstaller.installApplication(this, filePath)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        clearNotification()
    }

    private fun clearNotification() {
        notificationManager?.cancel(NOTIFICATION_ID)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }
}