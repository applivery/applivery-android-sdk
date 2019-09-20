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
import com.applivery.updates.domain.Download
import okhttp3.ResponseBody
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.pow
import kotlin.math.roundToInt

private const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_87234"
private const val NOTIFICATION_ID = 0x21

class DownloadService : IntentService("Download Service") {

    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private var totalFileSize: Int = 0
    private lateinit var downloadApiService: DownloadApiService
    private lateinit var updatesApiService: UpdatesApiService

    override fun onHandleIntent(intent: Intent?) {
        AppliveryDataManager.appData?.run {

            updatesApiService = ApiServiceProvider.getApiService()
            downloadApiService = ApiServiceProvider.getDownloadApiService()

            val buildToken = getBuildToken(appConfig.lastBuildId)
            if (buildToken.isNotEmpty()) {
                initDownload(buildToken)
            }
        } ?: also {
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
            Log.e("DownloadService", "getBuildToken", e)
            ""
        }
    }

    private fun initDownload(buildToken: String) {
        showNotification()
        try {
            val request = downloadApiService.downloadBuild(buildToken)
            request.execute().body()?.run { downloadFile(this) }
        } catch (e: IOException) {
            notificationManager?.cancel(0)
            Log.e("DownloadService", "initDownload", e)
        }
    }

    @Throws(IOException::class)
    private fun downloadFile(body: ResponseBody) {

        val apkFileName = "TODO_874ywfuhsd"

        val fileSize = body.contentLength()
        val bis = BufferedInputStream(body.byteStream())
        val outputFile = File(cacheDir, "$apkFileName.apk")
        val output = FileOutputStream(outputFile)

        val startTime = System.currentTimeMillis()
        var total: Long = 0
        var timeCount = 1

        var count: Int
        val data = ByteArray(fileSize.toInt())

        while (bis.read(data) != -1) {
            count = bis.read(data)
            total += count.toLong()
            totalFileSize = (fileSize / 1024.0.pow(2.0)).toInt()
            val current = (total / 1024.0.pow(2.0)).roundToInt().toDouble()

            val progress = (total * 100 / fileSize).toInt()

            val currentTime = System.currentTimeMillis() - startTime

            val download = Download()
            download.totalFileSize = totalFileSize

            if (currentTime > 1000 * timeCount) {
                download.currentFileSize = current.toInt()
                download.progress = progress
                sendNotification(download)
                timeCount++
            }

            output.write(data, 0, count)
        }
        onDownloadComplete()
        output.flush()
        output.close()
        bis.close()
    }

    private fun showNotification() {
        createNotificationChannel()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle("Download")
            .setContentText("Downloading File")
            .setAutoCancel(true)
        notificationManager?.notify(NOTIFICATION_ID, notificationBuilder?.build())
    }

    private fun sendNotification(download: Download) {
        sendIntent(download)
        notificationBuilder?.setProgress(100, download.progress, false)
        notificationBuilder?.setContentText(
            String.format(
                "Downloaded (%d/%d) MB",
                download.currentFileSize,
                download.totalFileSize
            )
        )
        notificationManager?.notify(NOTIFICATION_ID, notificationBuilder!!.build())
    }

    private fun sendIntent(download: Download) {

        //        Intent intent = new Intent(MainActivity.MESSAGE_PROGRESS);
        //        intent.putExtra("download",download);
        //        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private fun onDownloadComplete() {
        val download = Download()
        download.progress = 100
        sendIntent(download)

        notificationManager?.cancel(NOTIFICATION_ID)
        notificationBuilder?.setProgress(0, 0, false)
        notificationBuilder?.setContentText("File Downloaded")
        notificationManager?.notify(NOTIFICATION_ID, notificationBuilder!!.build())
    }

    override fun onTaskRemoved(rootIntent: Intent) {
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