package com.example.g_bankforclient.domain.usecase.credit

import com.example.g_bankforclient.domain.repository.CreditRepository
import java.util.UUID
import javax.inject.Inject

class TakeCreditUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(userId: UUID, rateId: UUID, sum: Double, bankAccountNum: String): Boolean =
        repository.takeCredit(userId, rateId, sum, bankAccountNum)
}
