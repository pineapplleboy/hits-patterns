package com.example.g_bankforemployees.feature.credit_rate.presentation

sealed interface CreditRateCreateScreenState {
    data class Default(
        val name: String = "",
        val percent: String = "",
        val days: String = "",
        val hours: String = "",
        val minutes: String = "",
    ) : CreditRateCreateScreenState

    data object Loading : CreditRateCreateScreenState

    data class Error(
        val message: String,
    ) : CreditRateCreateScreenState
}
