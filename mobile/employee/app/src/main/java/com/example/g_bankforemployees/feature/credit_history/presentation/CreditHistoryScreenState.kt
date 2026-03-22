package com.example.g_bankforemployees.feature.credit_history.presentation

import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation
import com.example.g_bankforemployees.feature.credit_history.domain.model.CreditRating

sealed interface CreditHistoryScreenState {
    data class Error(
        val message: String,
    ) : CreditHistoryScreenState

    data object Loading : CreditHistoryScreenState

    data class Default(
        val userName: String,
        val creditRating: CreditRating,
        val expiredOperations: List<Operation> = emptyList(),
        val warningMessage: String? = null,
    ) : CreditHistoryScreenState
}
