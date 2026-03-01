package com.example.g_bankforemployees.feature.account_operations.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.GetAccountOperationsUseCase
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.GetBankAccountDetailsUseCase
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.GetCreditAccountDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.URLDecoder

private const val TRANSFER_TYPE_CREDIT = "CREDIT_ACCOUNT"

class AccountOperationsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getAccountOperationsUseCase: GetAccountOperationsUseCase,
    private val getBankAccountDetailsUseCase: GetBankAccountDetailsUseCase,
    private val getCreditAccountDetailsUseCase: GetCreditAccountDetailsUseCase,
    private val navigatorHolder: NavigatorHolder,
) : ViewModel() {

    val userId: String = savedStateHandle.get<String>("userId").orEmpty()
    val accountNumber: String = savedStateHandle.get<String>("accountNumber").orEmpty()
    val transferType: String = savedStateHandle.get<String>("transferType").orEmpty().ifEmpty { "BANK_ACCOUNT" }
    val userName: String = runCatching {
        URLDecoder.decode(savedStateHandle.get<String>("userName").orEmpty(), "UTF-8")
    }.getOrElse { "" }

    private val _operations = MutableStateFlow<List<Operation>>(emptyList())
    val operations: StateFlow<List<Operation>> = _operations.asStateFlow()

    private val _bankAccount = MutableStateFlow<BankAccount?>(null)
    val bankAccount: StateFlow<BankAccount?> = _bankAccount.asStateFlow()

    private val _creditAccount = MutableStateFlow<CreditAccount?>(null)
    val creditAccount: StateFlow<CreditAccount?> = _creditAccount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _bankAccount.value = null
            _creditAccount.value = null
            when (transferType) {
                TRANSFER_TYPE_CREDIT -> getCreditAccountDetailsUseCase(userId, accountNumber)
                    .onSuccess { _creditAccount.value = it }
                    .onFailure {
                        if (it is HttpException && it.code() == 401) {
                            navigatorHolder.navigator?.navigateToAuthorizationAndClearStack()
                            return@launch
                        }
                        _errorMessage.value = it.message
                    }
                else -> getBankAccountDetailsUseCase(userId, accountNumber)
                    .onSuccess { _bankAccount.value = it }
                    .onFailure {
                        if (it is HttpException && it.code() == 401) {
                            navigatorHolder.navigator?.navigateToAuthorizationAndClearStack()
                            return@launch
                        }
                        _errorMessage.value = it.message
                    }
            }
            getAccountOperationsUseCase(userId, accountNumber, transferType)
                .onSuccess { _operations.value = it }
                .onFailure {
                    if (it is HttpException && it.code() == 401) {
                        navigatorHolder.navigator?.navigateToAuthorizationAndClearStack()
                        return@launch
                    }
                    if (_errorMessage.value == null) _errorMessage.value = it.message
                }
            _isLoading.value = false
        }
    }

    fun onBackClick() {
        navigatorHolder.navigator?.navigateBack()
    }
}
