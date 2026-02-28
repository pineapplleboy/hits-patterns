package com.example.g_bankforclient.presentation.state

import com.example.g_bankforclient.common.models.Transaction

sealed interface TransactionHistoryScreenState {

    data class Default(
        val transactions: List<Transaction>,
        val isLoading: Boolean = false
    ) : TransactionHistoryScreenState

    data object Loading : TransactionHistoryScreenState

    data class Error(
        val message: String
    ) : TransactionHistoryScreenState
}
