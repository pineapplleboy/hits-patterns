package com.example.g_bankforemployees.feature.account_operations.domain.repository

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation

interface AccountOperationsRepository {

    suspend fun getBankAccount(userId: String, accountNumber: String): Result<BankAccount>

    suspend fun getCreditAccount(userId: String, accountNumber: String): Result<CreditAccount>

    suspend fun getAccountOperations(
        userId: String,
        accountNumber: String,
        transferType: String,
    ): Result<List<Operation>>
}
