package com.example.g_bankforclient.presentation.state

import com.example.g_bankforclient.common.models.Credit

sealed interface CreditDetailsScreenState {

    data class Default(
        val credit: Credit,
        val isLoading: Boolean = false
    ) : CreditDetailsScreenState

    data object Loading : CreditDetailsScreenState

    data class Error(
        val message: String
    ) : CreditDetailsScreenState
}
