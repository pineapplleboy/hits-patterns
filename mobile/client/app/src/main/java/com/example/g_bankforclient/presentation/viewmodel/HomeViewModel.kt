package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.usecase.account.GetAccountsUseCase
import com.example.g_bankforclient.domain.usecase.account.GetHiddenAccountsUseCase
import com.example.g_bankforclient.domain.usecase.auth.LogoutUseCase
import com.example.g_bankforclient.presentation.state.HomeScreenState
import com.example.g_bankforclient.presentation.ui.utils.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getHiddenAccountsUseCase: GetHiddenAccountsUseCase,
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
            val current = _state.value as? HomeScreenState.Default
            _state.value = HomeScreenState.Default(
                accounts = current?.accounts ?: emptyList(),
                hiddenAccounts = current?.hiddenAccounts ?: emptyList(),
                showingHidden = current?.showingHidden ?: false,
                isLoading = true
            )
            runCatching { getAccountsUseCase() }
                .onSuccess { accounts ->
                    val cur = _state.value as? HomeScreenState.Default
                    _state.value = HomeScreenState.Default(
                        accounts = accounts,
                        hiddenAccounts = emptyList(),
                        showingHidden = cur?.showingHidden ?: false,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _state.value = HomeScreenState.Error(
                        message = e.toUserMessage("Не удалось загрузить счета")
                    )
                }
        }
    }

    fun toggleShowHidden() {
        val current = _state.value as? HomeScreenState.Default ?: return
        val newShowingHidden = !current.showingHidden
        _state.value = current.copy(showingHidden = newShowingHidden, isLoading = newShowingHidden)

        if (newShowingHidden) {
            viewModelScope.launch {
                runCatching { getHiddenAccountsUseCase() }
                    .onSuccess { hidden ->
                        val cur = _state.value as? HomeScreenState.Default ?: return@onSuccess
                        _state.value = cur.copy(hiddenAccounts = hidden, isLoading = false)
                    }
                    .onFailure { e ->
                        val cur = _state.value as? HomeScreenState.Default ?: return@onFailure
                        _state.value = cur.copy(
                            showingHidden = false,
                            isLoading = false,
                            errorMessage = e.toUserMessage("Не удалось загрузить скрытые счета")
                        )
                    }
            }
        }
    }

    fun clearError() {
        val cur = _state.value as? HomeScreenState.Default ?: return
        _state.value = cur.copy(errorMessage = null)
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
