package com.example.g_bankforclient.presentation.state

import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.domain.models.Transaction

sealed interface AccountDetailsScreenState {

    data class Default(
        val account: Account,
        val transactions: List<Transaction>,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : AccountDetailsScreenState

    data object Loading : AccountDetailsScreenState

    data class Error(
        val message: String
    ) : AccountDetailsScreenState
}
