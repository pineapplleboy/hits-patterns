package com.example.g_bankforemployees.feature.client_details.presentation

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount

sealed interface ClientDetailsScreenState {
    data class Error(
        val message: String,
    ) : ClientDetailsScreenState

    data object Loading : ClientDetailsScreenState

    data class Default(
        val userName: String,
        val userPhone: String,
        val selectedTabIndex: Int = 0,
        val bankAccounts: List<BankAccount> = emptyList(),
        val creditAccounts: List<CreditAccount> = emptyList(),
    ) : ClientDetailsScreenState
}
