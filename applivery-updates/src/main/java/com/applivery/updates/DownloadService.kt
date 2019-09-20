package com.applivery.updates

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
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

            val buildToken = getBuildToken(id)
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
            tokenResponse.body()?.toBuildToken() as String
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun initDownload(buildToken: String) {
        showNotification()
        val request = downloadApiService.downloadBuild(buildToken)
        try {
            downloadFile(request.execute().body()!!)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNotification() {
        createNotificationChannel()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle("Download")
            .setContentText("Downloading File")
            .setAutoCancel(true)
        notificationManager!!.notify(0, notificationBuilder!!.build())
    }

    @Throws(IOException::class)
    private fun downloadFile(body: ResponseBody) {
        var count: Int
        val data = ByteArray(1024 * 4)
        val fileSize = body.contentLength()
        val bis = BufferedInputStream(body.byteStream(), 1024 * 8)
        val outputFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "file.zip"
        )
        val output = FileOutputStream(outputFile)
        var total: Long = 0
        val startTime = System.currentTimeMillis()
        var timeCount = 1
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

    private fun sendNotification(download: Download) {
        sendIntent(download)
        notificationBuilder!!.setProgress(100, download.progress, false)
        notificationBuilder!!.setContentText(
            String.format(
                "Downloaded (%d/%d) MB",
                download.currentFileSize,
                download.totalFileSize
            )
        )
        notificationManager!!.notify(0, notificationBuilder!!.build())
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

        notificationManager!!.cancel(0)
        notificationBuilder!!.setProgress(0, 0, false)
        notificationBuilder!!.setContentText("File Downloaded")
        notificationManager!!.notify(0, notificationBuilder!!.build())

    }

    override fun onTaskRemoved(rootIntent: Intent) {
        notificationManager!!.cancel(0)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    companion object {

        private val CHANNEL_ID = "CHANNEL_ID"
    }
}