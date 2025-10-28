package com.applivery.android.sdk.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.applivery.android.sdk.R

enum class NotificationChannels(val id: String) {
    Download("NOTIFICATION_CHANNEL_87234"),
    ScreenRecording("NOTIFICATION_CHANNEL:SCREEN_RECORDING")
}

fun Context.createNotificationChannel(channel: NotificationChannels) {
    val (name, description) = when (channel) {
        NotificationChannels.Download -> Pair(
            getString(R.string.applivery_updates_channel_name),
            getString(R.string.applivery_updates_channel_description)
        )

        NotificationChannels.ScreenRecording -> Pair(
            getString(R.string.appliveryScreenRecordingNotificationChannelTitle),
            getString(R.string.appliveryScreenRecordingNotificationChannelDescription)
        )
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel = NotificationChannel(channel.id, name, importance).apply {
            enableVibration(true)
            setDescription(description)
        }
        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel)
    }
}

