package com.example.g_bankforemployees.feature.user_create.presentation

sealed interface UserCreateScreenState {
    data class Default(
        val name: String = "",
        val phone: String = "",
        val password: String = "",
        val roleIndex: Int = 0,
    ) : UserCreateScreenState

    data object Loading : UserCreateScreenState

    data class Error(
        val message: String,
    ) : UserCreateScreenState
}
