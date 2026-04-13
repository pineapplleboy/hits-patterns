package com.example.g_bankforclient.feature.notifications.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

const val CLIENT_NOTIFICATION_CHANNEL_ID = "client_notifications"

fun Context.createClientNotificationChannel() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        return
    }

    val notificationManager = getSystemService(NotificationManager::class.java)
    val channel = NotificationChannel(
        CLIENT_NOTIFICATION_CHANNEL_ID,
        "G-Bank notifications",
        NotificationManager.IMPORTANCE_DEFAULT,
    )
    notificationManager.createNotificationChannel(channel)
}
