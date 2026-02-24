package com.example.g_bankforclient.presentation.state

import com.example.g_bankforclient.common.models.UserCredentials

sealed interface AuthorizationScreenState {

    data class Default(
        val credentials: UserCredentials,
    ) : AuthorizationScreenState

    data object Loading : AuthorizationScreenState
}