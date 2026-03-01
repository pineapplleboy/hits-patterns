package com.example.g_bankforclient.domain.usecase.account

import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.domain.repository.AccountRepository
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(): List<Account> = repository.getAccounts()
}
