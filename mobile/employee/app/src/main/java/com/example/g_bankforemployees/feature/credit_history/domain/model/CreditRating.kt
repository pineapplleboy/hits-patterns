package com.example.g_bankforemployees.feature.credit_history.domain.model

data class CreditRating(
    val rating: Long,
    val totalCreditCounter: Long,
    val closedCreditCounter: Long,
    val activeCreditAmount: Long,
    val expiredOperationsAmount: Long,
)
