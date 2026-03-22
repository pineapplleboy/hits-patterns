package com.example.g_bankforemployees.feature.credit_history.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreditRatingDto(
    val rating: Long,
    val totalCreditCounter: Long,
    val closedCreditCounter: Long,
    val activeCreditAmount: Long,
    val expiredOperationsAmount: Long,
)
