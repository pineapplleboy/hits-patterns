package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.usecase.account.GetAccountsUseCase
import com.example.g_bankforclient.presentation.state.HomeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<HomeScreenState> = MutableStateFlow(
        value = HomeScreenState.Default(
            accounts = emptyList()
        )
    )
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            _state.value = HomeScreenState.Loading
            try {
                getAccountsUseCase().collect { accounts ->
                    _state.value = HomeScreenState.Default(
                        accounts = accounts
                    )
                }
            } catch (e: Exception) {
                _state.value = HomeScreenState.Error(
                    message = e.message ?: "Failed to load accounts"
                )
            }
        }
    }
}
