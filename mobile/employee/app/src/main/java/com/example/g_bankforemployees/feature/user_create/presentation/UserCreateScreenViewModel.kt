package com.example.g_bankforemployees.feature.user_create.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.feature.authorization.domain.model.RegisterUserInput
import com.example.g_bankforemployees.feature.authorization.domain.usecase.CreateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val ROLE_CLIENT = "CLIENT"
private const val ROLE_EMPLOYEE = "EMPLOYEE"

class UserCreateScreenViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val navigatorHolder: com.example.g_bankforemployees.common.navigation.NavigatorHolder,
) : ViewModel() {

    private val _state = MutableStateFlow(UserCreateScreenState())
    val state: StateFlow<UserCreateScreenState> = _state.asStateFlow()

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name, error = null) }
    }

    fun onPhoneChange(phone: String) {
        _state.update { it.copy(phone = phone, error = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    fun onRoleIndexChange(roleIndex: Int) {
        _state.update { it.copy(roleIndex = roleIndex, error = null) }
    }

    fun createUser() {
        val name = _state.value.name.trim()
        val phone = _state.value.phone.trim()
        val password = _state.value.password
        if (name.isBlank() || phone.isBlank() || password.isBlank()) {
            _state.update { it.copy(error = "Заполните все поля") }
            return
        }
        val role = if (_state.value.roleIndex == 0) ROLE_CLIENT else ROLE_EMPLOYEE
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            createUserUseCase(
                RegisterUserInput(
                    name = name,
                    phone = phone,
                    password = password,
                    userRole = role,
                )
            )
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    (navigatorHolder.navigator as? com.example.g_bankforemployees.common.navigation.AppNavigator)
                        ?.navigateBackFromUserCreate()
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Не удалось создать пользователя",
                        )
                    }
                }
        }
    }

    fun onBackClick() {
        navigatorHolder.navigator?.navigateBack()
    }
}
