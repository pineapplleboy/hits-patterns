package com.example.g_bankforclient.domain.repository

import com.example.g_bankforclient.domain.models.Currency

interface CurrencyRepository {
    suspend fun getAllCurrencies(): List<Currency>
}
