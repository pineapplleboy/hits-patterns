package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.usecase.transaction.GetTransactionsUseCase
import com.example.g_bankforclient.presentation.state.TransactionHistoryScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
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
            try {
                getTransactionsUseCase().collect { transactions ->
                    val accountTransactions = transactions.filter { it.accountId == accountId }
                    _state.value = TransactionHistoryScreenState.Default(
                        transactions = accountTransactions
                    )
                }
            } catch (e: Exception) {
                _state.value = TransactionHistoryScreenState.Error(
                    message = e.message ?: "Failed to load transaction history"
                )
            }
        }
    }
}
