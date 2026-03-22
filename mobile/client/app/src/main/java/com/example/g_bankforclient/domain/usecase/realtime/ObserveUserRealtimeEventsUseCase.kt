package com.example.g_bankforclient.domain.usecase.realtime

import com.example.g_bankforclient.domain.models.UserRealtimeEvent
import com.example.g_bankforclient.domain.repository.UserRealtimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserRealtimeEventsUseCase @Inject constructor(
    private val repository: UserRealtimeRepository
) {
    operator fun invoke(userId: String): Flow<UserRealtimeEvent> =
        repository.observeUserEvents(userId)
}

