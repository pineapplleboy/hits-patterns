package com.example.g_bankforemployees.feature.account_operations.domain.usecase

import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation
import com.example.g_bankforemployees.feature.account_operations.domain.repository.AccountOperationsRepository

class GetAccountOperationsUseCase(
    private val accountOperationsRepository: AccountOperationsRepository,
) {

    suspend operator fun invoke(
        userId: String,
        accountNumber: String,
        transferType: String,
    ): Result<List<Operation>> {
        return accountOperationsRepository.getAccountOperations(
            userId = userId,
            accountNumber = accountNumber,
            transferType = transferType,
        )
    }
}
