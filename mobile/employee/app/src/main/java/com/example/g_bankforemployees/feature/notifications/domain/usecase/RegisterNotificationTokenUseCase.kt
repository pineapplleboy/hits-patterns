package com.example.g_bankforemployees.feature.notifications.domain.usecase

import com.example.g_bankforemployees.feature.notifications.domain.repository.NotificationRepository

class RegisterNotificationTokenUseCase(
    private val notificationRepository: NotificationRepository,
) {

    suspend operator fun invoke(): Result<Unit> =
        notificationRepository.registerToken()

    suspend operator fun invoke(token: String): Result<Unit> =
        notificationRepository.registerToken(token)
}
