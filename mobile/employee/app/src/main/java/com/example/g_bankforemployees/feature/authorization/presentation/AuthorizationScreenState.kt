package com.example.g_bankforemployees.feature.authorization.presentation

import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeLoginInput
import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeRegistrationInput

sealed interface AuthorizationScreenState {

    data class Default(
        val loginInput: EmployeeLoginInput,
        val registrationInput: EmployeeRegistrationInput,
    ) : AuthorizationScreenState {

        companion object {
            val Initial = Default(
                loginInput = EmployeeLoginInput.Empty,
                registrationInput = EmployeeRegistrationInput.Empty,
            )
        }
    }

    data class Error(
        val title: String,
        val description: String,
    ) : AuthorizationScreenState

    data object AuthSuccess : AuthorizationScreenState

    data object Loading : AuthorizationScreenState
}
