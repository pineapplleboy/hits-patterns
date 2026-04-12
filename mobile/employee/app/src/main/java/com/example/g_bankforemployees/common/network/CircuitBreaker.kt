package com.example.g_bankforemployees.common.network

import android.util.Log
import kotlin.math.roundToInt

private const val CIRCUIT_BREAKER_TAG = "CircuitBreaker"

private enum class CircuitState {
    CLOSED,
    OPEN,
}

class CircuitBreaker(
    private val serviceKey: String,
    private val config: CircuitBreakerConfig,
    private val nowMillis: () -> Long = System::currentTimeMillis,
) {

    private var state = CircuitState.CLOSED
    private var calls = 0
    private var failures = 0
    private var openedAtMillis = 0L

    @Synchronized
    fun tryAcquire(): Boolean {
        return when (state) {
            CircuitState.CLOSED -> true
            CircuitState.OPEN -> {
                if (nowMillis() - openedAtMillis >= config.openStateDurationMillis) {
                    close()
                    true
                } else {
                    false
                }
            }
        }
    }

    @Synchronized
    fun onSuccess() {
        if (state == CircuitState.CLOSED) {
            recordOutcome(isFailure = false)
        }
    }

    @Synchronized
    fun onFailure() {
        if (state == CircuitState.CLOSED) {
            recordOutcome(isFailure = true)
        }
    }

    private fun recordOutcome(isFailure: Boolean) {
        calls += 1
        if (isFailure) failures += 1

        val failureRate = failures.toDouble() / calls
        Log.d(CIRCUIT_BREAKER_TAG, "Сервис $serviceKey - ${(failureRate * 100).roundToInt()}% с ошибкой")
        if (failures >= config.minimumFailures && failureRate > config.failureRateThreshold) {
            state = CircuitState.OPEN
            openedAtMillis = nowMillis()
        }
    }

    private fun close() {
        state = CircuitState.CLOSED
        calls = 0
        failures = 0
    }
}
