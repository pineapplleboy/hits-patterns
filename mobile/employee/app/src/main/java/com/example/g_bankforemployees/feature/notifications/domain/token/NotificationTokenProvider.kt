package com.example.g_bankforemployees.feature.notifications.domain.token

interface NotificationTokenProvider {

    suspend fun getNotificationToken(): String?
}
