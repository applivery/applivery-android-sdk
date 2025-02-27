package com.applivery.android.sdk.feedback.video.recorder

import java.io.File

interface ScreenRecorderListener {

    fun onRecordingStarted()

    fun onRecordingCompleted(file: File)

    fun onRecordingError(errorCode: Int, reason: String?)

    fun onRecordingPaused()

    fun onRecordingResumed()
}
