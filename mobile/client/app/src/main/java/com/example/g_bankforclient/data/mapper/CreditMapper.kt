package com.example.g_bankforclient.data.mapper

import com.example.g_bankforclient.data.network.model.CreditAccountFullModel
import com.example.g_bankforclient.data.network.model.CreditAccountShortModel
import com.example.g_bankforclient.domain.models.Credit
import com.example.g_bankforclient.domain.models.Transaction

fun CreditAccountShortModel.toDomain(transactions: List<Transaction> = emptyList()): Credit {
    val initialAmount = transactions
        .filter { it.type == com.example.g_bankforclient.domain.models.TransactionType.CREDIT_TAKEN }
        .sumOf { it.amount }
        .takeIf { it > 0.0 } ?: dept

    return Credit(
        id = accountNumber,
        name = creditRateName,
        amount = initialAmount,
        debt = dept,
        interestRate = creditRatePercent.toDouble(),
        writeOffPeriod = writeOffPeriod,
        nextWriteOffDate = nextWriteOffDate,
        transactions = transactions
    )
}

fun CreditAccountFullModel.toDomain(transactions: List<Transaction> = emptyList()): Credit {
    val initialAmount = transactions
        .filter { it.type == com.example.g_bankforclient.domain.models.TransactionType.CREDIT_TAKEN }
        .sumOf { it.amount }
        .takeIf { it > 0.0 } ?: dept

    return Credit(
        id = accountNumber,
        name = creditRateName,
        amount = initialAmount,
        debt = dept,
        interestRate = creditRatePercent.toDouble(),
        writeOffPeriod = writeOffPeriod,
        nextWriteOffDate = nextWriteOffDate,
        transactions = transactions
    )
}

