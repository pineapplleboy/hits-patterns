package com.example.g_bankforclient.domain.usecase.currency

import com.example.g_bankforclient.domain.models.Currency
import com.example.g_bankforclient.domain.repository.CurrencyRepository
import javax.inject.Inject

class GetCurrenciesUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {
    suspend operator fun invoke(): List<Currency> = currencyRepository.getAllCurrencies()
}
