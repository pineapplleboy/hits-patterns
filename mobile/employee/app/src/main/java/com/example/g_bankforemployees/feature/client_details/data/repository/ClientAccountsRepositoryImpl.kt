package com.example.g_bankforemployees.feature.client_details.data.repository

import com.example.g_bankforemployees.common.network.safeApiCall
import com.example.g_bankforemployees.feature.client_details.data.mapper.toDomain
import com.example.g_bankforemployees.feature.client_details.data.remote.ClientAccountsApi
import com.example.g_bankforemployees.feature.client_details.domain.repository.ClientAccountsRepository
import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount

class ClientAccountsRepositoryImpl(
    private val clientAccountsApi: ClientAccountsApi,
) : ClientAccountsRepository {

    override suspend fun getUserBankAccounts(userId: String): Result<List<BankAccount>> =
        safeApiCall(
            apiCall = { clientAccountsApi.getUserBankAccounts(userId) },
            converter = { list -> list.map { it.toDomain() } }
        )

    override suspend fun getUserCreditAccounts(userId: String): Result<List<CreditAccount>> =
        safeApiCall(
            apiCall = { clientAccountsApi.getUserCreditAccounts(userId) },
            converter = { list -> list.map { it.toDomain() } }
        )
}
