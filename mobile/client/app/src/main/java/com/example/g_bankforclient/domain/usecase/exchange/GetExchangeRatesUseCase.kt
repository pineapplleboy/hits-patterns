package com.example.g_bankforclient.domain.usecase.exchange

import com.example.g_bankforclient.domain.models.ExchangeRate
import com.example.g_bankforclient.domain.repository.ExchangeRateRepository
import javax.inject.Inject

class GetExchangeRatesUseCase @Inject constructor(
    private val repository: ExchangeRateRepository
) {
    suspend operator fun invoke(): Map<String, ExchangeRate> = repository.getRates()
}
