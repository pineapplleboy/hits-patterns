package com.example.g_bankforclient.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
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

        val traceId = request.header(RequestHeadersInterceptor.TRACE_ID_HEADER)
            ?: request.tag(RequestMetadata::class.java)?.traceId
            ?: "unknown-trace"
        val breaker = circuitBreakerRegistry.get(serviceName)
        val permit = breaker.beforeRequest()
        if (permit is ServiceCircuitBreaker.Permit.Denied) {
            Log.w(
                LOG_TAG,
                "traceId=$traceId service=$serviceName breaker=open errorPercent=${permit.errorPercent}"
            )
            throw CircuitBreakerOpenException(serviceName, permit.retryAfterMillis)
        }

        val maxAttempts = if (request.isRetryable()) 3 else 1
        var attempt = 0
        var lastException: IOException? = null

        while (attempt < maxAttempts) {
            attempt++
            val startedAt = System.nanoTime()
            try {
                val response = chain.proceed(request)
                val durationMs = (System.nanoTime() - startedAt) / 1_000_000
                val isFailure = response.code >= 500
                val snapshot = if (isFailure) {
                    breaker.recordFailure()
                } else {
                    breaker.recordSuccess()
                }

                logTrace(
                    serviceName = serviceName,
                    traceId = traceId,
                    attempt = attempt,
                    method = request.method,
                    path = request.url.encodedPath,
                    code = response.code,
                    durationMs = durationMs,
                    snapshot = snapshot
                )

                if (!isFailure || attempt >= maxAttempts) {
                    return response
                }

                response.close()
                sleepBeforeRetry(attempt)
            } catch (exception: IOException) {
                lastException = exception
                val durationMs = (System.nanoTime() - startedAt) / 1_000_000
                val snapshot = breaker.recordFailure()
                Log.w(
                    LOG_TAG,
                    "traceId=$traceId service=$serviceName attempt=$attempt method=${request.method} " +
                            "path=${request.url.encodedPath} durationMs=$durationMs errorPercent=${snapshot.errorPercent} " +
                            "state=${snapshot.state} error=${exception.message}"
                )

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

    private fun logTrace(
        serviceName: String,
        traceId: String,
        attempt: Int,
        method: String,
        path: String,
        code: Int,
        durationMs: Long,
        snapshot: ServiceCircuitBreaker.Snapshot,
    ) {
        val priority = if (code >= 500) Log.WARN else Log.INFO
        Log.println(
            priority,
            LOG_TAG,
            "traceId=$traceId service=$serviceName attempt=$attempt method=$method path=$path " +
                    "code=$code durationMs=$durationMs errorPercent=${snapshot.errorPercent} state=${snapshot.state}"
        )
    }

    companion object {
        private const val INTERNAL_HOST = "91.227.18.176"
        private const val LOG_TAG = "NetworkTrace"
    }
}
