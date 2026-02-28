package com.example.g_bankforclient.domain.repository

import com.example.g_bankforclient.common.models.Credit
import com.example.g_bankforclient.common.models.CreditRate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface CreditRepository {
    fun getCredits(): Flow<List<Credit>>
    suspend fun createCredit(name: String, amount: Double, interestRate: Double)
    suspend fun payCredit(creditId: String, accountId: String, amount: Double)
    
    // Credit rate methods
    suspend fun getAvailableCreditRates(): List<CreditRate>
    suspend fun getAvailableCreditRateById(id: UUID): CreditRate
    suspend fun takeCredit(userId: UUID, rateId: UUID, sum: Double, bankAccountNum: String): Boolean
    suspend fun createCreditRate(creditRateData: CreditRate): UUID?
    suspend fun updateCreditRate(id: UUID, creditRateData: CreditRate): Boolean
    suspend fun deactivateCreditRate(id: UUID): Boolean
}
