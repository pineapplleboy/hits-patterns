package com.example.g_bankforclient.presentation.state

sealed interface AuthorizationScreenState {
    data object Default : AuthorizationScreenState
    data object Loading : AuthorizationScreenState
    data object AuthSuccess : AuthorizationScreenState
    data class Error(val message: String) : AuthorizationScreenState
}
