package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.models.UserCredentials
import com.example.g_bankforclient.domain.usecase.auth.LoginUseCase
import com.example.g_bankforclient.domain.usecase.auth.RegisterUseCase
import com.example.g_bankforclient.presentation.state.AuthorizationScreenState
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val gson: Gson
) : ViewModel() {

    private val _state: MutableStateFlow<AuthorizationScreenState> = MutableStateFlow(
        value = AuthorizationScreenState.Default(
            credentials = UserCredentials("", "")
        )
    )
    val state: StateFlow<AuthorizationScreenState> = _state.asStateFlow()

    fun onLoginClick() {
        val currentState = _state.value
        if (currentState !is AuthorizationScreenState.Default) return

        viewModelScope.launch {
            _state.value = AuthorizationScreenState.Loading

            val result = if (currentState.isRegisterMode) {
                runCatching {
                    registerUseCase(
                        name = currentState.name,
                        phone = currentState.credentials.login,
                        password = currentState.credentials.password
                    )
                }
            } else {
                runCatching {
                    loginUseCase(currentState.credentials)
                }
            }

            result.onSuccess { isAuthenticated ->
                if (isAuthenticated) {
                    _state.value = AuthorizationScreenState.AuthSuccess
                } else {
                    _state.value = AuthorizationScreenState.Error(
                        title = if (currentState.isRegisterMode) "Ошибка регистрации" else "Ошибка входа",
                        description = if (currentState.isRegisterMode)
                            "Не удалось зарегистрироваться. Возможно, пользователь уже существует."
                        else
                            "Неверный логин или пароль",
                        credentials = currentState.credentials,
                        isRegisterMode = currentState.isRegisterMode,
                        name = currentState.name
                    )
                }
            }.onFailure { e ->
                val errorMessage = if (e is HttpException) {
                    try {
                        val errorBody = e.response()?.errorBody()?.string()
                        val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
                        jsonObject.get("detail")?.asString ?: ""
                    } catch (ex: Exception) {
                        ""
                    }
                } else {
                    "Не удалось выполнить операцию"
                }
                _state.value = AuthorizationScreenState.Error(
                    title = if (currentState.isRegisterMode) "Ошибка регистрации" else "Ошибка входа",
                    description = errorMessage,
                    credentials = currentState.credentials,
                    isRegisterMode = currentState.isRegisterMode,
                    name = currentState.name
                )
            }
        }
    }

    fun toggleMode() {
        val currentState = _state.value
        if (currentState is AuthorizationScreenState.Default) {
            _state.value = currentState.copy(
                isRegisterMode = !currentState.isRegisterMode,
                name = if (currentState.isRegisterMode) "" else currentState.name
            )
        }
    }

    fun onNameChange(name: String) {
        val currentState = _state.value
        if (currentState is AuthorizationScreenState.Default) {
            _state.value = currentState.copy(name = name)
        }
    }
    
    fun onLoginChange(login: String) {
        val currentState = _state.value
        when (currentState) {
            is AuthorizationScreenState.Default -> {
                _state.value = currentState.copy(
                    credentials = currentState.credentials.copy(login = login)
                )
            }

            is AuthorizationScreenState.Error -> {
                _state.value = AuthorizationScreenState.Default(
                    credentials = currentState.credentials.copy(login = login),
                    isRegisterMode = currentState.isRegisterMode,
                    name = currentState.name
                )
            }

            else -> {}
        }
    }

    fun onPasswordChange(password: String) {
        val currentState = _state.value
        when (currentState) {
            is AuthorizationScreenState.Default -> {
                _state.value = currentState.copy(
                    credentials = currentState.credentials.copy(password = password)
                )
            }

            is AuthorizationScreenState.Error -> {
                _state.value = AuthorizationScreenState.Default(
                    credentials = currentState.credentials.copy(password = password),
                    isRegisterMode = currentState.isRegisterMode,
                    name = currentState.name
                )
            }

            else -> {}
        }
    }

    fun dismissError() {
        val currentState = _state.value
        if (currentState is AuthorizationScreenState.Error) {
            _state.value = AuthorizationScreenState.Default(
                credentials = currentState.credentials,
                isRegisterMode = currentState.isRegisterMode,
                name = currentState.name
            )
        }
    }
}
