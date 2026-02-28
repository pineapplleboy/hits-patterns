package com.example.g_bankforclient.domain.usecase.credit

import com.example.g_bankforclient.domain.repository.CreditRepository
import java.util.UUID
import javax.inject.Inject

class DeactivateCreditRateUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(id: UUID): Boolean =
        repository.deactivateCreditRate(id)
}
