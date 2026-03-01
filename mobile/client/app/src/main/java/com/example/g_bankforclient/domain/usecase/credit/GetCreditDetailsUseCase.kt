package com.example.g_bankforclient.domain.usecase.credit

import com.example.g_bankforclient.domain.models.Credit
import com.example.g_bankforclient.domain.repository.CreditRepository
import javax.inject.Inject

class GetCreditDetailsUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(accountNumber: String): Credit =
        repository.getCreditDetails(accountNumber)
}

