package com.example.g_bankforclient.presentation.state

import com.example.g_bankforclient.domain.models.Currency

sealed interface CreateAccountScreenState {

    data class Default(
        val currencies: List<Currency> = emptyList(),
        val selectedCurrencyId: Int? = null,
        val currenciesLoading: Boolean = false
    ) : CreateAccountScreenState

    data object Loading : CreateAccountScreenState

    data class Success(
        val message: String
    ) : CreateAccountScreenState

    data class Error(
        val message: String
    ) : CreateAccountScreenState
}
