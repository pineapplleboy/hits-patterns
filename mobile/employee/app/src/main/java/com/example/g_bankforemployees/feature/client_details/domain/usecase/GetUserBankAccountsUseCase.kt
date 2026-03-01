package com.example.g_bankforemployees.feature.client_details.domain.usecase

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.feature.client_details.domain.repository.ClientAccountsRepository

class GetUserBankAccountsUseCase(
    private val clientAccountsRepository: ClientAccountsRepository,
) {

    suspend operator fun invoke(userId: String): Result<List<BankAccount>> =
        clientAccountsRepository.getUserBankAccounts(userId)
}
