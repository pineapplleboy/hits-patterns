package com.example.g_bankforclient.feature.notifications.domain.usecase

import com.example.g_bankforclient.feature.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

class RegisterNotificationTokenUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {

    suspend operator fun invoke(): Result<Unit> =
        notificationRepository.registerToken()

    suspend operator fun invoke(token: String): Result<Unit> =
        notificationRepository.registerToken(token)
}
