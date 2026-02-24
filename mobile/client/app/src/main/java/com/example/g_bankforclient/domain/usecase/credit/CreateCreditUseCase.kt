package com.example.g_bankforclient.domain.usecase.credit

import com.example.g_bankforclient.domain.repository.CreditRepository
import javax.inject.Inject

class CreateCreditUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(name: String, amount: Double, interestRate: Double) = 
        repository.createCredit(name, amount, interestRate)
}
