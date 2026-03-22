package com.example.g_bankforemployees.feature.account_operations.data.repository

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.common.network.safeApiCall
import com.example.g_bankforemployees.common.realtime.domain.RealtimeSessionManager
import com.example.g_bankforemployees.common.realtime.domain.model.RealtimeEvent
import com.example.g_bankforemployees.common.realtime.domain.model.RealtimeOperation
import com.example.g_bankforemployees.common.realtime.domain.repository.RealtimeEventsRepository
import com.example.g_bankforemployees.feature.account_operations.data.mapper.toDomain
import com.example.g_bankforemployees.feature.account_operations.data.remote.AccountOperationsApi
import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation
import com.example.g_bankforemployees.feature.account_operations.domain.repository.AccountOperationsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

private const val TRANSFER_TYPE_BANK_ACCOUNT = "BANK_ACCOUNT"

class AccountOperationsRepositoryImpl(
    private val accountOperationsApi: AccountOperationsApi,
    private val realtimeSessionManager: RealtimeSessionManager,
    private val realtimeEventsRepository: RealtimeEventsRepository,
) : AccountOperationsRepository {

    override suspend fun observeBankAccount(
        userId: String,
        accountNumber: String,
    ): Result<Flow<BankAccount>> {
        if (userId.isBlank() || accountNumber.isBlank()) {
            return Result.failure(IllegalArgumentException("Missing bank account params"))
        }

        return loadBankAccount(userId, accountNumber)
            .map { initialAccount ->
                callbackFlow {
                    var currentAccount = initialAccount
                    trySend(currentAccount)

                    realtimeSessionManager.observeUser(userId)
                    val socketJob = launch {
                        realtimeEventsRepository.events.collect { event ->
                            val updatedAccount = currentAccount.applyRealtimeEvent(event) ?: return@collect
                            if (updatedAccount != currentAccount) {
                                currentAccount = updatedAccount
                                trySend(currentAccount)
                            }
                        }
                    }

                    awaitClose {
                        socketJob.cancel()
                        realtimeSessionManager.stopObservingUser(userId)
                    }
                }
            }
    }

    override suspend fun observeCreditAccount(
        userId: String,
        accountNumber: String,
    ): Result<Flow<CreditAccount>> {
        if (userId.isBlank() || accountNumber.isBlank()) {
            return Result.failure(IllegalArgumentException("Missing credit account params"))
        }

        return loadCreditAccount(userId, accountNumber)
            .map { initialAccount ->
                callbackFlow {
                    var currentAccount = initialAccount
                    trySend(currentAccount)

                    realtimeSessionManager.observeUser(userId)
                    val socketJob = launch {
                        realtimeEventsRepository.events.collect { event ->
                            val updatedAccount = currentAccount.applyRealtimeEvent(event) ?: return@collect
                            if (updatedAccount != currentAccount) {
                                currentAccount = updatedAccount
                                trySend(currentAccount)
                            }
                        }
                    }

                    awaitClose {
                        socketJob.cancel()
                        realtimeSessionManager.stopObservingUser(userId)
                    }
                }
            }
    }

    override suspend fun observeAccountOperations(
        userId: String,
        accountNumber: String,
        transferType: String,
    ): Result<Flow<List<Operation>>> {
        if (userId.isBlank() || accountNumber.isBlank()) {
            return Result.failure(IllegalArgumentException("Missing account operations params"))
        }

        val normalizedTransferType = transferType.ifBlank { TRANSFER_TYPE_BANK_ACCOUNT }
        return loadAccountOperations(userId, accountNumber, normalizedTransferType)
            .map { initialOperations ->
                callbackFlow {
                    var currentOperations = initialOperations.sortedByDescending { it.createTime }
                    trySend(currentOperations)

                    realtimeSessionManager.observeUser(userId)
                    val socketJob = launch {
                        realtimeEventsRepository.events.collect { event ->
                            val updatedOperations = currentOperations.applyRealtimeEvent(
                                event = event,
                                accountNumber = accountNumber,
                                transferType = normalizedTransferType,
                            ) ?: return@collect
                            if (updatedOperations != currentOperations) {
                                currentOperations = updatedOperations
                                trySend(currentOperations)
                            }
                        }
                    }

                    awaitClose {
                        socketJob.cancel()
                        realtimeSessionManager.stopObservingUser(userId)
                    }
                }
            }
    }

    override suspend fun getExpiredOperations(userId: String): Result<List<Operation>> =
        safeApiCall(
            apiCall = { accountOperationsApi.getExpiredOperations(userId) },
            converter = { list -> list.map { it.toDomain() } }
        )

    private suspend fun loadBankAccount(
        userId: String,
        accountNumber: String,
    ): Result<BankAccount> = safeApiCall(
        apiCall = { accountOperationsApi.getBankAccount(userId, accountNumber) },
        converter = { it.toDomain() },
    )

    private suspend fun loadCreditAccount(
        userId: String,
        accountNumber: String,
    ): Result<CreditAccount> = safeApiCall(
        apiCall = { accountOperationsApi.getCreditAccount(userId, accountNumber) },
        converter = { it.toDomain() },
    )

    private suspend fun loadAccountOperations(
        userId: String,
        accountNumber: String,
        transferType: String,
    ): Result<List<Operation>> = safeApiCall(
        apiCall = { accountOperationsApi.getAccountOperations(userId, accountNumber, transferType) },
        converter = { list -> list.map { it.toDomain() } },
    )

    private fun BankAccount.applyRealtimeEvent(event: RealtimeEvent): BankAccount? = when (event) {
        is RealtimeEvent.BankAccountBalanceChanged -> {
            if (!event.matchesAccount(accountId = id, accountNumber = accountNumber)) {
                null
            } else {
                copy(
                    balance = event.balance.toDisplayAmount(),
                    balanceText = event.balance,
                )
            }
        }

        else -> null
    }

    private fun CreditAccount.applyRealtimeEvent(event: RealtimeEvent): CreditAccount? = when (event) {
        is RealtimeEvent.CreditAccountDebtChanged -> {
            if (!event.matchesAccount(accountId = id, accountNumber = accountNumber)) {
                null
            } else {
                copy(
                    dept = event.balance.toDisplayAmount(),
                    deptText = event.balance,
                )
            }
        }

        else -> null
    }

    private fun List<Operation>.applyRealtimeEvent(
        event: RealtimeEvent,
        accountNumber: String,
        transferType: String,
    ): List<Operation>? = when (event) {
        is RealtimeEvent.OperationUpsert -> {
            if (!event.operation.isRelevantForAccount(accountNumber, transferType)) {
                null
            } else {
                filterNot { it.operationId == event.operation.operationId }
                    .plus(event.operation.toDomainOperation())
                    .sortedByDescending { it.createTime }
            }
        }

        is RealtimeEvent.OperationStatusChanged -> {
            val updatedOperations = map { operation ->
                if (operation.operationId == event.operationId) {
                    operation.copy(status = event.newStatus)
                } else {
                    operation
                }
            }
            if (updatedOperations == this) null else updatedOperations
        }

        else -> null
    }

    private fun RealtimeEvent.BankAccountBalanceChanged.matchesAccount(
        accountId: String,
        accountNumber: String,
    ): Boolean =
        this.accountId == accountId || this.accountNumber == accountNumber

    private fun RealtimeEvent.CreditAccountDebtChanged.matchesAccount(
        accountId: String,
        accountNumber: String,
    ): Boolean =
        this.accountId == accountId || this.accountNumber == accountNumber

    private fun RealtimeOperation.isRelevantForAccount(
        accountNumber: String,
        transferType: String,
    ): Boolean = transferAccountType == transferType &&
        (accountNumberFrom == accountNumber || recipientAccountNumber == accountNumber)

    private fun RealtimeOperation.toDomainOperation(): Operation = Operation(
        operationId = operationId,
        accountNumberFrom = accountNumberFrom,
        userIdFrom = userIdFrom,
        recipientAccountNumber = recipientAccountNumber,
        recipientName = null,
        amount = amount,
        transferAccountType = transferAccountType,
        actionType = actionType,
        status = status,
        createTime = createTime,
    )

    private fun String.toDisplayAmount(): Double =
        replace(',', '.')
            .replace(Regex("[^0-9.-]"), "")
            .toDoubleOrNull()
            ?: 0.0
}
