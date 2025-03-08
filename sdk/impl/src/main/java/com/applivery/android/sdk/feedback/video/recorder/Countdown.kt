package com.applivery.android.sdk.feedback.video.recorder

import java.util.Timer
import java.util.TimerTask

abstract class Countdown(
    totalTime: Long,
    private val interval: Long,
    private val delay: Long = 0
) : Timer(true) {

    private var startTime: Long = -1
    private var restart = false

    private val task: TimerTask = object : TimerTask() {
        override fun run() {
            val timeLeft: Long = if (startTime < 0 || restart) {
                startTime = scheduledExecutionTime()
                restart = false
                totalTime
            } else {
                totalTime - (scheduledExecutionTime() - startTime)
            }

            if (timeLeft <= 0) {
                this.cancel()
                startTime = -1
                onFinished()
                return
            }

            onTick(timeLeft)
        }
    }

    fun start() {
        schedule(task, delay, interval)
    }

    fun stop() {
        onStop()
        task.cancel()
        cancel()
        purge()
    }

    abstract fun onTick(timeLeft: Long)
    abstract fun onFinished()
    abstract fun onStop()
}
