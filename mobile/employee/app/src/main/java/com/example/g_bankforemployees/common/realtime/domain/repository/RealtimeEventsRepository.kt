package com.example.g_bankforemployees.common.realtime.domain.repository

import com.example.g_bankforemployees.common.realtime.domain.model.RealtimeEvent
import kotlinx.coroutines.flow.SharedFlow

interface RealtimeEventsRepository {

    val events: SharedFlow<RealtimeEvent>

    fun connect(userId: String)

    fun disconnect()
}
