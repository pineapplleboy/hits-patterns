package com.example.g_bankforclient.domain.models

import java.util.Date

enum class TransactionType {
    DEPOSIT, WITHDRAWAL, CREDIT_TAKEN, CREDIT_PAYMENT, INFO
}

enum class TransactionStatus {
    CREATED, IN_PROCESS, SUCCESS, REJECTED
}

data class Transaction(
    val id: String,
    val accountId: String,
    val type: TransactionType,
    val amount: Double,
    val date: Date,
    val description: String,
    val status: TransactionStatus = TransactionStatus.SUCCESS,
    val fromAccount: String? = null,
    val toAccount: String? = null
)

