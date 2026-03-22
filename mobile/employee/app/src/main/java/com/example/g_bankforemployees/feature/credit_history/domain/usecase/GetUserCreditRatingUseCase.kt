package com.example.g_bankforemployees.feature.credit_history.domain.usecase

import com.example.g_bankforemployees.feature.credit_history.domain.model.CreditRating
import com.example.g_bankforemployees.feature.credit_history.domain.repository.CreditHistoryRepository

class GetUserCreditRatingUseCase(
    private val creditHistoryRepository: CreditHistoryRepository,
) {
    suspend operator fun invoke(userId: String): Result<CreditRating> =
        creditHistoryRepository.getUserCreditRating(userId)
}
