package com.example.g_bankforclient.presentation.state

import com.example.g_bankforclient.domain.models.Credit

sealed interface CreditDetailsScreenState {

    data class Default(
        val credit: Credit,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : CreditDetailsScreenState

    data object Loading : CreditDetailsScreenState

    data class Error(
        val message: String
    ) : CreditDetailsScreenState
}
