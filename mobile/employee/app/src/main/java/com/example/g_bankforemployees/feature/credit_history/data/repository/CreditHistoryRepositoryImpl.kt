package com.example.g_bankforemployees.feature.credit_history.data.repository

import com.example.g_bankforemployees.common.network.safeApiCall
import com.example.g_bankforemployees.feature.credit_history.data.mapper.toDomain
import com.example.g_bankforemployees.feature.credit_history.data.remote.CreditHistoryApi
import com.example.g_bankforemployees.feature.credit_history.domain.model.CreditRating
import com.example.g_bankforemployees.feature.credit_history.domain.repository.CreditHistoryRepository

class CreditHistoryRepositoryImpl(
    private val creditHistoryApi: CreditHistoryApi,
) : CreditHistoryRepository {

    override suspend fun getUserCreditRating(userId: String): Result<CreditRating> =
        safeApiCall(
            apiCall = { creditHistoryApi.getUserCreditRating(userId) },
            converter = { it.toDomain() },
        )
}
