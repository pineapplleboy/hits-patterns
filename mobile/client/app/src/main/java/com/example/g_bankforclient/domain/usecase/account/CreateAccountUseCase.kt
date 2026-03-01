package com.example.g_bankforclient.domain.usecase.account

import com.example.g_bankforclient.domain.repository.AccountRepository
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(initialAmount: Double) = repository.createAccount(initialAmount)
}
