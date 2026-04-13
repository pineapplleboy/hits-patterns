package com.example.g_bankforclient.data.network

data class RequestMetadata(
    val traceId: String,
    val idempotencyKey: String? = null,
)
