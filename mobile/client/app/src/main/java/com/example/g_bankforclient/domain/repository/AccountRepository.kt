package com.example.g_bankforclient.domain.repository

import com.example.g_bankforclient.common.models.Account
import com.example.g_bankforclient.common.models.Transaction
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccounts(): Flow<List<Account>>
    suspend fun createAccount(name: String)
    suspend fun closeAccount(accountId: String)
    suspend fun deposit(accountId: String, amount: Double)
    suspend fun withdrawal(accountId: String, amount: Double)
    fun getTransactions(): Flow<List<Transaction>>
}
