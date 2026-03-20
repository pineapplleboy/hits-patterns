package com.example.g_bankforclient.data.mapper

import com.example.g_bankforclient.data.network.model.BankAccountFullModel
import com.example.g_bankforclient.data.network.model.BankAccountShortModel
import com.example.g_bankforclient.domain.models.Account

private fun parseMoney(raw: String): Double {
    val normalized = raw
        .trim()
        .replace(" ", "")
        .replace(',', '.')

    // Keep only number-ish characters to handle values like "86.15₽" or "86.15Р".
    val cleaned = normalized.filter { it.isDigit() || it == '.' || it == '-' }
    return cleaned.toDoubleOrNull() ?: 0.0
}

fun BankAccountShortModel.toDomain(): Account = Account(
    id = accountNumber,
    name = "Счет $accountNumber",
    balance = parseMoney(balance),
    banned = banned,
    currencySymbol = null,
    balanceDisplay = balance
)

fun BankAccountFullModel.toDomain(): Account = Account(
    id = accountNumber,
    name = "Счет $accountNumber",
    balance = parseMoney(balance),
    banned = banned,
    currencySymbol = currency?.symbol,
    balanceDisplay = null
)

