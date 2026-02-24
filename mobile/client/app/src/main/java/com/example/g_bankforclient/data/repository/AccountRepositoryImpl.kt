package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.common.models.Account
import com.example.g_bankforclient.common.models.Transaction
import com.example.g_bankforclient.common.models.TransactionType
import com.example.g_bankforclient.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor() : AccountRepository {
    
    private val _accounts = MutableStateFlow(
        listOf(
            Account("1", "Основной счет", 125000.0),
            Account("2", "Накопления", 450000.0)
        )
    )
    
    private val _transactions = MutableStateFlow(
        listOf(
            Transaction("1", "1", TransactionType.DEPOSIT, 50000.0, Date(), "Зачисление зарплаты"),
            Transaction("2", "1", TransactionType.WITHDRAWAL, 3500.0, Date(System.currentTimeMillis() - 86400000), "Снятие наличных"),
            Transaction("3", "1", TransactionType.CREDIT_PAYMENT, 25000.0, Date(System.currentTimeMillis() - 172800000), "Платеж по ипотеке"),
            Transaction("4", "2", TransactionType.DEPOSIT, 100000.0, Date(System.currentTimeMillis() - 518400000), "Пополнение накоплений")
        )
    )

    override fun getAccounts(): Flow<List<Account>> = _accounts.asStateFlow()

    override suspend fun createAccount(name: String) {
        _accounts.value = _accounts.value + Account(
            id = System.currentTimeMillis().toString(),
            name = name,
            balance = 0.0
        )
    }

    override suspend fun closeAccount(accountId: String) {
        _accounts.value = _accounts.value.filter { it.id != accountId }
    }

    override suspend fun deposit(accountId: String, amount: Double) {
        _accounts.value = _accounts.value.map { account ->
            if (account.id == accountId) {
                account.copy(balance = account.balance + amount)
            } else {
                account
            }
        }

        _transactions.value = listOf(
            Transaction(
                id = System.currentTimeMillis().toString(),
                accountId = accountId,
                type = TransactionType.DEPOSIT,
                amount = amount,
                date = Date(),
                description = "Пополнение счета"
            )
        ) + _transactions.value
    }

    override suspend fun withdrawal(accountId: String, amount: Double) {
        val account = _accounts.value.find { it.id == accountId }
        if (account != null && account.balance >= amount) {
            _accounts.value = _accounts.value.map { acc ->
                if (acc.id == accountId) {
                    acc.copy(balance = acc.balance - amount)
                } else {
                    acc
                }
            }

            _transactions.value = listOf(
                Transaction(
                    id = System.currentTimeMillis().toString(),
                    accountId = accountId,
                    type = TransactionType.WITHDRAWAL,
                    amount = amount,
                    date = Date(),
                    description = "Снятие наличных"
                )
            ) + _transactions.value
        }
    }

    override fun getTransactions(): Flow<List<Transaction>> = _transactions.asStateFlow()
}
