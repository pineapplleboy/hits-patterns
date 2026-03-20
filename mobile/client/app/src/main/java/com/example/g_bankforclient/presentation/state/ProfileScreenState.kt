package com.example.g_bankforclient.presentation.state

import com.example.g_bankforclient.domain.models.User

sealed interface ProfileScreenState {

    data class Default(val user: User) : ProfileScreenState

    data object Loading : ProfileScreenState

    data class Error(
        val message: String
    ) : ProfileScreenState
}
