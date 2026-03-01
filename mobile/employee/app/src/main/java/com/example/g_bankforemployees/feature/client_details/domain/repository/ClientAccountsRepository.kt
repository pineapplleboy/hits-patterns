package com.example.g_bankforemployees.feature.client_details.domain.repository

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount

interface ClientAccountsRepository {

    suspend fun getUserBankAccounts(userId: String): Result<List<BankAccount>>

    suspend fun getUserCreditAccounts(userId: String): Result<List<CreditAccount>>
}
