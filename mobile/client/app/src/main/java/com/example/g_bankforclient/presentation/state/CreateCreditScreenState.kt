package com.example.g_bankforclient.presentation.state

sealed interface CreateCreditScreenState {

    data object Default : CreateCreditScreenState

    data object Loading : CreateCreditScreenState

    data class Success(
        val message: String
    ) : CreateCreditScreenState

    data class Error(
        val message: String
    ) : CreateCreditScreenState
}
