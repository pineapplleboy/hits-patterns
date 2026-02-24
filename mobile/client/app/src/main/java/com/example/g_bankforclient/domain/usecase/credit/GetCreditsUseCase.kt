package com.example.g_bankforclient.domain.usecase.credit

import com.example.g_bankforclient.common.models.Credit
import com.example.g_bankforclient.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCreditsUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    operator fun invoke(): Flow<List<Credit>> = repository.getCredits()
}
