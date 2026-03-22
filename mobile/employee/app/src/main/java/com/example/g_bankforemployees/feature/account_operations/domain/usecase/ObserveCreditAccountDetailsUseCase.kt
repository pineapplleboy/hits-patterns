package com.example.g_bankforemployees.feature.account_operations.domain.usecase

import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.feature.account_operations.domain.repository.AccountOperationsRepository
import kotlinx.coroutines.flow.Flow

class ObserveCreditAccountDetailsUseCase(
    private val accountOperationsRepository: AccountOperationsRepository,
) {

    suspend operator fun invoke(
        userId: String,
        accountNumber: String,
    ): Result<Flow<CreditAccount>> =
        accountOperationsRepository.observeCreditAccount(userId, accountNumber)
}
