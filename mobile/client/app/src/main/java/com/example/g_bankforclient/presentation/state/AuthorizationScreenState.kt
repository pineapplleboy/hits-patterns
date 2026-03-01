package com.example.g_bankforclient.presentation.state

import com.example.g_bankforclient.domain.models.UserCredentials

sealed interface AuthorizationScreenState {

    data class Default(
        val credentials: UserCredentials,
        val isRegisterMode: Boolean = false,
        val name: String = ""
    ) : AuthorizationScreenState

    data object Loading : AuthorizationScreenState

    data object AuthSuccess : AuthorizationScreenState

    data class Error(
        val title: String,
        val description: String,
        val credentials: UserCredentials,
        val isRegisterMode: Boolean = false,
        val name: String = ""
    ) : AuthorizationScreenState
}