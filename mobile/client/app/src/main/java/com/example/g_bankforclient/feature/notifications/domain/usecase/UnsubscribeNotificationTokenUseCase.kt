package com.example.g_bankforclient.feature.notifications.domain.usecase

import com.example.g_bankforclient.feature.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

class UnsubscribeNotificationTokenUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {

    suspend operator fun invoke(): Result<Unit> =
        notificationRepository.unsubscribeToken()
}
