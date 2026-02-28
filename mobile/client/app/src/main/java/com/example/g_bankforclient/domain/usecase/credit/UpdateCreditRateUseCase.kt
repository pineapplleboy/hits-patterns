package com.example.g_bankforclient.domain.usecase.credit

import com.example.g_bankforclient.common.models.CreditRate
import com.example.g_bankforclient.domain.repository.CreditRepository
import java.util.UUID
import javax.inject.Inject

class UpdateCreditRateUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(id: UUID, creditRateData: CreditRate): Boolean =
        repository.updateCreditRate(id, creditRateData)
}
