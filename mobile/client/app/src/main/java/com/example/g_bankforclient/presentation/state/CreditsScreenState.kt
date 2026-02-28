package com.example.g_bankforclient.presentation.state

import com.example.g_bankforclient.common.models.Credit
import com.example.g_bankforclient.common.models.CreditRate

sealed interface CreditsScreenState {

    data class Default(
        val credits: List<Credit>,
        val creditRates: List<CreditRate> = emptyList(),
        val isLoading: Boolean = false
    ) : CreditsScreenState

    data object Loading : CreditsScreenState

    data class Error(
        val message: String
    ) : CreditsScreenState
}
