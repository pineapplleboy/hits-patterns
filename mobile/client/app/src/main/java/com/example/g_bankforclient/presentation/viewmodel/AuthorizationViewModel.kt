package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.TokenStorage
import com.example.g_bankforclient.domain.models.UserCredentials
import com.example.g_bankforclient.domain.usecase.auth.LoginUseCase
import com.example.g_bankforclient.presentation.state.AuthorizationScreenState
import com.example.g_bankforclient.presentation.ui.utils.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _state =
        MutableStateFlow<AuthorizationScreenState>(AuthorizationScreenState.Default)
    val state: StateFlow<AuthorizationScreenState> = _state.asStateFlow()

    init {
        if (!tokenStorage.getUserId().isNullOrBlank()) {
            _state.value = AuthorizationScreenState.AuthSuccess
        }
    }

    fun onLoginClick() {
        if (_state.value !is AuthorizationScreenState.Default) return
        viewModelScope.launch {
            _state.value = AuthorizationScreenState.Loading
            runCatching { loginUseCase(UserCredentials("", "")) }
                .onSuccess { isAuthenticated ->
                    _state.value = if (isAuthenticated) {
                        AuthorizationScreenState.AuthSuccess
                    } else {
                        AuthorizationScreenState.Error("Не удалось войти через SSO")
                    }
                }
                .onFailure { e ->
                    _state.value = AuthorizationScreenState.Error(
                        e.toUserMessage("Не удалось подключиться к серверу авторизации")
                    )
                }
        }
    }

    fun dismissError() {
        _state.value = AuthorizationScreenState.Default
    }
}
