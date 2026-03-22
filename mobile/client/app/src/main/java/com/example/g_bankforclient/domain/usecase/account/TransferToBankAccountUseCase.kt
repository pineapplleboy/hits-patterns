package com.example.g_bankforclient.domain.usecase.account

import com.example.g_bankforclient.domain.repository.AccountRepository
import javax.inject.Inject

class TransferToBankAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(fromAccount: String, toAccount: String, amount: Double) =
        repository.transferToBankAccount(fromAccount, toAccount, amount)
}
