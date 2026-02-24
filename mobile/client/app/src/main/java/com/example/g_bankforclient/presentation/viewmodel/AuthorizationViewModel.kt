package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.common.models.UserCredentials
import com.example.g_bankforclient.domain.usecase.auth.LoginUseCase
import com.example.g_bankforclient.presentation.state.AuthorizationScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<AuthorizationScreenState> = MutableStateFlow(
        value = AuthorizationScreenState.Default(
            credentials = UserCredentials("", "")
        )
    )
    val state: StateFlow<AuthorizationScreenState> = _state.asStateFlow()

    fun onLoginClick() {
        val currentState = _state.value
        if (currentState is AuthorizationScreenState.Default) {
            viewModelScope.launch {
                val isAuthenticated = loginUseCase(currentState.credentials)
            }
        }
    }

    fun onLoginChange(login: String) {
        val currentState = _state.value
        if (currentState is AuthorizationScreenState.Default) {
            _state.value = currentState.copy(
                credentials = currentState.credentials.copy(
                    login = login,
                )
            )
        }
    }

    fun onPasswordChange(password: String) {
        val currentState = _state.value
        if (currentState is AuthorizationScreenState.Default) {
            _state.value = currentState.copy(
                credentials = currentState.credentials.copy(
                    password = password,
                )
            )
        }
    }
}
