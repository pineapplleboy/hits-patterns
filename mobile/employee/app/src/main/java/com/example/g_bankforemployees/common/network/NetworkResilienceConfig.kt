package com.example.g_bankforemployees.common.network

data class NetworkResilienceConfig(
    val retry: RetryConfig = RetryConfig(),
    val circuitBreaker: CircuitBreakerConfig = CircuitBreakerConfig(),
)

data class RetryConfig(
    val delayMillis: Long = 500,
    val retryableHttpCodes: Set<Int> = setOf(408, 429),
    val retryableHttpCodeRange: IntRange = 500..599,
)

data class CircuitBreakerConfig(
    val failureRateThreshold: Double = 0.7,
    val minimumFailures: Int = 5,
    val openStateDurationMillis: Long = 30_000,
)
