package com.example.g_bankforclient.domain.usecase.credit

import com.example.g_bankforclient.common.models.CreditRate
import com.example.g_bankforclient.domain.repository.CreditRepository
import java.util.UUID
import javax.inject.Inject

class CreateCreditRateUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(creditRateData: CreditRate): UUID? =
        repository.createCreditRate(creditRateData)
}
