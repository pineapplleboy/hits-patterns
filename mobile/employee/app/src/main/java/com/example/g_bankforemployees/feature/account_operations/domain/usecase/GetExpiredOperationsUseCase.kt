package com.example.g_bankforemployees.feature.account_operations.domain.usecase

import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation
import com.example.g_bankforemployees.feature.account_operations.domain.repository.AccountOperationsRepository

class GetExpiredOperationsUseCase(
    private val accountOperationsRepository: AccountOperationsRepository,
) {
    suspend operator fun invoke(userId: String): Result<List<Operation>> =
        accountOperationsRepository.getExpiredOperations(userId)
}
