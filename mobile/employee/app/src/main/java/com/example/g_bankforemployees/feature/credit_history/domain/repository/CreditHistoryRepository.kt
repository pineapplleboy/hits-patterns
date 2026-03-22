package com.example.g_bankforemployees.feature.credit_history.domain.repository

import com.example.g_bankforemployees.feature.credit_history.domain.model.CreditRating

interface CreditHistoryRepository {

    suspend fun getUserCreditRating(userId: String): Result<CreditRating>
}
