package com.example.g_bankforclient.domain.usecase.transaction

import com.example.g_bankforclient.common.models.Transaction
import com.example.g_bankforclient.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    operator fun invoke(): Flow<List<Transaction>> = repository.getTransactions()
}
