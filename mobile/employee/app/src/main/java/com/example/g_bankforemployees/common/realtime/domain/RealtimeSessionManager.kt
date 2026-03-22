package com.example.g_bankforemployees.common.realtime.domain

import com.example.g_bankforemployees.common.realtime.domain.repository.RealtimeEventsRepository

class RealtimeSessionManager(
    private val realtimeEventsRepository: RealtimeEventsRepository,
) {

    @Volatile
    private var activeUserId: String? = null

    @Volatile
    private var observersCount: Int = 0

    @Synchronized
    fun observeUser(userId: String) {
        if (userId.isBlank()) return

        if (activeUserId != userId) {
            activeUserId = userId
            observersCount = 0
            realtimeEventsRepository.connect(userId)
        }

        observersCount += 1
    }

    @Synchronized
    fun stopObservingUser(userId: String) {
        if (activeUserId != userId) return

        observersCount = (observersCount - 1).coerceAtLeast(0)
        if (observersCount == 0) {
            activeUserId = null
            realtimeEventsRepository.disconnect()
        }
    }

    @Synchronized
    fun disconnect() {
        activeUserId = null
        observersCount = 0
        realtimeEventsRepository.disconnect()
    }
}
