package com.example.g_bankforemployees.feature.account_operations.domain.usecase

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.feature.account_operations.domain.repository.AccountOperationsRepository

class GetBankAccountDetailsUseCase(
    private val accountOperationsRepository: AccountOperationsRepository,
) {

    suspend operator fun invoke(userId: String, accountNumber: String): Result<BankAccount> =
        accountOperationsRepository.getBankAccount(userId, accountNumber)
}
