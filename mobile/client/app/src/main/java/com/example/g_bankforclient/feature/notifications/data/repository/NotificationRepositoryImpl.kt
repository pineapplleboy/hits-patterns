package com.example.g_bankforclient.feature.notifications.data.repository

import com.example.g_bankforclient.domain.TokenStorage
import com.example.g_bankforclient.feature.notifications.data.model.NotificationTokenRequestDto
import com.example.g_bankforclient.feature.notifications.data.remote.NotificationApi
import com.example.g_bankforclient.feature.notifications.domain.repository.NotificationRepository
import com.example.g_bankforclient.feature.notifications.domain.token.NotificationTokenProvider
import retrofit2.Response
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi,
    private val notificationTokenProvider: NotificationTokenProvider,
    private val tokenStorage: TokenStorage,
) : NotificationRepository {

    override suspend fun registerToken(): Result<Unit> =
        sendProvidedToken(notificationApi::registerToken)

    override suspend fun registerToken(token: String): Result<Unit> =
        sendToken(
            token = token,
            apiCall = notificationApi::registerToken,
        )

    override suspend fun unsubscribeToken(): Result<Unit> =
        sendProvidedToken(notificationApi::unsubscribeToken)

    private suspend fun sendProvidedToken(
        apiCall: suspend (NotificationTokenRequestDto) -> Response<Unit>,
    ): Result<Unit> {
        val token = runCatching { notificationTokenProvider.getNotificationToken() }
            .getOrElse { error ->
                return Result.failure(error)
            }

        return sendToken(token, apiCall)
    }

    private suspend fun sendToken(
        token: String?,
        apiCall: suspend (NotificationTokenRequestDto) -> Response<Unit>,
    ): Result<Unit> {
        val firebaseToken = token?.takeUnless { it.isBlank() }
            ?: return Result.failure(IllegalStateException("Notification token is missing"))

        if (tokenStorage.getToken().isNullOrBlank()) {
            return Result.failure(IllegalStateException("Authorization token is missing"))
        }

        return runCatching {
            val response = apiCall(NotificationTokenRequestDto(firebaseToken))
            if (!response.isSuccessful) {
                throw IllegalStateException("Notification request failed with code ${response.code()}")
            }
        }
    }
}
