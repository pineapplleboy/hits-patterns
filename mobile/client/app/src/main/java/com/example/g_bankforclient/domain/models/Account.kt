package com.example.g_bankforclient.domain.models

data class Account(
    val id: String,
    val name: String,
    var balance: Double,
    val banned: Boolean = false,
    /** Символ валюты счёта (например ₽, $). Если null — по умолчанию отображаем как рубли. */
    val currencySymbol: String? = null,
    /** Готовая строка баланса с бэкенда (число + символ валюты). Если задана — показываем как есть, без преобразований. */
    val balanceDisplay: String? = null
)

