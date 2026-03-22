package com.example.g_bankforemployees.feature.users_list.presentation

import com.example.g_bankforemployees.feature.users_list.domain.model.User

sealed interface UsersListScreenState {
    data class Default(
        val selectedUsersTabIndex: Int = 0,
        val clients: List<User> = emptyList(),
        val employees: List<User> = emptyList(),
    ) : UsersListScreenState

    data class Error(
        val message: String,
    ) : UsersListScreenState

    data object Loading : UsersListScreenState
}