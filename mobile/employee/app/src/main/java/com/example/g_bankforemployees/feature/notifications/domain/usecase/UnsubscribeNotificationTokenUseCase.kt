package com.example.g_bankforemployees.feature.notifications.domain.usecase

import com.example.g_bankforemployees.feature.notifications.domain.repository.NotificationRepository

class UnsubscribeNotificationTokenUseCase(
    private val notificationRepository: NotificationRepository,
) {

    suspend operator fun invoke(): Result<Unit> =
        notificationRepository.unsubscribeToken()
}
