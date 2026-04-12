package com.example.g_bankforemployees.feature.notifications.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

const val EMPLOYEE_NOTIFICATION_CHANNEL_ID = "employee_notifications"

fun Context.createEmployeeNotificationChannel() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        return
    }

    val notificationManager = getSystemService(NotificationManager::class.java)
    val channel = NotificationChannel(
        EMPLOYEE_NOTIFICATION_CHANNEL_ID,
        "G-Bank notifications",
        NotificationManager.IMPORTANCE_DEFAULT,
    )
    notificationManager.createNotificationChannel(channel)
}
