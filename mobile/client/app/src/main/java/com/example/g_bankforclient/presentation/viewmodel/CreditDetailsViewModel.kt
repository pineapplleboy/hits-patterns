package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.domain.models.Credit
import com.example.g_bankforclient.domain.usecase.account.GetAccountsUseCase
import com.example.g_bankforclient.domain.usecase.credit.GetCreditDetailsUseCase
import com.example.g_bankforclient.domain.usecase.credit.PayCreditUseCase
import com.example.g_bankforclient.presentation.state.CreditDetailsScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditDetailsViewModel @Inject constructor(
    private val getCreditDetailsUseCase: GetCreditDetailsUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val payCreditUseCase: PayCreditUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<CreditDetailsScreenState> = MutableStateFlow(
        value = CreditDetailsScreenState.Default(
            credit = Credit("", "", 0.0, 0.0, 0.0)
        )
    )
    val state: StateFlow<CreditDetailsScreenState> = _state.asStateFlow()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: List<Account> get() = _accounts.value

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            runCatching { getAccountsUseCase() }
                .onSuccess { accounts -> _accounts.value = accounts }
        }
    }

    fun loadCreditDetails(creditId: String) {
        viewModelScope.launch {
            _state.value = CreditDetailsScreenState.Loading
            runCatching { getCreditDetailsUseCase(creditId) }
                .onSuccess { credit ->
                    _state.value = CreditDetailsScreenState.Default(credit = credit)
                }
                .onFailure { e ->
                    _state.value = CreditDetailsScreenState.Error(
                        message = e.message ?: "Не удалось загрузить данные кредита"
                    )
                }
        }
    }

    fun payCredit(creditId: String, accountId: String, amount: Double) {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState is CreditDetailsScreenState.Default) {
                _state.value = currentState.copy(isLoading = true, errorMessage = null)
            }
            runCatching { payCreditUseCase(creditId, accountId, amount) }
                .onSuccess {
                    loadCreditDetails(creditId)
                }
                .onFailure { e ->
                    val state = _state.value
                    if (state is CreditDetailsScreenState.Default) {
                        _state.value = state.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Ошибка при оплате кредита"
                        )
                    }
                }
        }
    }

    fun clearError() {
        val currentState = _state.value
        if (currentState is CreditDetailsScreenState.Default) {
            _state.value = currentState.copy(errorMessage = null)
        }
    }
}
