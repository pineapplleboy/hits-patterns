package com.example.g_bankforemployees.feature.account_operations.data.repository

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.common.network.safeApiCall
import com.example.g_bankforemployees.feature.account_operations.data.mapper.toDomain
import com.example.g_bankforemployees.feature.account_operations.data.remote.AccountOperationsApi
import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation
import com.example.g_bankforemployees.feature.account_operations.domain.repository.AccountOperationsRepository

private const val TRANSFER_TYPE_BANK_ACCOUNT = "BANK_ACCOUNT"

class AccountOperationsRepositoryImpl(
    private val accountOperationsApi: AccountOperationsApi,
) : AccountOperationsRepository {

    override suspend fun getBankAccount(userId: String, accountNumber: String): Result<BankAccount> =
        safeApiCall(
            apiCall = { accountOperationsApi.getBankAccount(userId, accountNumber) },
            converter = { it.toDomain() }
        )

    override suspend fun getCreditAccount(userId: String, accountNumber: String): Result<CreditAccount> =
        safeApiCall(
            apiCall = { accountOperationsApi.getCreditAccount(userId, accountNumber) },
            converter = { it.toDomain() }
        )

    override suspend fun getAccountOperations(
        userId: String,
        accountNumber: String,
        transferType: String,
    ): Result<List<Operation>> {
        val type = transferType.ifBlank { TRANSFER_TYPE_BANK_ACCOUNT }
        return safeApiCall(
            apiCall = { accountOperationsApi.getAccountOperations(userId, accountNumber, type) },
            converter = { list -> list.map { it.toDomain() } }
        )
    }
}
