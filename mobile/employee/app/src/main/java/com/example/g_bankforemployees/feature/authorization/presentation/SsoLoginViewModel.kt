package com.example.g_bankforemployees.feature.authorization.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.feature.authorization.domain.AuthSessionCoordinator
import com.example.g_bankforemployees.feature.authorization.domain.TokenStorage
import com.example.g_bankforemployees.feature.settings.domain.usecase.SyncThemeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SsoLoginViewModel(
    private val tokenStorage: TokenStorage,
    private val authSessionCoordinator: AuthSessionCoordinator,
    private val syncThemeUseCase: SyncThemeUseCase,
    private val navigatorHolder: NavigatorHolder,
) : ViewModel() {

    private val _state = MutableStateFlow<SsoLoginScreenState>(SsoLoginScreenState.Default)
    val state: StateFlow<SsoLoginScreenState> = _state.asStateFlow()

    init {
        checkExistingSession()
    }

    fun onLoginClick() {
        _state.value = SsoLoginScreenState.Loading
        navigatorHolder.navigator?.navigateToSsoGate()
    }

    fun onRetry() {
        _state.value = SsoLoginScreenState.Default
        checkExistingSession()
    }

    private fun checkExistingSession() {
        val token = tokenStorage.getToken()
        if (token.isNullOrBlank()) {
            authSessionCoordinator.onLogoutFinished()
            _state.value = SsoLoginScreenState.Default
            return
        }

        _state.value = SsoLoginScreenState.Loading
        viewModelScope.launch {
            syncThemeUseCase()
                .onSuccess {
                    authSessionCoordinator.onAuthorized()
                    navigatorHolder.navigator?.navigateToUsersList()
                }
                .onFailure { error ->
                    _state.value = SsoLoginScreenState.Error(
                        message = error.message
                            ?: "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u043e\u0434\u0433\u043e\u0442\u043e\u0432\u0438\u0442\u044c \u043f\u0440\u0438\u043b\u043e\u0436\u0435\u043d\u0438\u0435",
                    )
                }
        }
    }
}
