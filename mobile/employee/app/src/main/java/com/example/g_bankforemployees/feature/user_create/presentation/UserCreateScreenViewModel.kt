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

    private val _state = MutableStateFlow<UserCreateScreenState>(
        UserCreateScreenState.Default()
    )
    val state: StateFlow<UserCreateScreenState> = _state.asStateFlow()

    fun onNameChange(name: String) {
        val current = _state.value
        if (current !is UserCreateScreenState.Default) return
        _state.value = current.copy(name = name)
    }

    fun onPhoneChange(phone: String) {
        val current = _state.value
        if (current !is UserCreateScreenState.Default) return
        _state.value = current.copy(phone = phone)
    }

    fun onPasswordChange(password: String) {
        val current = _state.value
        if (current !is UserCreateScreenState.Default) return
        _state.value = current.copy(password = password)
    }

    fun onRoleIndexChange(roleIndex: Int) {
        val current = _state.value
        if (current !is UserCreateScreenState.Default) return
        _state.value = current.copy(roleIndex = roleIndex)
    }

    fun createUser() {
        val current = _state.value
        if (current !is UserCreateScreenState.Default) return
        val name = current.name.trim()
        val phone = current.phone.trim()
        val password = current.password
        if (name.isBlank() || phone.isBlank() || password.isBlank()) {
            _state.value = UserCreateScreenState.Error(message = "Р—Р°РїРѕР»РЅРёС‚Рµ РІСЃРµ РїРѕР»СЏ")
            return
        }
        val role = if (current.roleIndex == 0) ROLE_CLIENT else ROLE_EMPLOYEE
        viewModelScope.launch {
            _state.value = UserCreateScreenState.Loading
            createUserUseCase(
                RegisterUserInput(
                    name = name,
                    phone = phone,
                    password = password,
                    userRole = role,
                )
            )
                .onSuccess {
                    (navigatorHolder.navigator as? com.example.g_bankforemployees.common.navigation.AppNavigator)
                        ?.navigateBackFromUserCreate()
                }
                .onFailure { e ->
                    _state.value = UserCreateScreenState.Error(
                        message = e.message?.takeUnless { it.isBlank() } ?: "РќРµ СѓРґР°Р»РѕСЃСЊ СЃРѕР·РґР°С‚СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ",
                    )
                }
        }
    }

    fun onErrorDismiss() {
        _state.value = UserCreateScreenState.Default()
    }

    fun onBackClick() {
        navigatorHolder.navigator?.navigateBack()
    }
}


