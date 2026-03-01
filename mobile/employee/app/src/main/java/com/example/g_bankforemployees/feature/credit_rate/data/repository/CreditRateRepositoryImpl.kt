package com.example.g_bankforemployees.feature.credit_rate.data.repository

import com.example.g_bankforemployees.common.network.safeApiCall
import com.example.g_bankforemployees.feature.credit_rate.data.mapper.toDto
import com.example.g_bankforemployees.feature.credit_rate.data.mapper.toDomain
import com.example.g_bankforemployees.feature.credit_rate.data.remote.CreditApi
import com.example.g_bankforemployees.feature.credit_rate.domain.model.CreditRateInput
import com.example.g_bankforemployees.feature.credit_rate.domain.model.CreditRate
import com.example.g_bankforemployees.feature.credit_rate.domain.repository.CreditRateRepository

class CreditRateRepositoryImpl(
    private val creditApi: CreditApi,
) : CreditRateRepository {

    override suspend fun createCreditRate(input: CreditRateInput): Result<String> =
        safeApiCall(
            apiCall = { creditApi.createCreditRate(input.toDto()) },
            converter = { it.id }
        )

    override suspend fun getCreditRates(): Result<List<CreditRate>> =
        safeApiCall(
            apiCall = { creditApi.getCreditRates() },
            converter = { list -> list.map { it.toDomain() } }
        )
}
