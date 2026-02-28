package com.example.g_bankforclient.domain.usecase.credit

import com.example.g_bankforclient.domain.repository.CreditRepository
import java.util.UUID
import javax.inject.Inject

class TakeCreditByRateUseCase @Inject constructor(
    private val repository: CreditRepository,
    private val takeCreditUseCase: TakeCreditUseCase
) {
    suspend operator fun invoke(rateId: UUID, sum: Double, bankAccountNum: String): Boolean {
        // In a real app, we would get the current user ID from the auth repository
        val userId = UUID.randomUUID() // TODO: Replace with actual user ID
        return takeCreditUseCase(userId, rateId, sum, bankAccountNum)
    }
}
