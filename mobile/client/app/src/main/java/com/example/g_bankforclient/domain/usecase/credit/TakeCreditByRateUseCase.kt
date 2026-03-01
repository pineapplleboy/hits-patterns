package com.example.g_bankforclient.domain.usecase.credit

import com.example.g_bankforclient.domain.TokenStorage
import com.example.g_bankforclient.domain.repository.CreditRepository
import java.util.UUID
import javax.inject.Inject

class TakeCreditByRateUseCase @Inject constructor(
    private val repository: CreditRepository,
    private val tokenStorage: TokenStorage
) {
    suspend operator fun invoke(rateId: UUID, sum: Double, bankAccountNum: String): Boolean {
        val userId = tokenStorage.getUserId()?.let { UUID.fromString(it) }
            ?: error("Пользователь не авторизован")
        return repository.takeCredit(userId, rateId, sum, bankAccountNum)
    }
}
