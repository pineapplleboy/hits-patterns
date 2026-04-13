package com.example.g_bankforclient.feature.notifications.domain.token

interface NotificationTokenProvider {
    suspend fun getNotificationToken(): String?
}
