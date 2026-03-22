package com.example.g_bankforclient.domain.usecase.account

import com.example.g_bankforclient.domain.models.Transaction
import com.example.g_bankforclient.domain.repository.AccountRepository
import javax.inject.Inject

class GetMissedPaymentsUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(): List<Transaction> = repository.getMissedPayments()
}
