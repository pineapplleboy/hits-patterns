package com.example.g_bankforemployees.feature.client_details.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.feature.client_details.domain.usecase.GetUserBankAccountsUseCase
import com.example.g_bankforemployees.feature.client_details.domain.usecase.GetUserCreditAccountsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.URLDecoder

class ClientDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getUserBankAccountsUseCase: GetUserBankAccountsUseCase,
    private val getUserCreditAccountsUseCase: GetUserCreditAccountsUseCase,
    private val navigatorHolder: NavigatorHolder,
) : ViewModel() {

    val userId: String = savedStateHandle.get<String>("userId").orEmpty()
    val userName: String = runCatching {
        URLDecoder.decode(savedStateHandle.get<String>("userName").orEmpty(), "UTF-8")
    }.getOrElse { "" }
    val userPhone: String = runCatching {
        URLDecoder.decode(savedStateHandle.get<String>("userPhone").orEmpty(), "UTF-8")
    }.getOrElse { "" }

    private val _bankAccounts = MutableStateFlow<List<BankAccount>>(emptyList())
    val bankAccounts: StateFlow<List<BankAccount>> = _bankAccounts.asStateFlow()

    private val _creditAccounts = MutableStateFlow<List<CreditAccount>>(emptyList())
    val creditAccounts: StateFlow<List<CreditAccount>> = _creditAccounts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    init {
        loadAccounts()
    }

    fun loadAccounts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            getUserBankAccountsUseCase(userId)
                .onSuccess { _bankAccounts.value = it }
                .onFailure {
                    if (it is HttpException && it.code() == 401) {
                        navigatorHolder.navigator?.navigateToAuthorizationAndClearStack()
                        return@launch
                    }
                    _errorMessage.value = it.message
                }
            getUserCreditAccountsUseCase(userId)
                .onSuccess { _creditAccounts.value = it }
                .onFailure {
                    if (it is HttpException && it.code() == 401) {
                        navigatorHolder.navigator?.navigateToAuthorizationAndClearStack()
                        return@launch
                    }
                    _errorMessage.value = it.message
                }
            _isLoading.value = false
        }
    }

    fun onSelectedTabIndexChange(index: Int) {
        _selectedTabIndex.value = index
    }

    fun onBackClick() {
        navigatorHolder.navigator?.navigateBack()
    }

    fun onAccountClick(accountNumber: String) {
        navigatorHolder.navigator?.navigateToAccountOperations(
            userId = userId,
            accountNumber = accountNumber,
            transferType = "BANK_ACCOUNT",
            userName = userName,
        )
    }

    fun onCreditAccountClick(accountNumber: String) {
        navigatorHolder.navigator?.navigateToAccountOperations(
            userId = userId,
            accountNumber = accountNumber,
            transferType = "CREDIT_ACCOUNT",
            userName = userName,
        )
    }
}
