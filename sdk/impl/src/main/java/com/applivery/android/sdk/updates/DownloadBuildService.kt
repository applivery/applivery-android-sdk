package com.applivery.android.sdk.updates

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import arrow.core.raise.either
import com.applivery.android.sdk.R
import com.applivery.android.sdk.di.AppliveryKoinComponent
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.usecases.DownloadLastBuildUseCase
import com.applivery.android.sdk.notifications.NotificationChannels
import com.applivery.android.sdk.notifications.createNotificationChannel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

internal class DownloadBuildService : Service(), AppliveryKoinComponent {

    private val hostAppPackageInfoProvider: HostAppPackageInfoProvider by inject()

    private val downloadLastBuild: DownloadLastBuildUseCase by inject()

    private val buildInstaller: BuildInstaller by inject()

    private val logger: DomainLogger by inject()

    private val progressSender: UpdateInstallProgressSender by inject()

    private val coroutineScope = MainScope()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        val notificationChannel = NotificationChannels.Download
        createNotificationChannel(notificationChannel)
        val appName = hostAppPackageInfoProvider.packageInfo.appName
        val notification = NotificationCompat.Builder(this, notificationChannel.id)
            .setSmallIcon(R.drawable.ic_applivery_blue)
            .setContentTitle(getString(R.string.applivery_updates_app_name))
            .setContentText(getString(R.string.applivery_updates_notification_text, appName))
            .setAutoCancel(false)
            .setOngoing(true)
            .setSound(null)
            .setVibrate(null)
            .setProgress(0, 0, true)
            .build()
        startForeground(NotificationId, notification)

        coroutineScope.launch {
            progressSender.step.update { UpdateInstallStep.Idle }
            either {
                progressSender.step.update { UpdateInstallStep.Downloading }
                val lastBuildFile = downloadLastBuild().bind()
                progressSender.step.update { UpdateInstallStep.Installing }
                buildInstaller.install(lastBuildFile).onLeft(::onInstallFailed)
                lastBuildFile.delete()
                progressSender.step.update { UpdateInstallStep.Done }
            }
            progressSender.step.update { UpdateInstallStep.Idle }
            stopSelf()
        }

        progressSender.step
            .onEach { logger.installBuildProgress(it) }
            .launchIn(coroutineScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun onInstallFailed(error: DomainError) {
        logger.errorInstallingBuild(error)
    }

    companion object {
        private const val NotificationId = 0x21

        fun start(context: Context) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, DownloadBuildService::class.java)
            )
        }
    }
}