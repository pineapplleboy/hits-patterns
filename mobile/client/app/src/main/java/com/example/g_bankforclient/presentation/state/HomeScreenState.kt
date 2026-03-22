package com.example.g_bankforclient.presentation.state

import com.example.g_bankforclient.domain.models.Account

sealed interface HomeScreenState {

    data class Default(
        val accounts: List<Account>,
        val hiddenAccounts: List<Account> = emptyList(),
        val showingHidden: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : HomeScreenState

    data object Loading : HomeScreenState

    data class Error(
        val message: String
    ) : HomeScreenState
}
