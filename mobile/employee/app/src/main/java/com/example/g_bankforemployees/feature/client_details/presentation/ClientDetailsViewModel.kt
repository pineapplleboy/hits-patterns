package com.example.g_bankforemployees.feature.client_details.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.feature.client_details.domain.usecase.GetUserBankAccountsUseCase
import com.example.g_bankforemployees.feature.client_details.domain.usecase.GetUserCreditAccountsUseCase
import com.example.g_bankforemployees.feature.users_list.domain.model.User
import com.example.g_bankforemployees.feature.users_list.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder

class ClientDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getUserBankAccountsUseCase: GetUserBankAccountsUseCase,
    private val getUserCreditAccountsUseCase: GetUserCreditAccountsUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val navigatorHolder: NavigatorHolder,
) : ViewModel() {

    val userId: String = savedStateHandle.get<String>("userId").orEmpty()
    private val initialUserName: String = runCatching {
        URLDecoder.decode(savedStateHandle.get<String>("userName").orEmpty(), "UTF-8")
    }.getOrElse { "" }
    private val initialUserPhone: String = runCatching {
        URLDecoder.decode(savedStateHandle.get<String>("userPhone").orEmpty(), "UTF-8")
    }.getOrElse { "" }

    private val _state: MutableStateFlow<ClientDetailsScreenState> =
        MutableStateFlow(ClientDetailsScreenState.Loading)
    val state: StateFlow<ClientDetailsScreenState> = _state.asStateFlow()

    init {
        loadAccounts()
    }

    fun loadAccounts(showLoading: Boolean = true) {
        viewModelScope.launch {
            val previousState = _state.value as? ClientDetailsScreenState.Default
            val selectedTabIndex = previousState?.selectedTabIndex ?: 0

            if (showLoading || previousState == null) {
                _state.value = ClientDetailsScreenState.Loading
            }

            val actualUser = resolveActualUser()

            val bankAccounts = getUserBankAccountsUseCase(userId)
                .getOrElse { error ->
                    if (previousState != null && !showLoading) {
                        _state.value = previousState
                    } else {
                        _state.value = ClientDetailsScreenState.Error(
                            message = error.message?.takeUnless { it.isBlank() } ?: "Не удалось загрузить счета",
                        )
                    }
                    return@launch
                }

            val creditAccounts = getUserCreditAccountsUseCase(userId)
                .getOrElse { error ->
                    if (previousState != null && !showLoading) {
                        _state.value = previousState.copy(bankAccounts = bankAccounts)
                    } else {
                        _state.value = ClientDetailsScreenState.Error(
                            message = error.message?.takeUnless { it.isBlank() } ?: "Не удалось загрузить кредиты",
                        )
                    }
                    return@launch
                }

            _state.value = ClientDetailsScreenState.Default(
                userName = actualUser?.name ?: initialUserName,
                userPhone = actualUser?.phone ?: initialUserPhone,
                selectedTabIndex = selectedTabIndex,
                bankAccounts = bankAccounts,
                creditAccounts = creditAccounts,
            )
        }
    }

    fun onSelectedTabIndexChange(index: Int) {
        val current = _state.value
        if (current !is ClientDetailsScreenState.Default) return
        _state.value = current.copy(selectedTabIndex = index)
    }

    fun onBackClick() {
        navigatorHolder.navigator?.navigateBack()
    }

    fun onCreditHistoryClick() {
        val userName = (state.value as? ClientDetailsScreenState.Default)?.userName ?: initialUserName
        navigatorHolder.navigator?.navigateToCreditHistory(
            userId = userId,
            userName = userName,
        )
    }

    fun onAccountClick(accountNumber: String) {
        val userName = (state.value as? ClientDetailsScreenState.Default)?.userName ?: initialUserName
        navigatorHolder.navigator?.navigateToAccountOperations(
            userId = userId,
            accountNumber = accountNumber,
            transferType = "BANK_ACCOUNT",
            userName = userName,
        )
    }

    fun onCreditAccountClick(accountNumber: String) {
        val userName = (state.value as? ClientDetailsScreenState.Default)?.userName ?: initialUserName
        navigatorHolder.navigator?.navigateToAccountOperations(
            userId = userId,
            accountNumber = accountNumber,
            transferType = "CREDIT_ACCOUNT",
            userName = userName,
        )
    }

    private suspend fun resolveActualUser(): User? =
        getUsersUseCase()
            .getOrNull()
            ?.firstOrNull { it.id == userId }
}


