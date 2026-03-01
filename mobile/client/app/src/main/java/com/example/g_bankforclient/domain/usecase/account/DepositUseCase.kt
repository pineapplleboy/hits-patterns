package com.example.g_bankforclient.domain.usecase.account

import com.example.g_bankforclient.domain.repository.AccountRepository
import javax.inject.Inject

class DepositUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(accountId: String, amount: Double) = repository.deposit(accountId, amount)
}
