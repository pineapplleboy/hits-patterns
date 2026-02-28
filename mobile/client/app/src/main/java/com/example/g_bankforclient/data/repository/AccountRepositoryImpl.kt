package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.common.models.Account
import com.example.g_bankforclient.common.models.Transaction
import com.example.g_bankforclient.common.models.TransactionType
import com.example.g_bankforclient.data.network.AccountService
import com.example.g_bankforclient.data.network.model.MoneyAmountRequestModel
import com.example.g_bankforclient.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountService: AccountService
) : AccountRepository {
    
    // Используем фиксированный UUID для демонстрации
    // В реальном приложении это должен быть ID текущего пользователя
    private val currentUserId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

    override fun getAccounts(): Flow<List<Account>> = flow {
        try {
            val accounts = accountService.getUserBankAccounts(currentUserId)
            val mappedAccounts = accounts.map { account ->
                Account(
                    id = account.accountNumber,
                    name = "Счет ${account.accountNumber.takeLast(4)}",
                    balance = account.balance
                )
            }
            emit(mappedAccounts)
        } catch (e: Exception) {
            // В случае ошибки эмитим пустой список
            emit(emptyList())
        }
    }

    override suspend fun createAccount(name: String) {
        try {
            // Замечание: API требует указания суммы, но интерфейс не предоставляет её
            // Используем 0 как значение по умолчанию
            accountService.createBankAccount(
                userId = currentUserId,
                request = MoneyAmountRequestModel(amount = 0.0)
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun closeAccount(accountId: String) {
        // API не предоставляет прямого метода для закрытия счета
        // В реальном приложении здесь должен быть вызов соответствующего API endpoint,
        // например DELETE /patterns/api/v1/users/{userId}/bank-accounts/{accountNumber}
        // или POST /patterns/api/v1/users/{userId}/bank-accounts/{accountNumber}/close
        throw NotImplementedError("Закрытие счетов не реализовано в API. Необходимо добавить соответствующий endpoint в AccountService.")
    }

    override suspend fun deposit(accountId: String, amount: Double) {
        try {
            val response = accountService.replenishBankAccount(
                userId = currentUserId,
                bankAccountNumber = accountId,
                request = MoneyAmountRequestModel(amount = amount)
            )
            
            // Проверяем статус операции
            if (response.status.name != "SUCCESS") {
                throw Exception("Операция пополнения завершена со статусом: ${response.status}")
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
            
            // Проверяем статус операции
            if (response.status.name != "SUCCESS") {
                throw Exception("Операция снятия средств завершена со статусом: ${response.status}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getTransactions(): Flow<List<Transaction>> = flow {
        try {
            val operations = accountService.getUserOperations(currentUserId)
            val mappedTransactions = operations.map { operation ->
                Transaction(
                    id = operation.operationId.toString(),
                    accountId = operation.accountNumberFrom ?: "",
                    type = when (operation.actionType) {
                        com.example.g_bankforclient.data.network.model.AccountActionType.OPEN_ACCOUNT -> TransactionType.DEPOSIT
                        com.example.g_bankforclient.data.network.model.AccountActionType.CLOSE_ACCOUNT -> TransactionType.WITHDRAWAL
                        com.example.g_bankforclient.data.network.model.AccountActionType.TRANSFER_RECEIVED -> TransactionType.DEPOSIT
                        com.example.g_bankforclient.data.network.model.AccountActionType.TRANSFER_SENT -> TransactionType.WITHDRAWAL
                    },
                    amount = operation.amount,
                    date = operation.createTime,
                    description = operation.actionType.name
                )
            }
            emit(mappedTransactions)
        } catch (e: Exception) {
            // В случае ошибки эмитим пустой список
            emit(emptyList())
        }
    }
}
