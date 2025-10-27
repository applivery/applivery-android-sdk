package com.applivery.android.sdk.updates

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Parcelable
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import arrow.core.raise.either
import com.applivery.android.sdk.R
import com.applivery.android.sdk.di.AppliveryKoinComponent
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.model.AppUpdateError
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.usecases.DownloadBuildUseCase
import com.applivery.android.sdk.domain.usecases.DownloadLastBuildUseCase
import com.applivery.android.sdk.notifications.NotificationChannels
import com.applivery.android.sdk.notifications.createNotificationChannel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.android.ext.android.inject
import java.io.File

internal class DownloadBuildService : Service(), AppliveryKoinComponent {

    private val hostAppPackageInfoProvider: HostAppPackageInfoProvider by inject()
    private val downloadLastBuild: DownloadLastBuildUseCase by inject()
    private val downloadBuild: DownloadBuildUseCase by inject()
    private val buildInstaller: BuildInstaller by inject()
    private val logger: DomainLogger by inject()
    private val progressSender: UpdateInstallProgressSender by inject()
    private val coroutineScope = MainScope()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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

        val specificBuild = intent?.getParcelableExtra<BuildDownloadParams>(ArgsBuildDownloadParams)
        coroutineScope.launch {
            progressSender.step.update { UpdateInstallStep.Idle }
            either {
                progressSender.step.update { UpdateInstallStep.Downloading }
                val build = specificBuild
                    ?.let { downloadBuild(it.buildId, it.buildVersion) }
                    ?: downloadLastBuild()
                val buildFile = File(build.bind().filePath)
                progressSender.step.update { UpdateInstallStep.Installing }
                buildInstaller.install(buildFile).onLeft(::onInstallFailed)
                progressSender.step.update { UpdateInstallStep.Done }
            }
            progressSender.step.update { UpdateInstallStep.Idle }
            stopSelf()
        }

        progressSender.step
            .onEach { logger.installBuildProgress(it) }
            .launchIn(coroutineScope)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun onInstallFailed(error: DomainError) {
        logger.errorInstallingBuild(error)
        when (error) {
            is AppUpdateError.Installation -> when (error.cause) {
                AppUpdateError.Installation.Cause.Unknown -> Unit
                AppUpdateError.Installation.Cause.InsufficientStorage -> Toast.makeText(
                    this,
                    getString(R.string.appliveryInsufficientStorage),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private const val NotificationId = 0x21
        private const val ArgsBuildDownloadParams = "arg:build_download_params"

        fun start(context: Context, params: BuildDownloadParams? = null) {
            val intent = Intent(context, DownloadBuildService::class.java)
                .putExtra(ArgsBuildDownloadParams, params)
            ContextCompat.startForegroundService(context, intent)
        }
    }
}

@Parcelize
internal data class BuildDownloadParams(
    val buildId: String,
    val buildVersion: Int
) : Parcelable