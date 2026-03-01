package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.usecase.account.GetAccountsUseCase
import com.example.g_bankforclient.domain.usecase.auth.LogoutUseCase
import com.example.g_bankforclient.presentation.state.HomeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<HomeScreenState> = MutableStateFlow(
        value = HomeScreenState.Default(accounts = emptyList())
    )
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        loadAccounts()
    }

    fun loadAccounts() {
        viewModelScope.launch {
            val currentAccounts =
                (_state.value as? HomeScreenState.Default)?.accounts ?: emptyList()
            _state.value = HomeScreenState.Default(accounts = currentAccounts, isLoading = true)
            runCatching { getAccountsUseCase() }
                .onSuccess { accounts ->
                    _state.value = HomeScreenState.Default(accounts = accounts, isLoading = false)
                }
                .onFailure { e ->
                    _state.value = HomeScreenState.Error(
                        message = e.message ?: "Не удалось загрузить счета"
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
