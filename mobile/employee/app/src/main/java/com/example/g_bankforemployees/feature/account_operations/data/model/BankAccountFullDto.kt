package com.example.g_bankforemployees.feature.account_operations.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BankAccountFullDto(
    val id: String,
    val accountNumber: String,
    val balance: Double,
    val banned: Boolean = false,
    val operations: List<OperationDto> = emptyList(),
    val createTime: String? = null,
)
