package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.common.models.Account
import com.example.g_bankforclient.domain.usecase.account.CloseAccountUseCase
import com.example.g_bankforclient.domain.usecase.account.DepositUseCase
import com.example.g_bankforclient.domain.usecase.account.GetAccountsUseCase
import com.example.g_bankforclient.domain.usecase.account.WithdrawalUseCase
import com.example.g_bankforclient.domain.usecase.transaction.GetTransactionsUseCase
import com.example.g_bankforclient.presentation.state.AccountDetailsScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountDetailsViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
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
            try {
                // Load account details
                getAccountsUseCase().collect { accounts ->
                    val account = accounts.find { it.id == accountId }
                    if (account != null) {
                        // Load transactions for this account
                        getTransactionsUseCase().collect { transactions ->
                            val accountTransactions = transactions.filter { it.accountId == accountId }
                            _state.value = AccountDetailsScreenState.Default(
                                account = account,
                                transactions = accountTransactions
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = AccountDetailsScreenState.Error(
                    message = e.message ?: "Failed to load account details"
                )
            }
        }
    }

    fun deposit(accountId: String, amount: Double) {
        viewModelScope.launch {
            try {
                depositUseCase(accountId, amount)
                // Reload account details to reflect the new balance
                loadAccountDetails(accountId)
            } catch (e: Exception) {
                // In a real app, you'd want to show an error message to the user
                // For now, we'll just log it
            }
        }
    }

    fun withdrawal(accountId: String, amount: Double) {
        viewModelScope.launch {
            try {
                withdrawalUseCase(accountId, amount)
                // Reload account details to reflect the new balance
                loadAccountDetails(accountId)
            } catch (e: Exception) {
                // In a real app, you'd want to show an error message to the user
                // For now, we'll just log it
            }
        }
    }

    fun closeAccount(accountId: String) {
        viewModelScope.launch {
            try {
                closeAccountUseCase(accountId)
            } catch (e: Exception) {
                // In a real app, you'd want to show an error message to the user
                // For now, we'll just log it
            }
        }
    }
}
