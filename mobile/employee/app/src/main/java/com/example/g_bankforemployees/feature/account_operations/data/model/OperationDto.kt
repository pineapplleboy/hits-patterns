package com.example.g_bankforemployees.feature.account_operations.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OperationDto(
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
