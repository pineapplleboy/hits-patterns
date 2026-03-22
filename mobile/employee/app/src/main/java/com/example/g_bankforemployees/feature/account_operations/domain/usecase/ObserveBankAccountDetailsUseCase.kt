package com.example.g_bankforemployees.feature.account_operations.domain.usecase

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.feature.account_operations.domain.repository.AccountOperationsRepository
import kotlinx.coroutines.flow.Flow

class ObserveBankAccountDetailsUseCase(
    private val accountOperationsRepository: AccountOperationsRepository,
) {

    suspend operator fun invoke(
        userId: String,
        accountNumber: String,
    ): Result<Flow<BankAccount>> =
        accountOperationsRepository.observeBankAccount(userId, accountNumber)
}
