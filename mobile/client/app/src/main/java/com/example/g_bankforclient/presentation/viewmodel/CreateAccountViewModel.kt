package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.usecase.account.CreateAccountUseCase
import com.example.g_bankforclient.presentation.state.CreateAccountScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val createAccountUseCase: CreateAccountUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<CreateAccountScreenState> = MutableStateFlow(
        value = CreateAccountScreenState.Default
    )
    val state: StateFlow<CreateAccountScreenState> = _state.asStateFlow()

    fun createAccount() {
        viewModelScope.launch {
            _state.value = CreateAccountScreenState.Loading
            runCatching { createAccountUseCase(0.0) }
                .onSuccess {
                    _state.value = CreateAccountScreenState.Success(
                        message = "Счет успешно открыт"
                    )
                }
                .onFailure { e ->
                    _state.value = CreateAccountScreenState.Error(
                        message = e.message ?: "Не удалось открыть счет"
                    )
                }
        }
    }
}
