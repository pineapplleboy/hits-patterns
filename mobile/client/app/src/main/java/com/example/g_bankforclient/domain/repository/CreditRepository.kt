package com.example.g_bankforclient.domain.repository

import com.example.g_bankforclient.common.models.Credit
import kotlinx.coroutines.flow.Flow

interface CreditRepository {
    fun getCredits(): Flow<List<Credit>>
    suspend fun createCredit(name: String, amount: Double, interestRate: Double)
    suspend fun payCredit(creditId: String, accountId: String, amount: Double)
}
