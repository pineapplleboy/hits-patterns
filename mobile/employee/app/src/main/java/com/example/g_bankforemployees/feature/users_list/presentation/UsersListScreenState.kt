package com.example.g_bankforemployees.feature.users_list.presentation

import com.example.g_bankforemployees.feature.users_list.domain.model.User

sealed interface UsersListScreenState {

    data class Default(
        val clients: List<User>,
        val employees: List<User>,
    ) : UsersListScreenState

    data class Error(
        val message: String,
    ) : UsersListScreenState

    data object Unauthorized : UsersListScreenState

    data object Loading : UsersListScreenState
}