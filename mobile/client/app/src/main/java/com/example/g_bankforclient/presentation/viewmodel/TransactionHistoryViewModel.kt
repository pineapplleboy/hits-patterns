package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.usecase.account.GetAccountTransactionsUseCase
import com.example.g_bankforclient.presentation.state.TransactionHistoryScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val getAccountTransactionsUseCase: GetAccountTransactionsUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<TransactionHistoryScreenState> = MutableStateFlow(
        value = TransactionHistoryScreenState.Default(
            transactions = emptyList()
        )
    )
    val state: StateFlow<TransactionHistoryScreenState> = _state.asStateFlow()

    fun loadTransactionHistory(accountId: String) {
        viewModelScope.launch {
            _state.value = TransactionHistoryScreenState.Loading
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
    }
}
