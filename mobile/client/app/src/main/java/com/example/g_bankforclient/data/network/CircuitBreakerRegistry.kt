package com.example.g_bankforclient.data.network

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CircuitBreakerRegistry @Inject constructor() {

    private val breakers = ConcurrentHashMap<String, ServiceCircuitBreaker>()

    fun get(serviceName: String): ServiceCircuitBreaker {
        return breakers.getOrPut(serviceName) { ServiceCircuitBreaker() }
    }
}
