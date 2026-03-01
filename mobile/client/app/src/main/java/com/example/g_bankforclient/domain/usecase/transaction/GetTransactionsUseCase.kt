package com.example.g_bankforclient.domain.usecase.transaction

import com.example.g_bankforclient.domain.models.Transaction
import com.example.g_bankforclient.domain.repository.AccountRepository
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(): List<Transaction> = repository.getTransactions()
}
