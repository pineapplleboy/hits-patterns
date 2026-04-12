package com.example.g_bankforclient.domain.repository

import com.example.g_bankforclient.domain.models.ExchangeRate

interface ExchangeRateRepository {
    suspend fun getRates(): Map<String, ExchangeRate>
}
