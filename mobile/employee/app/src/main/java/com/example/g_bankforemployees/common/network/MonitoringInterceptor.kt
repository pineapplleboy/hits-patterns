package com.example.g_bankforemployees.common.network

import com.example.g_bankforemployees.feature.authorization.domain.TokenStorage
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.security.MessageDigest
import java.util.UUID

private const val HEADER_SERVICE_FROM = "serviceFrom"
private const val HEADER_TRACE_ID = "traceId"
private const val HEADER_IDEMPOTENCY_KEY = "idempotencyKey"
private const val SERVICE_FROM = "employee-mobile-app"

class MonitoringInterceptor(
    private val tokenStorage: TokenStorage,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()

        if (request.header(HEADER_SERVICE_FROM).isNullOrBlank()) {
            builder.header(HEADER_SERVICE_FROM, SERVICE_FROM)
        }

        if (request.header(HEADER_TRACE_ID).isNullOrBlank()) {
            builder.header(HEADER_TRACE_ID, UUID.randomUUID().toString())
        }

        if (request.method in METHODS_WITH_IDEMPOTENCY_KEY &&
            request.header(HEADER_IDEMPOTENCY_KEY).isNullOrBlank()
        ) {
            builder.header(HEADER_IDEMPOTENCY_KEY, createIdempotencyKey(request))
        }

        return chain.proceed(builder.build())
    }

    private fun createIdempotencyKey(request: Request): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(tokenStorage.getToken().orEmpty().toByteArray())
        digest.update(0)
        digest.update(request.body?.toByteArray() ?: ByteArray(0))
        return "$SERVICE_FROM-${digest.digest().toHexString()}"
    }

    private fun RequestBody.toByteArray(): ByteArray {
        val buffer = Buffer()
        writeTo(buffer)
        return buffer.readByteArray()
    }

    private fun ByteArray.toHexString(): String =
        joinToString(separator = "") { byte ->
            "%02x".format(byte.toInt() and 0xff)
        }

    private companion object {
        val METHODS_WITH_IDEMPOTENCY_KEY = setOf("POST", "PUT", "DELETE")
    }
}
