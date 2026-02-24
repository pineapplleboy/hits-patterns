package com.example.g_bankforclient.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.common.models.Account
import com.example.g_bankforclient.common.models.Credit
import com.example.g_bankforclient.common.models.Transaction
import com.example.g_bankforclient.domain.usecase.account.CloseAccountUseCase
import com.example.g_bankforclient.domain.usecase.account.CreateAccountUseCase
import com.example.g_bankforclient.domain.usecase.account.DepositUseCase
import com.example.g_bankforclient.domain.usecase.account.GetAccountsUseCase
import com.example.g_bankforclient.domain.usecase.account.WithdrawalUseCase
import com.example.g_bankforclient.domain.usecase.credit.CreateCreditUseCase
import com.example.g_bankforclient.domain.usecase.credit.GetCreditsUseCase
import com.example.g_bankforclient.domain.usecase.credit.PayCreditUseCase
import com.example.g_bankforclient.domain.usecase.transaction.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BankViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
    private val closeAccountUseCase: CloseAccountUseCase,
    private val depositUseCase: DepositUseCase,
    private val withdrawalUseCase: WithdrawalUseCase,
    private val getCreditsUseCase: GetCreditsUseCase,
    private val createCreditUseCase: CreateCreditUseCase,
    private val payCreditUseCase: PayCreditUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    var accounts by mutableStateOf<List<Account>>(emptyList())
        private set

    var credits by mutableStateOf<List<Credit>>(emptyList())
        private set

    var transactions by mutableStateOf<List<Transaction>>(emptyList())
        private set

    init {
        observeAccounts()
        observeCredits()
        observeTransactions()
    }

    private fun observeAccounts() {
        viewModelScope.launch {
            getAccountsUseCase().collectLatest { accountsList ->
                accounts = accountsList
            }
        }
    }

    private fun observeCredits() {
        viewModelScope.launch {
            getCreditsUseCase().collectLatest { creditsList ->
                credits = creditsList
            }
        }
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            getTransactionsUseCase().collectLatest { transactionsList ->
                transactions = transactionsList
            }
        }
    }

    fun createAccount(name: String) {
        viewModelScope.launch {
            createAccountUseCase(name)
        }
    }

    fun closeAccount(accountId: String) {
        viewModelScope.launch {
            closeAccountUseCase(accountId)
        }
    }

    fun deposit(accountId: String, amount: Double) {
        viewModelScope.launch {
            depositUseCase(accountId, amount)
        }
    }

    fun withdrawal(accountId: String, amount: Double) {
        viewModelScope.launch {
            withdrawalUseCase(accountId, amount)
        }
    }

    fun createCredit(name: String, amount: Double, interestRate: Double) {
        viewModelScope.launch {
            createCreditUseCase(name, amount, interestRate)
        }
    }

    fun payCredit(creditId: String, accountId: String, amount: Double) {
        viewModelScope.launch {
            payCreditUseCase(creditId, accountId, amount)
        }
    }
}
