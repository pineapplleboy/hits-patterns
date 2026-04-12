package com.example.g_bankforemployees.common.network

import okhttp3.HttpUrl

class CircuitBreakerRegistry(
    private val config: CircuitBreakerConfig,
) {

    private val circuitBreakers = mutableMapOf<String, CircuitBreaker>()

    @Synchronized
    fun get(url: HttpUrl): CircuitBreaker {
        val serviceKey = url.serviceKey()
        return circuitBreakers.getOrPut(serviceKey) {
            CircuitBreaker(
                serviceKey = serviceKey,
                config = config,
            )
        }
    }

    private fun HttpUrl.serviceKey(): String =
        pathSegments.firstOrNull()
            ?.takeUnless { it.isBlank() }
            ?: host
}
