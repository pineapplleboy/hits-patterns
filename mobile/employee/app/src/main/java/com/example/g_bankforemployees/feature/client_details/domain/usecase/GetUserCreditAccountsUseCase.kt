package com.example.g_bankforemployees.feature.client_details.domain.usecase

import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.feature.client_details.domain.repository.ClientAccountsRepository

class GetUserCreditAccountsUseCase(
    private val clientAccountsRepository: ClientAccountsRepository,
) {

    suspend operator fun invoke(userId: String): Result<List<CreditAccount>> =
        clientAccountsRepository.getUserCreditAccounts(userId)
}
