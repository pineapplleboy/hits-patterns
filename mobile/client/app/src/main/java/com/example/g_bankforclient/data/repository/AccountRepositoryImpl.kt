package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.data.mapper.toDomain
import com.example.g_bankforclient.data.network.AccountService
import com.example.g_bankforclient.data.network.model.MoneyAmountRequestModel
import com.example.g_bankforclient.data.network.model.TransferAccountType
import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.domain.models.Transaction
import com.example.g_bankforclient.domain.repository.AccountRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import com.example.g_bankforclient.data.mapper.toDomain as toAccountDomain

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountService: AccountService,
    private val tokenStorage: com.example.g_bankforclient.domain.TokenStorage
) : AccountRepository {

    private val currentUserId: UUID
        get() = tokenStorage.getUserId()
            ?.let { UUID.fromString(it) }
            ?: error("Пользователь не авторизован")

    override suspend fun getAccounts(): List<Account> {
        return try {
            accountService.getUserBankAccounts(currentUserId).map { it.toAccountDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getAccountDetails(accountNumber: String): Account {
        val accountDetails = accountService.getBankAccountDetails(currentUserId, accountNumber)
        return accountDetails.toAccountDomain()
    }

    override suspend fun createAccount(initialAmount: Double) {
        accountService.createBankAccount(
            userId = currentUserId,
            request = MoneyAmountRequestModel(amount = initialAmount)
        )
    }

    override suspend fun closeAccount(accountId: String) {
        accountService.closeBankAccount(
            userId = currentUserId,
            accountNumber = accountId
        )
    }

    override suspend fun deposit(accountId: String, amount: Double) {
        try {
            val response = accountService.replenishBankAccount(
                userId = currentUserId,
                bankAccountNumber = accountId,
                request = MoneyAmountRequestModel(amount = amount)
            )

            if (response.status.name != "SUCCESS") {
                throw Exception("Операция пополнения в работе")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun withdrawal(accountId: String, amount: Double) {
        try {
            val response = accountService.withdrawFromBankAccount(
                userId = currentUserId,
                bankAccountNumber = accountId,
                request = MoneyAmountRequestModel(amount = amount)
            )

            if (response.status.name != "SUCCESS") {
                throw Exception("Операция снятия средств в работе")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getTransactions(): List<Transaction> {
        return try {
            accountService.getUserOperations(currentUserId).map { operation ->
                operation.toDomain()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getAccountTransactions(accountNumber: String): List<Transaction> {
        return try {
            accountService.getAccountOperations(
                userId = currentUserId,
                accountNumber = accountNumber,
                transferType = TransferAccountType.BANK_ACCOUNT
            ).map { operation ->
                operation.toDomain(fallbackAccountId = accountNumber)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
