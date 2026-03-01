package com.example.g_bankforemployees.feature.account_operations.domain.usecase

import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.feature.account_operations.domain.repository.AccountOperationsRepository

class GetCreditAccountDetailsUseCase(
    private val accountOperationsRepository: AccountOperationsRepository,
) {

    suspend operator fun invoke(userId: String, accountNumber: String): Result<CreditAccount> =
        accountOperationsRepository.getCreditAccount(userId, accountNumber)
}
