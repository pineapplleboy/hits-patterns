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

private fun extractCurrencySymbol(balanceStr: String): String? {
    val symbol =
        balanceStr.filter { !it.isDigit() && it != '.' && it != '-' && it != ' ' && it != ',' }
            .trim()
    return symbol.ifEmpty { null }
}

private fun symbolToCharCode(symbol: String?): String? = when (symbol) {
    "₽", "руб", "р" -> "RUB"
    "$" -> "USD"
    "€" -> "EUR"
    "£" -> "GBP"
    "¥" -> "CNY"
    else -> null
}

fun BankAccountShortModel.toDomain(): Account {
    val symbol = extractCurrencySymbol(balance)
    return Account(
        id = accountNumber,
        uuid = id.toString(),
        name = "Счет $accountNumber",
        balance = parseMoney(balance),
        banned = banned,
        isHidden = hidden,
        currencySymbol = symbol,
        currencyCharCode = symbolToCharCode(symbol),
        balanceDisplay = balance
    )
}

fun BankAccountFullModel.toDomain(): Account = Account(
    id = accountNumber,
    uuid = id.toString(),
    name = "Счет $accountNumber",
    balance = parseMoney(balance),
    banned = banned,
    isHidden = hidden,
    currencySymbol = currency?.symbol,
    currencyCharCode = currency?.charCode,
    balanceDisplay = null
)
