package com.example.g_bankforclient.domain.usecase.account

import com.example.g_bankforclient.domain.repository.AccountRepository
import javax.inject.Inject

class CloseAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(accountId: String) = repository.closeAccount(accountId)
}
