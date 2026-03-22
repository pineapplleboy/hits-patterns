package com.example.g_bankforemployees.feature.account_operations.presentation

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation

sealed interface AccountOperationsScreenState {
    data class Error(
        val message: String,
    ) : AccountOperationsScreenState

    data object Loading : AccountOperationsScreenState

    data class Default(
        val accountNumber: String,
        val userName: String,
        val transferType: String,
        val bankAccount: BankAccount? = null,
        val creditAccount: CreditAccount? = null,
        val isCreditExpired: Boolean? = null,
        val operations: List<Operation> = emptyList(),
        val warningMessage: String? = null,
    ) : AccountOperationsScreenState
}
