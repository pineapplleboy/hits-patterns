package com.example.g_bankforemployees.feature.account_operations.domain.repository

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation
import kotlinx.coroutines.flow.Flow

interface AccountOperationsRepository {

    suspend fun observeBankAccount(
        userId: String,
        accountNumber: String,
    ): Result<Flow<BankAccount>>

    suspend fun observeCreditAccount(
        userId: String,
        accountNumber: String,
    ): Result<Flow<CreditAccount>>

    suspend fun observeAccountOperations(
        userId: String,
        accountNumber: String,
        transferType: String,
    ): Result<Flow<List<Operation>>>

    suspend fun getExpiredOperations(userId: String): Result<List<Operation>>
}
