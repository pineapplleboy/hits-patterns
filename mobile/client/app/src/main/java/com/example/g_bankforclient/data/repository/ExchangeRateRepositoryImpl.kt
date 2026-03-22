package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.data.network.ExchangeRateService
import com.example.g_bankforclient.domain.models.ExchangeRate
import com.example.g_bankforclient.domain.repository.ExchangeRateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRateRepositoryImpl @Inject constructor(
    private val exchangeRateService: ExchangeRateService
) : ExchangeRateRepository {

    private var cachedRates: Map<String, ExchangeRate>? = null
    private var cacheTimestamp: Long = 0L
    private val cacheTtlMs = 60 * 60 * 1000L // 1 hour

    override suspend fun getRates(): Map<String, ExchangeRate> {
        val now = System.currentTimeMillis()
        val cached = cachedRates
        if (cached != null && now - cacheTimestamp < cacheTtlMs) return cached

        val response = exchangeRateService.getDailyRates()
        val rates = response.valute.mapValues { (_, rate) ->
            ExchangeRate(
                charCode = rate.charCode,
                name = rate.name,
                nominal = rate.nominal,
                valueRub = rate.value
            )
        }
        cachedRates = rates
        cacheTimestamp = now
        return rates
    }
}
