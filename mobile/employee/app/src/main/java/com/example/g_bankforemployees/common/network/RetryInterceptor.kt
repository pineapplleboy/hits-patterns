package com.example.g_bankforemployees.common.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(
    private val config: RetryConfig,
    private val failureClassifier: NetworkFailureClassifier,
    private val sleep: (Long) -> Unit = Thread::sleep,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        while (true) {
            try {
                val response = chain.proceed(request)
                if (!failureClassifier.isRetryableStatusCode(response.code)) {
                    return response
                }

                response.close()
                waitBeforeRetry()
            } catch (exception: IOException) {
                if (!failureClassifier.isRetryableException(exception)) {
                    throw exception
                }

                waitBeforeRetry()
            }
        }
    }

    private fun waitBeforeRetry() {
        try {
            sleep(config.delayMillis)
        } catch (exception: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IOException("Retry interrupted", exception)
        }
    }
}
