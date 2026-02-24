package com.example.g_bankforclient.common.models

import java.util.*

enum class TransactionType {
    DEPOSIT, WITHDRAWAL, CREDIT_TAKEN, CREDIT_PAYMENT
}

data class Transaction(
    val id: String,
    val accountId: String,
    val type: TransactionType,
    val amount: Double,
    val date: Date,
    val description: String
)
