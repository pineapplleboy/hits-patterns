package com.example.g_bankforclient.presentation.state

import com.example.g_bankforclient.domain.models.Credit
import com.example.g_bankforclient.domain.models.CreditRate
import com.example.g_bankforclient.domain.models.CreditRating

sealed interface CreditsScreenState {

    data class Default(
        val credits: List<Credit>,
        val creditRates: List<CreditRate> = emptyList(),
        val creditRating: CreditRating? = null,
        val showRatingDialog: Boolean = false,
        val isRatingLoading: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : CreditsScreenState

    data object Loading : CreditsScreenState

    data class Error(
        val message: String
    ) : CreditsScreenState
}
