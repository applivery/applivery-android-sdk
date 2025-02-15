package com.applivery.android.sdk.feedback

import android.net.Uri
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.feedback.video.ScreenRecorder
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal interface ShakeFeedbackChecker {

    fun start()

    fun enable(enable: Boolean)
}

internal class ShakeFeedbackCheckerImpl(
    private val shakeDetector: ShakeDetector,
    private val logger: DomainLogger,
    private val hostActivityProvider: HostActivityProvider,
    private val screenRecorder: ScreenRecorder,
) : ShakeFeedbackChecker, ShakeDetector.Listener, DefaultLifecycleObserver {

    private var isEnabled: Boolean = false

    private val coroutineScope = MainScope()
    private var recordingJob: Job? = null

    override fun start() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun enable(enable: Boolean) {
        isEnabled = enable
        if (isEnabled) {
            shakeDetector.start(this)
        } else {
            shakeDetector.stop()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        if (isEnabled) {
            shakeDetector.start(this)
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        shakeDetector.stop()
    }

    override fun onShake(count: Int) {
        if (recordingJob?.isActive == true) {
            Log.d("ShakeFeedbackChecker", "onShake: already recording")
            return
        }

        recordingJob = coroutineScope.launch {
            screenRecorder.start().fold(
                ifLeft = {
                    // TODO: handle error
                    Log.d("ShakeFeedbackChecker", "onShake: failed to start recording")
                },
                ifRight = {
                    Log.d("ShakeFeedbackChecker", "file recorded $it")
                    val activity = hostActivityProvider.activity
                    if (activity == null) {
                        logger.noActivityFoundForFeedbackView()
                        return@launch
                    }
                    val arguments = FeedbackArguments.Video(uri = Uri.fromFile(it))
                    activity.startActivity(FeedbackActivity.getIntent(activity, arguments))
                }
            )
        }
    }
}