package com.example.g_bankforclient.data.network

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestHeadersInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val metadata = originalRequest.tag(RequestMetadata::class.java)
        val traceId = originalRequest.header(TRACE_ID_HEADER)
            ?: metadata?.traceId
            ?: UUID.randomUUID().toString()

        val builder = originalRequest.newBuilder()
            .header(SERVICE_FROM_HEADER, SERVICE_NAME)
            .header(TRACE_ID_HEADER, traceId)

        if (originalRequest.method.requiresIdempotencyKey()) {
            val idempotencyKey = originalRequest.header(IDEMPOTENCY_KEY_HEADER)
                ?: metadata?.idempotencyKey
                ?: UUID.randomUUID().toString()
            builder.header(IDEMPOTENCY_KEY_HEADER, idempotencyKey)
        }

        return chain.proceed(builder.build())
    }

    private fun String.requiresIdempotencyKey(): Boolean {
        return this == "POST" || this == "PUT" || this == "DELETE"
    }

    companion object {
        const val SERVICE_FROM_HEADER = "serviceFrom"
        const val TRACE_ID_HEADER = "traceId"
        const val IDEMPOTENCY_KEY_HEADER = "idempotencyKey"
        private const val SERVICE_NAME = "mobile-client"
    }
}
