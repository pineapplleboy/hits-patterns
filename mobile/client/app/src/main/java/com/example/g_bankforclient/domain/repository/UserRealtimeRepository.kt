package com.example.g_bankforclient.domain.repository

import com.example.g_bankforclient.domain.models.UserRealtimeEvent
import kotlinx.coroutines.flow.Flow

interface UserRealtimeRepository {
    fun observeUserEvents(userId: String): Flow<UserRealtimeEvent>
}

