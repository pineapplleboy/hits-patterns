package com.example.g_bankforclient.domain.usecase.credit

import com.example.g_bankforclient.domain.models.Credit
import com.example.g_bankforclient.domain.repository.CreditRepository
import javax.inject.Inject

class GetCreditsUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(): List<Credit> = repository.getCredits()
}
