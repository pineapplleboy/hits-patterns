package com.example.g_bankforclient.domain.usecase.credit

import com.example.g_bankforclient.domain.models.CreditRate
import com.example.g_bankforclient.domain.repository.CreditRepository
import javax.inject.Inject

class GetCreditRatesUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(): List<CreditRate> = repository.getAvailableCreditRates()
}
