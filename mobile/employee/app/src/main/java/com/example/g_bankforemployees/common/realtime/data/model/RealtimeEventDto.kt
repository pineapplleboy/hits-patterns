package com.example.g_bankforemployees.common.realtime.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OperationRealtimeBodyDto(
    val operationId: String,
    val accountNumberFrom: String? = null,
    val userIdFrom: String? = null,
    val recipientAccountNumber: String? = null,
    val amount: String,
    val transferAccountType: String,
    val actionType: String,
    val status: String,
    val createTime: String,
)

@Serializable
data class OperationStatusRealtimeBodyDto(
    val operationId: String,
    val newStatus: String,
)

@Serializable
data class BalanceRealtimeBodyDto(
    val balance: String,
    val accountId: String? = null,
    val accountNumber: String? = null,
)
