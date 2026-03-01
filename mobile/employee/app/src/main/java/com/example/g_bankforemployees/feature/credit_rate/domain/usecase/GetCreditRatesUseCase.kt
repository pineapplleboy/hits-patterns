package com.example.g_bankforemployees.feature.credit_rate.domain.usecase

import com.example.g_bankforemployees.feature.credit_rate.domain.model.CreditRate
import com.example.g_bankforemployees.feature.credit_rate.domain.repository.CreditRateRepository

class GetCreditRatesUseCase(
    private val creditRateRepository: CreditRateRepository,
) {

    suspend operator fun invoke(): Result<List<CreditRate>> =
        creditRateRepository.getCreditRates()
}

