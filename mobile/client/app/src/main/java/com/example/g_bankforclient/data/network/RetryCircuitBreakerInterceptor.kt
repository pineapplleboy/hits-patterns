package com.example.g_bankforclient.data.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetryCircuitBreakerInterceptor @Inject constructor(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val serviceName = request.resolveServiceName()
        if (serviceName == null) {
            return chain.proceed(request)
        }

        val breaker = circuitBreakerRegistry.get(serviceName)
        val permit = breaker.beforeRequest()
        if (permit is ServiceCircuitBreaker.Permit.Denied) {
            return request.buildCircuitOpenResponse(serviceName, permit.retryAfterMillis)
        }

        val maxAttempts = if (request.isRetryable()) 3 else 1
        var attempt = 0
        var lastException: IOException? = null

        while (attempt < maxAttempts) {
            attempt++
            try {
                val response = chain.proceed(request)
                val isFailure = response.code >= 500
                if (isFailure) {
                    breaker.recordFailure()
                } else {
                    breaker.recordSuccess()
                }

                if (!isFailure || attempt >= maxAttempts) {
                    return response
                }

                response.close()
                sleepBeforeRetry(attempt)
            } catch (exception: IOException) {
                lastException = exception
                breaker.recordFailure()

                if (attempt >= maxAttempts) {
                    throw exception
                }

                sleepBeforeRetry(attempt)
            }
        }

        throw lastException ?: IOException("Request failed without response")
    }

    private fun Request.isRetryable(): Boolean {
        return method == "GET" ||
                header(RequestHeadersInterceptor.IDEMPOTENCY_KEY_HEADER) != null ||
                tag(RequestMetadata::class.java)?.idempotencyKey != null
    }

    private fun Request.resolveServiceName(): String? {
        if (url.host != INTERNAL_HOST) return null
        return url.pathSegments.firstOrNull()?.takeIf { it.isNotBlank() }
    }

    private fun sleepBeforeRetry(attempt: Int) {
        try {
            Thread.sleep(300L * attempt)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    companion object {
        private const val INTERNAL_HOST = "91.227.18.176"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    private fun Request.buildCircuitOpenResponse(
        serviceName: String,
        retryAfterMillis: Long,
    ): Response {
        val seconds = retryAfterMillis / 1000 + 1
        val message = "Сервис $serviceName временно недоступен. Повторите через $seconds сек."
        val body = """{"code":503,"message":"$message"}"""

        return Response.Builder()
            .request(this)
            .protocol(Protocol.HTTP_1_1)
            .code(503)
            .message("Service Unavailable")
            .body(body.toResponseBody(JSON_MEDIA_TYPE))
            .build()
    }
}
