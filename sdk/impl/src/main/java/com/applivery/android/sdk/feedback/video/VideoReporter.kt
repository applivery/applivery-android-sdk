package com.applivery.android.sdk.feedback.video

import android.app.Activity
import android.content.Context
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.InternalError
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorder
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderConfig
import com.applivery.android.sdk.feedback.video.recorder.ScreenRecorderListener
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

internal interface VideoReporter {

    suspend fun start(): Either<DomainError, File>

    suspend fun stop()
}

internal class VideoReporterImpl(
    private val context: Context,
    private val hostActivityProvider: HostActivityProvider,
    domainLogger: DomainLogger
) : VideoReporter, ScreenRecorderListener {

    private val outputDirectory get() = context.externalCacheDir ?: context.cacheDir

    private val recorderConfig = ScreenRecorderConfig.builder()
        .outputLocation(outputDirectory)
        .maxDuration(MaxVideoDurationInSecods)
        .build()

    private val recorder = ScreenRecorder(context, recorderConfig, domainLogger, this)

    private var currentRecordingCont: CancellableContinuation<Either<DomainError, File>>? = null

    override suspend fun start(): Either<DomainError, File> {
        val activity = hostActivityProvider.activity ?: return InternalError().left()
        val mediaPermissionResult = activity.requestMediaPermission()
        if (mediaPermissionResult.resultCode != Activity.RESULT_OK) return InternalError().left()
        return suspendCancellableCoroutine { cont ->
            currentRecordingCont = cont
            recorder.startScreenRecording(mediaPermissionResult)
        }
    }

    override suspend fun stop() {
        recorder.stopScreenRecording()
    }

    override fun onRecordingStarted() = Unit

    override fun onRecordingCompleted(file: File) {
        val continuation = currentRecordingCont ?: return
        if (!continuation.isActive) return
        if (!file.exists() || !file.isFile) {
            continuation.resume(InternalError().left())
            return
        }
        continuation.resume(file.right())
        currentRecordingCont = null
    }

    override fun onRecordingError(errorCode: Int, reason: String?) {
        val continuation = currentRecordingCont ?: return
        if (!continuation.isActive) return
        continuation.resume(RecordingError(reason).left())
        currentRecordingCont = null
    }

    override fun onRecordingPaused() = Unit

    override fun onRecordingResumed() = Unit

    companion object {
        private const val MaxVideoDurationInSecods = 30
    }
}

internal class RecordingError(message: String?) : DomainError(message)
