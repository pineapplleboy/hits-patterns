package com.example.g_bankforemployees.feature.account_operations.domain.usecase

import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation
import com.example.g_bankforemployees.feature.account_operations.domain.repository.AccountOperationsRepository
import kotlinx.coroutines.flow.Flow

class ObserveAccountOperationsUseCase(
    private val accountOperationsRepository: AccountOperationsRepository,
) {

    suspend operator fun invoke(
        userId: String,
        accountNumber: String,
        transferType: String,
    ): Result<Flow<List<Operation>>> {
        return accountOperationsRepository.observeAccountOperations(
            userId = userId,
            accountNumber = accountNumber,
            transferType = transferType,
        )
    }
}
