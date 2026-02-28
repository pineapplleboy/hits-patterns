package com.example.g_bankforclient.presentation.state

import com.example.g_bankforclient.common.models.Account

sealed interface HomeScreenState {

    data class Default(
        val accounts: List<Account>,
        val isLoading: Boolean = false
    ) : HomeScreenState

    data object Loading : HomeScreenState

    data class Error(
        val message: String
    ) : HomeScreenState
}
