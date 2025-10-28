package com.applivery.android.sdk.updates

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Parcelable
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import arrow.core.raise.either
import com.applivery.android.sdk.R
import com.applivery.android.sdk.di.AppliveryKoinComponent
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.model.AppUpdateError
import com.applivery.android.sdk.domain.model.BuildDownloadAction
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.usecases.DownloadBuildUseCase
import com.applivery.android.sdk.domain.usecases.DownloadLastBuildUseCase
import com.applivery.android.sdk.notifications.NotificationChannels
import com.applivery.android.sdk.notifications.createNotificationChannel
import com.applivery.android.sdk.ui.parcelable
import com.applivery.android.sdk.ui.serializable
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

    private val downloadLastBuild: DownloadLastBuildUseCase by inject()
    private val downloadBuild: DownloadBuildUseCase by inject()
    private val buildInstaller: BuildInstaller by inject()
    private val logger: DomainLogger by inject()
    private val progressSender: UpdateInstallProgressSender by inject()
    private val coroutineScope = MainScope()
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationChannel = NotificationChannels.Download
        createNotificationChannel(notificationChannel)
        val notification = NotificationCompat.Builder(this, notificationChannel.id)
            .setSmallIcon(R.drawable.ic_applivery_logo)
            .setContentTitle(getString(R.string.applivery_updates_app_name))
            .setContentText(getString(R.string.applivery_updates_notification_text))
            .setAutoCancel(false)
            .setOngoing(true)
            .setProgress(0, 0, true)
            .build()
        startForeground(ForegroundNotificationId, notification)
        notificationManager.cancel(DownloadCompletedNotificationId)

        val specificBuild = intent?.parcelable<BuildDownloadParams>(ArgsBuildDownloadParams)
        val downloadAction = intent?.serializable<BuildDownloadAction>(ArgsBuildDownloadAction)
            ?: BuildDownloadAction.IMMEDIATE
        coroutineScope.launch {
            downloadBuild(specificBuild, downloadAction)
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

    private suspend fun downloadBuild(
        params: BuildDownloadParams?,
        action: BuildDownloadAction
    ) {
        val serviceContext = this
        progressSender.step.update { UpdateInstallStep.Idle }
        either {
            progressSender.step.update { UpdateInstallStep.Downloading }
            val build = params
                ?.let { downloadBuild(it.buildId, it.buildVersion).bind() }
                ?: downloadLastBuild().bind()
            when (action) {
                BuildDownloadAction.IMMEDIATE -> {
                    val buildFile = File(build.filePath)
                    progressSender.step.update { UpdateInstallStep.Installing }
                    buildInstaller.install(buildFile).onLeft(::onInstallFailed)
                }

                BuildDownloadAction.DEFERRED -> {
                    /*Launch us again with IMMEDIATE: downloaded build is cached and won't download again*/
                    val intent = getIntent(serviceContext, params, BuildDownloadAction.IMMEDIATE)
                    val pendingIntent = PendingIntent.getService(
                        serviceContext,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                    val channel = NotificationChannels.Download
                    val action = NotificationCompat.Action(
                        R.drawable.ic_install,
                        getString(R.string.applivery_updates_install_action),
                        pendingIntent
                    )
                    val notification = NotificationCompat.Builder(serviceContext, channel.id)
                        .setSmallIcon(R.drawable.ic_applivery_logo)
                        .setContentTitle(getString(R.string.applivery_updates_app_name))
                        .setContentText(getString(R.string.applivery_updates_download_complete))
                        .addAction(action)
                        .build()
                    notificationManager.notify(DownloadCompletedNotificationId, notification)
                }
            }
            progressSender.step.update { UpdateInstallStep.Done }
        }
        progressSender.step.update { UpdateInstallStep.Idle }
    }

    companion object {
        private const val ForegroundNotificationId = 0x21
        private const val DownloadCompletedNotificationId = 0x22
        private const val ArgsBuildDownloadParams = "arg:build_download_params"
        private const val ArgsBuildDownloadAction = "arg:build_download_action"

        fun start(
            context: Context,
            params: BuildDownloadParams? = null,
            action: BuildDownloadAction = BuildDownloadAction.IMMEDIATE
        ) {
            val intent = getIntent(context, params, action)
            ContextCompat.startForegroundService(context, intent)
        }

        private fun getIntent(
            context: Context,
            params: BuildDownloadParams? = null,
            action: BuildDownloadAction = BuildDownloadAction.IMMEDIATE
        ): Intent {
            return Intent(context, DownloadBuildService::class.java)
                .putExtra(ArgsBuildDownloadParams, params)
                .putExtra(ArgsBuildDownloadAction, action)
        }
    }
}

@Parcelize
internal data class BuildDownloadParams(
    val buildId: String,
    val buildVersion: Int
) : Parcelable