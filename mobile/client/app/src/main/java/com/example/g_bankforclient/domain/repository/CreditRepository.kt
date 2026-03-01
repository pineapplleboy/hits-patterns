package com.example.g_bankforclient.domain.repository

import com.example.g_bankforclient.domain.models.Credit
import com.example.g_bankforclient.domain.models.CreditRate
import com.example.g_bankforclient.domain.models.Transaction
import java.util.UUID

interface CreditRepository {
    suspend fun getCredits(): List<Credit>
    suspend fun getCreditDetails(accountNumber: String): Credit
    suspend fun getCreditTransactions(accountNumber: String): List<Transaction>
    suspend fun payCredit(creditId: String, accountId: String, amount: Double)
    
    suspend fun getAvailableCreditRates(): List<CreditRate>
    suspend fun getAvailableCreditRateById(id: UUID): CreditRate
    suspend fun takeCredit(userId: UUID, rateId: UUID, sum: Double, bankAccountNum: String): Boolean
}
