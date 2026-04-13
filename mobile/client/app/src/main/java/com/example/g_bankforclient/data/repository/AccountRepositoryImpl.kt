package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.data.mapper.toCreditDomain
import com.example.g_bankforclient.data.mapper.toDomain
import com.example.g_bankforclient.data.network.AccountService
import com.example.g_bankforclient.data.network.RequestMetadataFactory
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
    private val tokenStorage: com.example.g_bankforclient.domain.TokenStorage,
    private val requestMetadataFactory: RequestMetadataFactory
) : AccountRepository {

    private val currentUserId: UUID
        get() = tokenStorage.getUserId()
            ?.let { UUID.fromString(it) }
            ?: error("Пользователь не авторизован")

    override suspend fun getAccounts(): List<Account> {
        return accountService.getUserBankAccounts(currentUserId, hidden = false)
            .map { it.toAccountDomain() }
    }

    override suspend fun getHiddenAccounts(): List<Account> {
        return accountService.getUserBankAccounts(currentUserId, hidden = true)
            .map { it.toAccountDomain() }
    }

    override suspend fun getRubAccounts(): List<Account> {
        return accountService.getRubBankAccounts(currentUserId)
            .map { it.toAccountDomain() }
    }

    override suspend fun getAccountDetails(accountNumber: String): Account {
        val accountDetails = accountService.getBankAccountDetails(currentUserId, accountNumber)
        return accountDetails.toAccountDomain()
    }

    override suspend fun createAccount(currencyId: Int) {
        accountService.createBankAccount(
            userId = currentUserId,
            currencyId = currencyId,
            metadata = requestMetadataFactory.createMutation()
        )
    }

    override suspend fun closeAccount(accountId: String) {
        accountService.closeBankAccount(
            userId = currentUserId,
            accountNumber = accountId,
            metadata = requestMetadataFactory.createMutation()
        )
    }

    override suspend fun deposit(accountId: String, amount: Double) {
        try {
            val response = accountService.replenishBankAccount(
                userId = currentUserId,
                bankAccountNumber = accountId,
                request = MoneyAmountRequestModel(amount = amount),
                metadata = requestMetadataFactory.createMutation()
            )

            if (response.status.name != "SUCCESS") {
                throw com.example.g_bankforclient.domain.models.OperationPendingException("Операция пополнения принята и выполняется")
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
                request = MoneyAmountRequestModel(amount = amount),
                metadata = requestMetadataFactory.createMutation()
            )

            if (response.status.name != "SUCCESS") {
                throw com.example.g_bankforclient.domain.models.OperationPendingException("Операция снятия принята и выполняется")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun transferToBankAccount(
        fromAccount: String,
        toAccount: String,
        amount: Double
    ) {
        val response = accountService.transferToBankAccount(
            userId = currentUserId,
            bankAccountNumber = fromAccount,
            bankAccountTo = toAccount,
            request = MoneyAmountRequestModel(amount = amount),
            metadata = requestMetadataFactory.createMutation()
        )
        if (response.status.name != "SUCCESS") {
            throw com.example.g_bankforclient.domain.models.OperationPendingException("Перевод принят и выполняется")
        }
    }

    override suspend fun getTransactions(): List<Transaction> {
        return accountService.getUserOperations(currentUserId).map { operation ->
            operation.toDomain()
        }
    }

    override suspend fun getAccountTransactions(accountNumber: String): List<Transaction> {
        return accountService.getAccountOperations(
            userId = currentUserId,
            accountNumber = accountNumber,
            transferType = TransferAccountType.BANK_ACCOUNT
        ).map { operation ->
            operation.toDomain(fallbackAccountId = accountNumber)
        }
    }

    override suspend fun getMissedPayments(): List<Transaction> {
        return accountService.getMissedCreditPayments(currentUserId).map { it.toCreditDomain() }
    }
}
