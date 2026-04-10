package com.example.g_bankforclient.data.network

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestMetadataFactory @Inject constructor() {

    fun createTraceOnly(): RequestMetadata {
        return RequestMetadata(traceId = UUID.randomUUID().toString())
    }

    fun createMutation(): RequestMetadata {
        return RequestMetadata(
            traceId = UUID.randomUUID().toString(),
            idempotencyKey = UUID.randomUUID().toString()
        )
    }
}
