package com.applivery.android.sdk.feedback.video.recorder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.os.ResultReceiver
import android.util.Log
import androidx.activity.result.ActivityResult
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ERROR_KEY
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ERROR_REASON_KEY
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.GENERAL_ERROR
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ON_COMPLETE_KEY
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ON_PAUSE_KEY
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ON_RESUME_KEY
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConstants.ON_START_KEY
import kotlinx.parcelize.Parcelize
import java.io.File
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
internal class ScreenRecorderConfig private constructor(
    val outputFile: File,
    val maxFileSize: Long,
    val maxDuration: Int
) : Parcelable {
    companion object {
        fun builder(): Builder = Builder()
    }

    class Builder {
        private var outputFile: File? = null
        private var maxFileSize: Long = ScreenRecorderDefaults.NoLimitMaxFileSize
        private var maxDuration: Int = 0

        fun outputLocation(outputFile: File) = apply { this.outputFile = outputFile }
        fun maxFileSize(maxFileSize: Long) = apply { this.maxFileSize = maxFileSize }
        fun maxDuration(maxDuration: Int) = apply { this.maxDuration = maxDuration }

        fun build() = ScreenRecorderConfig(
            outputFile = buildFile(outputFile ?: error("Output path is required")),
            maxFileSize = maxFileSize,
            maxDuration = maxDuration.takeIf { it > 0 } ?: error("Max duration > 0 is required")
        )

        private fun buildFile(outputFile: File): File {
            if (!outputFile.exists()) error("Output directory does not exists")
            if (!outputFile.isDirectory) error("Output path is not a directory")

            val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
            val curDate = Date(System.currentTimeMillis())
            val curTime = formatter.format(curDate).replace(" ", "")
            val filePath = "${outputFile.path}/$curTime.mp4"
            return File(filePath)
        }
    }
}

internal class ScreenRecorder(
    private val context: Context,
    private val config: ScreenRecorderConfig,
    private val listener: ScreenRecorderListener
) : FileObserver.OnReadyListener {
    private val fileObserver: FileObserver = FileObserver(
        path = config.outputFile.parent ?: error("Output path has no parent"),
        listener = this
    )
    private var wasOnErrorCalled: Boolean = false
    private var isRecordingPaused: Boolean = false
    private var countDown: Countdown? = null

    fun startScreenRecording(activityResult: ActivityResult) {
        startService(activityResult)
    }

    fun stopScreenRecording() {
        val service = Intent(context, ScreenRecorderService::class.java)
        context.stopService(service)
    }

    fun pauseScreenRecording() {
        isRecordingPaused = true
        val intent = ScreenRecorderService.getIntent(context).apply {
            setAction(ScreenRecorderServiceActions.Pause)
        }
        context.startService(intent)
    }

    fun resumeScreenRecording() {
        isRecordingPaused = false
        val intent = ScreenRecorderService.getIntent(context).apply {
            setAction(ScreenRecorderServiceActions.Resume)
        }
        context.startService(intent)
    }

    private fun startService(activityResult: ActivityResult) {
        try {
            fileObserver.startWatching()
            val listener = object : ResultReceiver(Handler(Looper.getMainLooper())) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                    super.onReceiveResult(resultCode, resultData)
                    if (resultCode == Activity.RESULT_OK) {
                        val errorListener = resultData.getString(ERROR_REASON_KEY)
                        val onComplete = resultData.getString(ON_COMPLETE_KEY)
                        val onStartCode = resultData.getInt(ON_START_KEY)
                        val errorCode = resultData.getInt(ERROR_KEY)
                        if (errorListener != null) {
                            stopCountDown()
                            fileObserver.stopWatching()
                            wasOnErrorCalled = true
                            if (errorCode > 0) {
                                listener.onRecordingError(errorCode, errorListener)
                            } else {
                                listener.onRecordingError(GENERAL_ERROR, errorListener)
                            }
                            try {
                                val service = Intent(
                                    context,
                                    ScreenRecorderService::class.java
                                )
                                context.stopService(service)
                            } catch (e: Exception) {
                                // Can be ignored
                            }
                        } else if (onComplete != null) {
                            //Stop countdown if it was set
                            stopCountDown()
                            wasOnErrorCalled = false
                        } else if (onStartCode != 0) {
                            listener.onRecordingStarted()
                            startCountdown()
                        }
                        // OnPause/onResume was called
                        val onPause = resultData.getString(ON_PAUSE_KEY)
                        val onResume = resultData.getString(ON_RESUME_KEY)
                        if (onPause != null) {
                            listener.onRecordingPaused()
                        }
                        if (onResume != null) {
                            listener.onRecordingResumed()
                        }
                    }
                }
            }
            val serviceIntent = ScreenRecorderService.getIntent(
                context = context,
                config = config,
                mediaProjectionPermissionResult = activityResult,
                listener = listener
            )
            context.startService(serviceIntent)
        } catch (e: Exception) {
            listener.onRecordingError(0, Log.getStackTraceString(e))
        }
    }

    private fun startCountdown() {
        countDown = object : Countdown(config.maxDuration.toLong() * 1000, 1000, 0) {

            override fun onTick(timeLeft: Long) = Unit

            override fun onFinished() {
                onTick(0)
                Handler(Looper.getMainLooper()).post {
                    try {
                        stopScreenRecording()
                        fileObserver.stopWatching()
                        listener.onRecordingCompleted(config.outputFile)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onStop() = Unit
        }
        countDown?.start()
    }

    private fun stopCountDown() {
        countDown?.stop()
    }

    override fun onFileReady() {
        fileObserver.stopWatching()
        listener.onRecordingCompleted(config.outputFile)
    }
}
