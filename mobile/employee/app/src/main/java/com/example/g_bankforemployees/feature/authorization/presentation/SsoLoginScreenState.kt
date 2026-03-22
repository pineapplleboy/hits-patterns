package com.example.g_bankforemployees.feature.authorization.presentation

sealed interface SsoLoginScreenState {
    data object Default : SsoLoginScreenState
    data object Loading : SsoLoginScreenState
    data class Error(
        val message: String,
    ) : SsoLoginScreenState
}
