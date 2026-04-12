package com.example.g_bankforemployees.common.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class CircuitBreakerInterceptor(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val failureClassifier: NetworkFailureClassifier,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val circuitBreaker = circuitBreakerRegistry.get(request.url)
        if (!circuitBreaker.tryAcquire()) {
            throw IOException("Сервис временно недоступен")
        }

        return try {
            val response = chain.proceed(request)
            if (failureClassifier.isRetryableStatusCode(response.code)) {
                circuitBreaker.onFailure()
            } else {
                circuitBreaker.onSuccess()
            }
            response
        } catch (exception: IOException) {
            if (failureClassifier.isRetryableException(exception)) {
                circuitBreaker.onFailure()
            } else {
                circuitBreaker.onSuccess()
            }
            throw exception
        }
    }
}
