package com.example.g_bankforemployees.feature.credit_rate.presentation

import com.example.g_bankforemployees.feature.credit_rate.domain.model.CreditRate

sealed interface TariffsListScreenState {

    data class Default(
        val creditRates: List<CreditRate> = emptyList(),
    ) : TariffsListScreenState

    data class Error(
        val message: String,
    ) : TariffsListScreenState

    data object Loading : TariffsListScreenState
}

