package com.example.g_bankforemployees.feature.credit_rate.domain.repository

import com.example.g_bankforemployees.feature.credit_rate.domain.model.CreditRateInput
import com.example.g_bankforemployees.feature.credit_rate.domain.model.CreditRate

interface CreditRateRepository {

    suspend fun createCreditRate(input: CreditRateInput): Result<String>

    suspend fun getCreditRates(): Result<List<CreditRate>>
}
