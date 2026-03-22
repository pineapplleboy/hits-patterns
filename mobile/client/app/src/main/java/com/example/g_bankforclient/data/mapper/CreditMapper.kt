package com.example.g_bankforclient.data.mapper

import com.example.g_bankforclient.data.network.model.CreditAccountFullModel
import com.example.g_bankforclient.data.network.model.CreditAccountShortModel
import com.example.g_bankforclient.domain.models.Credit
import com.example.g_bankforclient.domain.models.Transaction

/** Парсим строку вида "50515.61₽" → Double */
private fun parseDebt(raw: String): Double {
    val normalized = raw.replace(',', '.')
    val cleaned = normalized.replace(Regex("[^0-9.\\-]"), "")
    return cleaned.toDoubleOrNull() ?: 0.0
}

fun CreditAccountShortModel.toDomain(transactions: List<Transaction> = emptyList()): Credit {
    val debtValue = parseDebt(dept)
    val initialAmount = transactions
        .filter { it.type == com.example.g_bankforclient.domain.models.TransactionType.CREDIT_TAKEN }
        .sumOf { it.amount }
        .takeIf { it > 0.0 } ?: debtValue

    return Credit(
        id = accountNumber,
        name = creditRateName,
        amount = initialAmount,
        debt = debtValue,
        interestRate = creditRatePercent.toDouble(),
        writeOffPeriod = writeOffPeriod,
        nextWriteOffDate = nextWriteOffDate,
        transactions = transactions
    )
}

fun CreditAccountFullModel.toDomain(transactions: List<Transaction> = emptyList()): Credit {
    val debtValue = parseDebt(dept)
    val initialAmount = transactions
        .filter { it.type == com.example.g_bankforclient.domain.models.TransactionType.CREDIT_TAKEN }
        .sumOf { it.amount }
        .takeIf { it > 0.0 } ?: debtValue

    return Credit(
        id = accountNumber,
        name = creditRateName,
        amount = initialAmount,
        debt = debtValue,
        interestRate = creditRatePercent.toDouble(),
        writeOffPeriod = writeOffPeriod,
        nextWriteOffDate = nextWriteOffDate,
        transactions = transactions
    )
}

