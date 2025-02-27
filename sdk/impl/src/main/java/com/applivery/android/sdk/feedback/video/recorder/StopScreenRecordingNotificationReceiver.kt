package com.applivery.android.sdk.feedback.video.recorder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

internal class StopScreenRecordingNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, ScreenRecorderService::class.java)
        context.stopService(service)
    }
}
