package com.example.g_bankforemployees.feature.credit_rate.domain.usecase

import com.example.g_bankforemployees.feature.credit_rate.domain.model.CreditRateInput
import com.example.g_bankforemployees.feature.credit_rate.domain.repository.CreditRateRepository

class CreateCreditRateUseCase(
    private val creditRateRepository: CreditRateRepository,
) {

    suspend operator fun invoke(input: CreditRateInput) =
        creditRateRepository.createCreditRate(input)
}
