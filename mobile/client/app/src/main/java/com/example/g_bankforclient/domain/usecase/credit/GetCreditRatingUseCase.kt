package com.example.g_bankforclient.domain.usecase.credit

import com.example.g_bankforclient.domain.models.CreditRating
import com.example.g_bankforclient.domain.repository.CreditRepository
import javax.inject.Inject

class GetCreditRatingUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(): CreditRating = repository.getCreditRating()
}
