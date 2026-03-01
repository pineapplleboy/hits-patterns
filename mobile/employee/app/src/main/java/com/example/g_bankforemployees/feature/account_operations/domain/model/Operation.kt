package com.example.g_bankforemployees.feature.account_operations.domain.model

data class Operation(
    val operationId: String,
    val accountNumberFrom: String?,
    val userIdFrom: String?,
    val recipientAccountNumber: String?,
    val recipientName: String?,
    val amount: Double,
    val transferAccountType: String,
    val actionType: String,
    val status: String,
    val createTime: String,
)
