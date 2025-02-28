package com.applivery.android.sdk.feedback.video.recorder

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Parcelable
import android.os.ResultReceiver
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import com.applivery.android.sdk.R
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderErrorCodes.MAX_FILE_SIZE_REACHED_ERROR
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderErrorCodes.SETTINGS_ERROR
import com.applivery.android.sdk.notifications.NotificationChannels
import com.applivery.android.sdk.notifications.createNotificationChannel
import com.applivery.android.sdk.ui.parcelable
import kotlinx.parcelize.Parcelize
import java.util.Locale

internal class ScreenRecorderService : Service() {
    private lateinit var config: ScreenRecorderConfig
    private lateinit var activityResult: ActivityResult
    private lateinit var listener: ResultReceiver

    private var hasMaxFileBeenReached = false

    private val mediaProjectionManager by lazy { getSystemService<MediaProjectionManager>() }
    private var mediaProjection: MediaProjection? = null
    private var mediaRecorder: MediaRecorder? = null
    private var virtualDisplay: VirtualDisplay? = null

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf(startId)
            return START_STICKY
        }

        if (intent.action != null) {
            when (intent.action) {
                ScreenRecorderServiceActions.Pause -> pauseRecording()
                ScreenRecorderServiceActions.Resume -> resumeRecording()
            }
            return START_STICKY
        }

        hasMaxFileBeenReached = false
        config = intent.parcelable(ExtraConfig) ?: error("Config is null")
        activityResult = intent.parcelable(ExtraActivityResult) ?: error("ActivityResult is null")
        listener = intent.parcelable(ExtraListener) ?: error("Listener is null")

        val notificationChannel = NotificationChannels.ScreenRecording
        createNotificationChannel(notificationChannel)

        val stopIntent = Intent(
            this,
            StopScreenRecordingNotificationReceiver::class.java
        )
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopAction = NotificationCompat.Action.Builder(
            R.drawable.ic_stop,
            getString(R.string.appliveryScreenRecordingNotificationStopAction),
            stopPendingIntent
        ).build()

        val notification = NotificationCompat.Builder(this, notificationChannel.id)
            .setSmallIcon(R.drawable.ic_applivery_blue)
            .setContentTitle(getString(R.string.appliveryScreenRecordingNotificationTitle))
            .setContentText(getString(R.string.appliveryScreenRecordingNotificationDescription))
            .addAction(stopAction)
            .build()
        startForegroundCompat(notification)

        try {
            initRecorder()
        } catch (e: Exception) {
            listener.sendEvent(Event.OnError(reason = Log.getStackTraceString(e)))
        }

        try {
            initMediaProjection(activityResult)
        } catch (e: Exception) {
            listener.sendEvent(Event.OnError(reason = Log.getStackTraceString(e)))
        }

        try {
            initVirtualDisplay()
        } catch (e: Exception) {
            listener.sendEvent(Event.OnError(reason = Log.getStackTraceString(e)))
        }

        mediaRecorder?.setOnErrorListener { _: MediaRecorder?, what: Int, _: Int ->
            if (what == 268435556 && hasMaxFileBeenReached) {
                // Benign error b/c recording is too short and has no frames. See SO: https://stackoverflow.com/questions/40616466/mediarecorder-stop-failed-1007
                return@setOnErrorListener
            }
            listener.sendEvent(Event.OnError(code = SETTINGS_ERROR, reason = what.toString()))
        }

        mediaRecorder?.setOnInfoListener { _: MediaRecorder?, what: Int, extra: Int ->
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
                hasMaxFileBeenReached = true
                Log.i(
                    TAG,
                    String.format(
                        Locale.US,
                        "onInfoListen what : %d | extra %d",
                        what,
                        extra
                    )
                )
                listener.sendEvent(Event.OnError(code = MAX_FILE_SIZE_REACHED_ERROR))
            }
        }

        try {
            mediaRecorder?.start()
            listener.sendEvent(Event.OnStart)
        } catch (e: Exception) {
            val event = Event.OnError(code = SETTINGS_ERROR, reason = Log.getStackTraceString(e))
            listener.sendEvent(event)
        }

        return START_STICKY
    }

    private fun pauseRecording() {
        mediaRecorder?.pause()
        listener.sendEvent(Event.OnPause)
    }

    private fun resumeRecording() {
        mediaRecorder?.resume()
        listener.sendEvent(Event.OnResume)
    }

    private fun initMediaProjection(activityResult: ActivityResult) {
        mediaProjection = mediaProjectionManager?.getMediaProjection(
            activityResult.resultCode,
            activityResult.data ?: Intent()
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            mediaProjection?.registerCallback(
                mediaProjectionCallback(::stopSelf),
                Handler(Looper.getMainLooper())
            )
        } else {
            mediaProjection?.registerCallback(
                mediaProjectionCallback(),
                Handler(Looper.getMainLooper())
            )
        }
    }

    private fun initRecorder() {
        val screenSpecs = ScreenSpecsCalculator(this).screenSpecs
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
        mediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT)
        mediaRecorder?.setOutputFile(config.outputFile.path)
        mediaRecorder?.setVideoSize(screenSpecs.width, screenSpecs.height)

        // TODO: use some kind of metric to record on SD on low end devices
        val isHdVideo = true
        if (!isHdVideo) {
            mediaRecorder?.setVideoEncodingBitRate(12000000)
            mediaRecorder?.setVideoFrameRate(30)
        } else {
            mediaRecorder?.setVideoEncodingBitRate(5 * screenSpecs.width * screenSpecs.height)
            mediaRecorder?.setVideoFrameRate(60)
        }

        // Catch approaching file limit
        if (config.maxFileSize > ScreenRecorderDefaults.NoLimitMaxFileSize) {
            mediaRecorder?.setMaxFileSize(config.maxFileSize) // in bytes
        }

        mediaRecorder?.prepare()
    }

    private fun initVirtualDisplay() {
        val mediaProjection = mediaProjection ?: return
        val screenSpecs = ScreenSpecsCalculator(this).screenSpecs
        virtualDisplay = mediaProjection.createVirtualDisplay(
            TAG,
            screenSpecs.width,
            screenSpecs.height,
            screenSpecs.density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mediaRecorder?.surface,
            null,
            null
        )
    }

    private fun startForegroundCompat(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NotificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            )
        } else {
            startForeground(NotificationId, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        resetAll()
        listener.sendEvent(Event.OnComplete)
    }

    private fun resetAll() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        virtualDisplay?.release()
        virtualDisplay = null
        mediaRecorder?.setOnErrorListener(null)
        mediaRecorder?.reset()
        mediaProjection?.stop()
    }

    private fun ResultReceiver.sendEvent(event: Event) {
        send(Activity.RESULT_OK, bundleOf(EventKey to event))
    }

    companion object {
        private const val TAG = "ScreenRecorderService"
        private const val NotificationId = 43566
        private const val ExtraConfig = "extra:config"
        private const val ExtraActivityResult = "extra:activityResult"
        private const val ExtraListener = "extra:listener"

        const val EventKey = "ScreenRecorderService:event"

        fun getIntent(context: Context): Intent {
            return Intent(context, ScreenRecorderService::class.java)
        }

        fun getIntent(
            context: Context,
            config: ScreenRecorderConfig,
            mediaProjectionPermissionResult: ActivityResult,
            listener: ResultReceiver
        ): Intent {
            return Intent(context, ScreenRecorderService::class.java).apply {
                putExtra(ExtraConfig, config)
                putExtra(ExtraActivityResult, mediaProjectionPermissionResult)
                putExtra(ExtraListener, listener)
            }
        }
    }

    sealed interface Event : Parcelable {
        @Parcelize
        data object OnStart : Event

        @Parcelize
        data object OnPause : Event

        @Parcelize
        data object OnResume : Event

        @Parcelize
        data object OnComplete : Event

        @Parcelize
        data class OnError(
            val code: Int = ScreenRecorderErrorCodes.GENERAL_ERROR,
            val reason: String? = null
        ) : Event
    }
}

private fun mediaProjectionCallback(onStop: () -> Unit = {}): MediaProjection.Callback {
    return object : MediaProjection.Callback() {
        override fun onStop() = onStop()
    }
}

