package com.example.g_bankforemployees.feature.authorization.presentation

import com.example.g_bankforemployees.feature.authorization.domain.model.UserCredentials

sealed interface AuthorizationScreenState {

    data class Default(
        val credentials: UserCredentials,
    ) : AuthorizationScreenState

    data object Loading : AuthorizationScreenState
}