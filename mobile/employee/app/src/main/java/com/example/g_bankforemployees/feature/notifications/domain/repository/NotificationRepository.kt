package com.example.g_bankforemployees.feature.notifications.domain.repository

interface NotificationRepository {

    suspend fun registerToken(): Result<Unit>

    suspend fun registerToken(token: String): Result<Unit>

    suspend fun unsubscribeToken(): Result<Unit>
}
