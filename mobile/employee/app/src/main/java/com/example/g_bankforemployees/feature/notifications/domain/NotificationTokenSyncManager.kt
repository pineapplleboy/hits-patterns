package com.example.g_bankforemployees.feature.notifications.domain

import com.example.g_bankforemployees.feature.notifications.domain.usecase.RegisterNotificationTokenUseCase
import com.example.g_bankforemployees.feature.notifications.domain.usecase.UnsubscribeNotificationTokenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationTokenSyncManager(
    private val registerNotificationTokenUseCase: RegisterNotificationTokenUseCase,
    private val unsubscribeNotificationTokenUseCase: UnsubscribeNotificationTokenUseCase,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun register() {
        scope.launch {
            registerNotificationTokenUseCase()
        }
    }

    fun register(token: String) {
        scope.launch {
            registerNotificationTokenUseCase(token)
        }
    }

    suspend fun unsubscribe(): Result<Unit> =
        withContext(Dispatchers.IO) {
            unsubscribeNotificationTokenUseCase()
        }
}
