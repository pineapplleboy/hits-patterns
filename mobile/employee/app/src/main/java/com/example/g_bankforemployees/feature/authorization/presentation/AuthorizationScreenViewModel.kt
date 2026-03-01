package com.example.g_bankforemployees.feature.authorization.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeLoginInput
import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeRegistrationInput
import com.example.g_bankforemployees.feature.authorization.domain.usecase.EmployeeLoginUseCase
import com.example.g_bankforemployees.feature.authorization.domain.usecase.EmployeeRegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthorizationScreenViewModel(
    private val employeeLoginUseCase: EmployeeLoginUseCase,
    private val employeeRegisterUseCase: EmployeeRegisterUseCase,
    private val navigatorHolder: com.example.g_bankforemployees.common.navigation.NavigatorHolder,
) : ViewModel() {

    private val _state: MutableStateFlow<AuthorizationScreenState> = MutableStateFlow(
        AuthorizationScreenState.Default.Initial
    )
    val state: StateFlow<AuthorizationScreenState> = _state.asStateFlow()

    fun onLoginClick() {
        val currentState = _state.value
        if (currentState !is AuthorizationScreenState.Default) return
        viewModelScope.launch {
            _state.value = AuthorizationScreenState.Loading
            employeeLoginUseCase(currentState.loginInput)
                .onSuccess {
                    _state.value = AuthorizationScreenState.Default.Initial
                    navigatorHolder.navigator?.navigateToUsersList()
                }
                .onFailure { e ->
                    _state.value = AuthorizationScreenState.Error(
                        title = "Ошибка входа",
                        description = e.message ?: "Не удалось выполнить вход",
                    )
                }
        }
    }

    fun onRegisterClick() {
        val currentState = _state.value
        if (currentState !is AuthorizationScreenState.Default) return
        viewModelScope.launch {
            _state.value = AuthorizationScreenState.Loading
            employeeRegisterUseCase(currentState.registrationInput)
                .onSuccess {
                    _state.value = AuthorizationScreenState.Default.Initial
                    navigatorHolder.navigator?.navigateToUsersList()
                }
                .onFailure { e ->
                    _state.value = AuthorizationScreenState.Error(
                        title = "Ошибка регистрации",
                        description = e.message ?: "Не удалось зарегистрироваться",
                    )
                }
        }
    }

    fun onLoginPhoneChange(phone: String) {
        _state.update {
            if (it is AuthorizationScreenState.Default) {
                it.copy(loginInput = it.loginInput.copy(phone = phone))
            } else it
        }
    }

    fun onLoginPasswordChange(password: String) {
        _state.update {
            if (it is AuthorizationScreenState.Default) {
                it.copy(loginInput = it.loginInput.copy(password = password))
            } else it
        }
    }

    fun onRegistrationNameChange(name: String) {
        _state.update {
            if (it is AuthorizationScreenState.Default) {
                it.copy(registrationInput = it.registrationInput.copy(name = name))
            } else it
        }
    }

    fun onRegistrationPhoneChange(phone: String) {
        _state.update {
            if (it is AuthorizationScreenState.Default) {
                it.copy(registrationInput = it.registrationInput.copy(phone = phone))
            } else it
        }
    }

    fun onRegistrationPasswordChange(password: String) {
        _state.update {
            if (it is AuthorizationScreenState.Default) {
                it.copy(registrationInput = it.registrationInput.copy(password = password))
            } else it
        }
    }

    fun onRetry() {
        _state.value = AuthorizationScreenState.Default.Initial
    }
}
