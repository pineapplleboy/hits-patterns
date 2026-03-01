package com.example.g_bankforclient.domain.usecase.account

import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.domain.repository.AccountRepository
import javax.inject.Inject

class GetAccountDetailsUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(accountNumber: String): Account =
        repository.getAccountDetails(accountNumber)
}

