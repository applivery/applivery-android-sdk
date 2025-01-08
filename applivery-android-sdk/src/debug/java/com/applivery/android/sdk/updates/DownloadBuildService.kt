package com.applivery.android.sdk.updates

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import arrow.core.raise.either
import com.applivery.android.sdk.R
import com.applivery.android.sdk.di.AppliveryKoinComponent
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.usecases.DownloadLastBuildUseCase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

internal class DownloadBuildService : Service(), AppliveryKoinComponent {

    private val hostAppPackageInfoProvider: HostAppPackageInfoProvider by inject()

    private val downloadLastBuild: DownloadLastBuildUseCase by inject()

    private val buildInstaller: BuildInstaller by inject()

    private val coroutineScope = MainScope()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val appName = hostAppPackageInfoProvider.packageInfo.appName
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_applivery_blue)
            .setContentTitle(getString(R.string.applivery_updates_app_name))
            .setContentText(getString(R.string.applivery_updates_notification_text, appName))
            .setAutoCancel(false)
            .setOngoing(true)
            .setSound(null)
            .setVibrate(null)
            .setProgress(0, 0, true)
            .build()
        startForeground(NOTIFICATION_ID, notification)

        coroutineScope.launch {
            either {
                buildInstaller.install(downloadLastBuild().bind())
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.applivery_updates_channel_name)
            val description = getString(R.string.applivery_updates_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                setSound(null, null)
                enableVibration(false)
                setDescription(description)
            }
            NotificationManagerCompat.from(this).createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_87234"
        private const val NOTIFICATION_ID = 0x21

        fun getIntent(context: Context): Intent {
            return Intent(context, DownloadBuildService::class.java)
        }
    }
}