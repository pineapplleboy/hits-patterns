package com.example.g_bankforemployees.feature.account_operations.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreditAccountFullDto(
    val id: String,
    val accountNumber: String,
    val dept: Double,
    val creditRateName: String,
    val creditRatePercent: Int,
    val writeOffPeriod: String,
    val nextWriteOffDate: String,
    val banned: Boolean = false,
    val operations: List<OperationDto> = emptyList(),
)
