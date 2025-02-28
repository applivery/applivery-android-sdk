package com.applivery.android.sdk.feedback.video.recorder

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.os.ResultReceiver
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResult
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.feedback.video.bubble.VideoReporterBubble
import com.applivery.android.sdk.feedback.video.bubble.VideoReporterFloatingViewManager
import com.applivery.android.sdk.ui.parcelable
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
    private val domainLogger: DomainLogger,
    private val listener: ScreenRecorderListener,
) : FileObserver.OnReadyListener {
    private val fileObserver: FileObserver = FileObserver(
        path = config.outputFile.parent ?: error("Output path has no parent"),
        listener = this
    )
    private var wasOnErrorCalled: Boolean = false
    private var isRecordingPaused: Boolean = false
    private var countDown: Countdown? = null

    private val floatingViewManager = VideoReporterFloatingViewManager(context)

    override fun onFileReady() {
        fileObserver.stopWatching()
        listener.onRecordingCompleted(config.outputFile)
    }

    fun startScreenRecording(activityResult: ActivityResult) {
        startService(activityResult)
    }

    fun stopScreenRecording() {
        context.stopService(ScreenRecorderService.getIntent(context))
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
                    val eventKey = ScreenRecorderService.EventKey
                    val event = resultData.parcelable<ScreenRecorderService.Event>(eventKey)
                    when (event) {
                        is ScreenRecorderService.Event.OnStart -> onStart()
                        is ScreenRecorderService.Event.OnResume -> onResume()
                        is ScreenRecorderService.Event.OnPause -> onPause()
                        is ScreenRecorderService.Event.OnComplete -> onComplete()
                        is ScreenRecorderService.Event.OnError -> onError(event.code, event.reason)
                        null -> Unit
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
            fileObserver.stopWatching()
            listener.onRecordingError(0, Log.getStackTraceString(e))
        }
    }

    private fun onStart() {
        listener.onRecordingStarted()
        startCountdown()
        showOverlay()
    }

    private fun onResume() {
        listener.onRecordingResumed()
    }

    private fun onPause() {
        listener.onRecordingPaused()
    }

    private fun onComplete() {
        stopCountDown()
        wasOnErrorCalled = false
        hideOverlay()
    }

    private fun onError(code: Int, reason: String?) {
        stopCountDown()
        fileObserver.stopWatching()
        wasOnErrorCalled = true
        listener.onRecordingError(code, reason)
        stopScreenRecording()
        hideOverlay()
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

    private fun showOverlay() {
        if (!Settings.canDrawOverlays(context)) {
            domainLogger.noOverlayPermission()
            return
        }
        floatingViewManager.show {
            VideoReporterBubble(
                countDowTimeInSeconds = config.maxDuration,
                onFinished = ::stopScreenRecording,
            )
        }
    }

    private fun hideOverlay() {
        if (!Settings.canDrawOverlays(context)) {
            domainLogger.noOverlayPermission()
            return
        }
        floatingViewManager.hide()
    }
}
