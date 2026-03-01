package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.domain.usecase.account.CloseAccountUseCase
import com.example.g_bankforclient.domain.usecase.account.DepositUseCase
import com.example.g_bankforclient.domain.usecase.account.GetAccountDetailsUseCase
import com.example.g_bankforclient.domain.usecase.account.GetAccountTransactionsUseCase
import com.example.g_bankforclient.domain.usecase.account.WithdrawalUseCase
import com.example.g_bankforclient.presentation.state.AccountDetailsScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountDetailsViewModel @Inject constructor(
    private val getAccountDetailsUseCase: GetAccountDetailsUseCase,
    private val getAccountTransactionsUseCase: GetAccountTransactionsUseCase,
    private val depositUseCase: DepositUseCase,
    private val withdrawalUseCase: WithdrawalUseCase,
    private val closeAccountUseCase: CloseAccountUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<AccountDetailsScreenState> = MutableStateFlow(
        value = AccountDetailsScreenState.Default(
            account = Account("", "", 0.0),
            transactions = emptyList()
        )
    )
    val state: StateFlow<AccountDetailsScreenState> = _state.asStateFlow()

    fun loadAccountDetails(accountId: String) {
        viewModelScope.launch {
            _state.value = AccountDetailsScreenState.Loading
            runCatching {
                val account = getAccountDetailsUseCase(accountId)
                val transactions = getAccountTransactionsUseCase(accountId)
                Pair(account, transactions)
            }.onSuccess { (account, transactions) ->
                _state.value = AccountDetailsScreenState.Default(
                    account = account,
                    transactions = transactions
                )
            }.onFailure { e ->
                _state.value = AccountDetailsScreenState.Error(
                    message = e.message ?: "Не удалось загрузить данные счёта"
                )
            }
        }
    }

    fun deposit(accountId: String, amount: Double) {
        viewModelScope.launch {
            val currentState = _state.value as? AccountDetailsScreenState.Default ?: return@launch
            _state.value = currentState.copy(isLoading = true, errorMessage = null)

            runCatching { depositUseCase(accountId, amount) }
                .onSuccess {
                    delay(1000)
                    reloadSilently(accountId)
                }
                .onFailure { e ->
                    _state.value = currentState.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Ошибка при пополнении счёта"
                    )
                }
        }
    }

    fun withdrawal(accountId: String, amount: Double) {
        viewModelScope.launch {
            val currentState = _state.value as? AccountDetailsScreenState.Default ?: return@launch
            _state.value = currentState.copy(isLoading = true, errorMessage = null)

            runCatching { withdrawalUseCase(accountId, amount) }
                .onSuccess {
                    delay(1000)
                    reloadSilently(accountId)
                }
                .onFailure { e ->
                    _state.value = currentState.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Ошибка при снятии средств"
                    )
                }
        }
    }

    fun closeAccount(accountId: String) {
        viewModelScope.launch {
            val currentState = _state.value as? AccountDetailsScreenState.Default ?: return@launch
            _state.value = currentState.copy(isLoading = true, errorMessage = null)

            runCatching { closeAccountUseCase(accountId) }
                .onSuccess {
                    _state.value = currentState.copy(isLoading = false)
                }
                .onFailure { e ->
                    _state.value = currentState.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Ошибка при закрытии счёта"
                    )
                }
        }
    }

    fun clearError() {
        val currentState = _state.value as? AccountDetailsScreenState.Default ?: return
        _state.value = currentState.copy(errorMessage = null)
    }

    private suspend fun reloadSilently(accountId: String) {
        runCatching {
            val account = getAccountDetailsUseCase(accountId)
            val transactions = getAccountTransactionsUseCase(accountId)
            Pair(account, transactions)
        }.onSuccess { (account, transactions) ->
            val currentState = _state.value as? AccountDetailsScreenState.Default
            _state.value = (currentState ?: AccountDetailsScreenState.Default(
                account = account,
                transactions = transactions
            )).copy(
                account = account,
                transactions = transactions,
                isLoading = false,
                errorMessage = null
            )
        }.onFailure { e ->
            val currentState = _state.value as? AccountDetailsScreenState.Default
            if (currentState != null) {
                _state.value = currentState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Не удалось обновить данные"
                )
            }
        }
    }
}
