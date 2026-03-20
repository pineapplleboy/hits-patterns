package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.TokenStorage
import com.example.g_bankforclient.domain.models.Transaction
import com.example.g_bankforclient.domain.models.TransactionType
import com.example.g_bankforclient.domain.models.UserRealtimeEvent
import com.example.g_bankforclient.domain.usecase.account.GetAccountTransactionsUseCase
import com.example.g_bankforclient.domain.usecase.realtime.ObserveUserRealtimeEventsUseCase
import com.example.g_bankforclient.presentation.state.TransactionHistoryScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val getAccountTransactionsUseCase: GetAccountTransactionsUseCase,
    private val tokenStorage: TokenStorage,
    private val observeUserRealtimeEventsUseCase: ObserveUserRealtimeEventsUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<TransactionHistoryScreenState> = MutableStateFlow(
        value = TransactionHistoryScreenState.Default(
            transactions = emptyList()
        )
    )
    val state: StateFlow<TransactionHistoryScreenState> = _state.asStateFlow()

    private var realtimeJob: Job? = null

    fun loadTransactionHistory(accountId: String) {
        viewModelScope.launch {
            _state.value = TransactionHistoryScreenState.Loading
            realtimeJob?.cancel()
            realtimeJob = null

            runCatching {
                getAccountTransactionsUseCase(accountId)
            }.onSuccess { accountTransactions ->
                _state.value = TransactionHistoryScreenState.Default(
                    transactions = accountTransactions
                )
            }.onFailure { e ->
                _state.value = TransactionHistoryScreenState.Error(
                    message = e.message ?: "Не удалось загрузить историю операций"
                )
            }
        }

        val userId = tokenStorage.getUserId()
        if (userId.isNullOrBlank()) return

        realtimeJob = viewModelScope.launch {
            observeUserRealtimeEventsUseCase(userId).collectLatest { event ->
                val current =
                    _state.value as? TransactionHistoryScreenState.Default ?: return@collectLatest
                when (event) {
                    is UserRealtimeEvent.OperationStatusUpdate -> {
                        val relevant =
                            event.accountNumberFrom == accountId || event.recipientAccountNumber == accountId
                        if (!relevant) return@collectLatest

                        val txAccountId = when {
                            event.accountNumberFrom == accountId -> event.accountNumberFrom
                            event.recipientAccountNumber == accountId -> event.recipientAccountNumber
                            else -> accountId
                        }

                        val updatedTx = Transaction(
                            id = event.operationId,
                            accountId = txAccountId ?: accountId,
                            type = mapActionTypeToTransactionType(event.actionType),
                            amount = event.amount ?: 0.0,
                            date = event.createTime ?: Date(),
                            description = event.actionType ?: "Операция",
                            status = event.status,
                            fromAccount = event.accountNumberFrom,
                            toAccount = event.recipientAccountNumber
                        )

                        val updatedList = updateOrInsertTransaction(
                            transactions = current.transactions,
                            tx = updatedTx
                        )
                        _state.value = current.copy(transactions = updatedList)
                    }

                    is UserRealtimeEvent.OperationCreate -> {
                        val updatedList = current.transactions.map { tx ->
                            if (tx.id == event.operationId) tx.copy(status = event.newStatus) else tx
                        }
                        if (updatedList != current.transactions) {
                            _state.value = current.copy(transactions = updatedList)
                        }
                    }

                    is UserRealtimeEvent.BankAccountSumUpdate -> {
                        // Не влияет на страницу истории напрямую (там только транзакции).
                    }
                }
            }
        }
    }

    private fun updateOrInsertTransaction(
        transactions: List<Transaction>,
        tx: Transaction
    ): List<Transaction> {
        val idx = transactions.indexOfFirst { it.id == tx.id && it.accountId == tx.accountId }
        return if (idx >= 0) {
            val mutable = transactions.toMutableList()
            mutable[idx] = tx
            mutable
        } else {
            // Новые операции показываем первыми.
            listOf(tx) + transactions
        }
    }

    private fun mapActionTypeToTransactionType(actionType: String?): TransactionType {
        val a = actionType ?: return TransactionType.INFO
        return when {
            a.contains("TRANSFER_RECEIVED", ignoreCase = true) -> TransactionType.DEPOSIT
            a.contains("TRANSFER_SENT", ignoreCase = true) -> TransactionType.WITHDRAWAL
            else -> TransactionType.INFO
        }
    }
}
