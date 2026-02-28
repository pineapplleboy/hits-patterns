package com.example.g_bankforclient.presentation.state

sealed interface CreateAccountScreenState {

    data object Default : CreateAccountScreenState

    data object Loading : CreateAccountScreenState

    data class Success(
        val message: String
    ) : CreateAccountScreenState

    data class Error(
        val message: String
    ) : CreateAccountScreenState
}
