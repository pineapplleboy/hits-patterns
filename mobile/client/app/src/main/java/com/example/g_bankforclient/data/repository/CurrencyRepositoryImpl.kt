package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.data.network.CurrencyService
import com.example.g_bankforclient.domain.models.Currency
import com.example.g_bankforclient.domain.repository.CurrencyRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val currencyService: CurrencyService
) : CurrencyRepository {

    override suspend fun getAllCurrencies(): List<Currency> {
        return currencyService.getAllCurrencies().map { model ->
            Currency(
                id = model.id,
                name = model.name,
                charCode = model.charCode,
                symbol = model.symbol,
                rate = model.rate
            )
        }
    }
}
