package com.example.g_bankforclient.domain.models

data class Account(
    val id: String,
    val uuid: String = "",
    val name: String,
    var balance: Double,
    val banned: Boolean = false,
    val isHidden: Boolean = false,
    /** Символ валюты счёта (например ₽, $). Если null — по умолчанию отображаем как рубли. */
    val currencySymbol: String? = null,
    /** Код валюты ISO 4217 (RUB, USD, EUR). Используется для конвертации по курсу ЦБ. */
    val currencyCharCode: String? = null,
    /** Готовая строка баланса с бэкенда (число + символ валюты). Если задана — показываем как есть, без преобразований. */
    val balanceDisplay: String? = null
)
