package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.usecase.account.GetAccountsUseCase
import com.example.g_bankforclient.domain.usecase.credit.GetCreditsUseCase
import com.example.g_bankforclient.domain.usecase.credit.PayCreditUseCase
import com.example.g_bankforclient.presentation.state.CreditDetailsScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.g_bankforclient.common.models.Account

@HiltViewModel
class CreditDetailsViewModel @Inject constructor(
    private val getCreditsUseCase: GetCreditsUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val payCreditUseCase: PayCreditUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<CreditDetailsScreenState> = MutableStateFlow(
        value = CreditDetailsScreenState.Default(
            credit = com.example.g_bankforclient.common.models.Credit("", "", 0.0, 0.0, 0.0)
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
            try {
                getAccountsUseCase().collect { accounts ->
                    _accounts.value = accounts
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadCreditDetails(creditId: String) {
        viewModelScope.launch {
            _state.value = CreditDetailsScreenState.Loading
            try {
                getCreditsUseCase().collect { credits ->
                    val credit = credits.find { it.id == creditId }
                    if (credit != null) {
                        _state.value = CreditDetailsScreenState.Default(
                            credit = credit
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = CreditDetailsScreenState.Error(
                    message = e.message ?: "Failed to load credit details"
                )
            }
        }
    }

    fun payCredit(creditId: String, accountId: String, amount: Double) {
        viewModelScope.launch {
            try {
                payCreditUseCase(creditId, accountId, amount)
                // Reload credit details to reflect the new debt
                loadCreditDetails(creditId)
            } catch (e: Exception) {
                // In a real app, you'd want to show an error message to the user
                // For now, we'll just log it
            }
        }
    }
}
