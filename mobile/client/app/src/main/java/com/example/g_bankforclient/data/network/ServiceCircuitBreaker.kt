package com.example.g_bankforclient.data.network

import kotlin.math.roundToInt

class ServiceCircuitBreaker(
    private val windowSize: Int = 10,
    private val errorThreshold: Double = 0.7,
    private val openDurationMs: Long = 30_000L,
) {
    private val outcomes = ArrayDeque<Boolean>()
    private var state: State = State.CLOSED
    private var openedAtMillis: Long = 0L
    private var halfOpenProbeInFlight: Boolean = false

    @Synchronized
    fun beforeRequest(nowMillis: Long = System.currentTimeMillis()): Permit {
        return when (state) {
            State.CLOSED -> Permit.Allowed(errorPercent = currentErrorPercent())
            State.OPEN -> {
                val elapsed = nowMillis - openedAtMillis
                if (elapsed >= openDurationMs) {
                    state = State.HALF_OPEN
                    halfOpenProbeInFlight = true
                    Permit.Allowed(errorPercent = currentErrorPercent())
                } else {
                    Permit.Denied(
                        retryAfterMillis = (openDurationMs - elapsed).coerceAtLeast(0L),
                        errorPercent = currentErrorPercent()
                    )
                }
            }

            State.HALF_OPEN -> {
                if (halfOpenProbeInFlight) {
                    Permit.Denied(
                        retryAfterMillis = openDurationMs,
                        errorPercent = currentErrorPercent()
                    )
                } else {
                    halfOpenProbeInFlight = true
                    Permit.Allowed(errorPercent = currentErrorPercent())
                }
            }
        }
    }

    @Synchronized
    fun recordSuccess(nowMillis: Long = System.currentTimeMillis()): Snapshot {
        if (state == State.HALF_OPEN) {
            state = State.CLOSED
            halfOpenProbeInFlight = false
            outcomes.clear()
        } else {
            appendOutcome(success = true)
        }
        return snapshot(nowMillis)
    }

    @Synchronized
    fun recordFailure(nowMillis: Long = System.currentTimeMillis()): Snapshot {
        if (state == State.HALF_OPEN) {
            state = State.OPEN
            openedAtMillis = nowMillis
            halfOpenProbeInFlight = false
        } else {
            appendOutcome(success = false)
            if (outcomes.size >= windowSize && currentErrorRate() > errorThreshold) {
                state = State.OPEN
                openedAtMillis = nowMillis
                halfOpenProbeInFlight = false
            }
        }
        return snapshot(nowMillis)
    }

    @Synchronized
    fun snapshot(nowMillis: Long = System.currentTimeMillis()): Snapshot {
        val retryAfterMillis = if (state == State.OPEN) {
            (openDurationMs - (nowMillis - openedAtMillis)).coerceAtLeast(0L)
        } else {
            0L
        }
        return Snapshot(
            state = state.name,
            errorPercent = currentErrorPercent(),
            retryAfterMillis = retryAfterMillis,
            sampleSize = outcomes.size
        )
    }

    @Synchronized
    private fun appendOutcome(success: Boolean) {
        if (outcomes.size == windowSize) {
            outcomes.removeFirst()
        }
        outcomes.addLast(success)
    }

    @Synchronized
    private fun currentErrorRate(): Double {
        if (outcomes.isEmpty()) return 0.0
        val failures = outcomes.count { success -> !success }
        return failures.toDouble() / outcomes.size.toDouble()
    }

    @Synchronized
    private fun currentErrorPercent(): Int {
        return (currentErrorRate() * 100).roundToInt()
    }

    enum class State {
        CLOSED,
        OPEN,
        HALF_OPEN,
    }

    sealed class Permit {
        data class Allowed(val errorPercent: Int) : Permit()
        data class Denied(
            val retryAfterMillis: Long,
            val errorPercent: Int,
        ) : Permit()
    }

    data class Snapshot(
        val state: String,
        val errorPercent: Int,
        val retryAfterMillis: Long,
        val sampleSize: Int,
    )
}
