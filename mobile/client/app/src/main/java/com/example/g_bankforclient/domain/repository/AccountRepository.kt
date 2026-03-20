package com.example.g_bankforclient.domain.repository

import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.domain.models.Transaction

interface AccountRepository {
    suspend fun getAccounts(): List<Account>
    suspend fun getAccountDetails(accountNumber: String): Account
    suspend fun createAccount(currencyId: Int)
    suspend fun closeAccount(accountId: String)
    suspend fun deposit(accountId: String, amount: Double)
    suspend fun withdrawal(accountId: String, amount: Double)
    suspend fun getTransactions(): List<Transaction>
    suspend fun getAccountTransactions(accountNumber: String): List<Transaction>
}
