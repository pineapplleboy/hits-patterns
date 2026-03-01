package com.example.g_bankforclient.domain.usecase.credit

import com.example.g_bankforclient.domain.repository.CreditRepository
import javax.inject.Inject

class PayCreditUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(creditId: String, accountId: String, amount: Double) = 
        repository.payCredit(creditId, accountId, amount)
}
