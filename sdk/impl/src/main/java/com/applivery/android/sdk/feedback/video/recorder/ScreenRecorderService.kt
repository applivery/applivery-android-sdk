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
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ERROR_KEY
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ERROR_REASON_KEY
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.MAX_FILE_SIZE_REACHED_ERROR
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ON_COMPLETE
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ON_COMPLETE_KEY
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ON_PAUSE
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ON_PAUSE_KEY
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ON_RESUME
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ON_RESUME_KEY
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ON_START
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ON_START_KEY
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.SETTINGS_ERROR
import com.applivery.android.sdk.notifications.NotificationChannels
import com.applivery.android.sdk.notifications.createNotificationChannel
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

        //Notification
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
            val bundle = bundleOf(ERROR_REASON_KEY to Log.getStackTraceString(e))
            listener.send(Activity.RESULT_OK, bundle)
        }

        try {
            initMediaProjection(activityResult)
        } catch (e: Exception) {
            val bundle = bundleOf(ERROR_REASON_KEY to Log.getStackTraceString(e))
            listener.send(Activity.RESULT_OK, bundle)
        }

        //Init VirtualDisplay
        try {
            initVirtualDisplay()
        } catch (e: Exception) {
            val bundle = bundleOf(ERROR_REASON_KEY to Log.getStackTraceString(e))
            listener.send(Activity.RESULT_OK, bundle)
        }

        mediaRecorder?.setOnErrorListener { _: MediaRecorder?, what: Int, _: Int ->
            if (what == 268435556 && hasMaxFileBeenReached) {
                // Benign error b/c recording is too short and has no frames. See SO: https://stackoverflow.com/questions/40616466/mediarecorder-stop-failed-1007
                return@setOnErrorListener
            }
            val bundle = bundleOf(
                ERROR_REASON_KEY to what.toString(),
                ERROR_KEY to SETTINGS_ERROR
            )
            listener.send(Activity.RESULT_OK, bundle)
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
                val bundle = bundleOf(
                    ERROR_REASON_KEY to "Max file size reached",
                    ERROR_KEY to MAX_FILE_SIZE_REACHED_ERROR
                )
                listener.send(Activity.RESULT_OK, bundle)
            }
        }

        //Start Recording
        try {
            mediaRecorder?.start()
            val bundle = bundleOf(ON_START_KEY to ON_START)
            listener.send(Activity.RESULT_OK, bundle)
        } catch (e: Exception) {
            val bundle = bundleOf(
                ERROR_REASON_KEY to Log.getStackTraceString(e),
                ERROR_KEY to SETTINGS_ERROR
            )
            listener.send(Activity.RESULT_OK, bundle)
        }

        return START_STICKY
    }

    //Pause Recording
    private fun pauseRecording() {
        mediaRecorder?.pause()
        val bundle = bundleOf(ON_PAUSE_KEY to ON_PAUSE)
        listener.send(Activity.RESULT_OK, bundle)
    }

    //Resume Recording
    private fun resumeRecording() {
        mediaRecorder?.resume()
        val bundle = bundleOf(ON_RESUME_KEY to ON_RESUME)
        listener.send(Activity.RESULT_OK, bundle)
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
        callOnComplete()
    }

    private fun callOnComplete() {
        val bundle = bundleOf(ON_COMPLETE_KEY to ON_COMPLETE)
        listener.send(Activity.RESULT_OK, bundle)
    }

    private fun resetAll() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        virtualDisplay?.release()
        virtualDisplay = null
        mediaRecorder?.setOnErrorListener(null)
        mediaRecorder?.reset()
        mediaProjection?.stop()
    }

    override fun onBind(intent: Intent): IBinder? = null

    companion object {
        private const val TAG = "ScreenRecorderService"
        private const val NotificationId = 43566
        private const val ExtraConfig = "extra:config"
        private const val ExtraActivityResult = "extra:activityResult"
        private const val ExtraListener = "extra:listener"

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
}

private fun mediaProjectionCallback(onStop: () -> Unit = {}): MediaProjection.Callback {
    return object : MediaProjection.Callback() {
        override fun onStop() = onStop()
    }
}

internal inline fun <reified T : Parcelable> Intent.parcelable(name: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(name, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(name)
    }
}