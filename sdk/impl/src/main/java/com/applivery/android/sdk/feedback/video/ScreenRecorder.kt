package com.applivery.android.sdk.feedback.video

import android.app.Activity
import android.content.Context
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.InternalError
import com.hbisoft.hbrecorder.HBRecorder
import com.hbisoft.hbrecorder.HBRecorderListener
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

internal interface ScreenRecorder {

    suspend fun start(): Either<DomainError, File>

    suspend fun stop()

}

internal class HBScreenRecorder(
    private val context: Context,
    private val hostActivityProvider: HostActivityProvider
) : ScreenRecorder, HBRecorderListener {

    private val recorder = HBRecorder(context, this).apply {
        isAudioEnabled(false)
        setOutputPath(outputDirectory?.path)
        setMaxDuration(MaxVideoDurationInSecods)
    }

    private val outputDirectory get() = context.externalCacheDir

    private var currentRecordingCont: CancellableContinuation<Either<DomainError, File>>? = null

    override suspend fun start(): Either<DomainError, File> {
        val activity = hostActivityProvider.activity ?: return InternalError().left()
        val mediaPermissionResult = activity.requestMediaPermission()
        if (mediaPermissionResult.resultCode != Activity.RESULT_OK) return InternalError().left()
        return suspendCancellableCoroutine { cont ->
            currentRecordingCont = cont
            recorder.startScreenRecording(
                mediaPermissionResult.data,
                mediaPermissionResult.resultCode
            )
        }
    }

    override suspend fun stop() {
        recorder.stopScreenRecording()
    }

    override fun HBRecorderOnStart() = Unit

    override fun HBRecorderOnComplete() {
        val continuation = currentRecordingCont ?: return
        if (!continuation.isActive) return
        val recordingFile = recorder.getRecordingFile().getOrNull()
        if (recordingFile == null) {
            continuation.resume(InternalError().left())
            return
        }
        if (!recordingFile.exists() || !recordingFile.isFile) {
            continuation.resume(InternalError().left())
        }
        continuation.resume(recordingFile.right())
        currentRecordingCont = null
    }

    override fun HBRecorderOnError(errorCode: Int, reason: String?) {
        // TODO: handle errors
        val continuation = currentRecordingCont ?: return
        if (!continuation.isActive) return
        continuation.resume(InternalError().left())
        currentRecordingCont = null
    }

    override fun HBRecorderOnPause() = Unit

    override fun HBRecorderOnResume() = Unit

    private fun HBRecorder.getRecordingFile(): Result<File> {
        return runCatching { File(recorder.filePath) }
    }

    companion object {
        private const val MaxVideoDurationInSecods = 30
    }
}
