package com.example.g_bankforemployees.feature.authorization.presentation

sealed interface SsoGateScreenState {
    data object Default : SsoGateScreenState
    data object Loading : SsoGateScreenState
    data class Error(
        val message: String,
    ) : SsoGateScreenState
}
