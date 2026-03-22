package com.example.g_bankforemployees.feature.account_operations.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.GetExpiredOperationsUseCase
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.ObserveAccountOperationsUseCase
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.ObserveBankAccountDetailsUseCase
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.ObserveCreditAccountDetailsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.net.URLDecoder

private const val TRANSFER_TYPE_CREDIT = "CREDIT_ACCOUNT"

class AccountOperationsViewModel(
    savedStateHandle: SavedStateHandle,
    private val observeAccountOperationsUseCase: ObserveAccountOperationsUseCase,
    private val observeBankAccountDetailsUseCase: ObserveBankAccountDetailsUseCase,
    private val observeCreditAccountDetailsUseCase: ObserveCreditAccountDetailsUseCase,
    private val getExpiredOperationsUseCase: GetExpiredOperationsUseCase,
    private val navigatorHolder: NavigatorHolder,
) : ViewModel() {

    private val userId: String = savedStateHandle.get<String>("userId").orEmpty()
    private val accountNumber: String = savedStateHandle.get<String>("accountNumber").orEmpty()
    private val transferType: String = savedStateHandle.get<String>("transferType").orEmpty().ifEmpty { "BANK_ACCOUNT" }
    private val userName: String = runCatching {
        URLDecoder.decode(savedStateHandle.get<String>("userName").orEmpty(), "UTF-8")
    }.getOrElse { "" }

    private val _state = MutableStateFlow<AccountOperationsScreenState>(AccountOperationsScreenState.Loading)
    val state: StateFlow<AccountOperationsScreenState> = _state.asStateFlow()

    private var loadJob: Job? = null
    private var contentJob: Job? = null

    init {
        load()
    }

    override fun onCleared() {
        loadJob?.cancel()
        contentJob?.cancel()
        super.onCleared()
    }

    fun load(showLoading: Boolean = true) {
        loadJob?.cancel()
        contentJob?.cancel()
        loadJob = viewModelScope.launch {
            val previousState = _state.value as? AccountOperationsScreenState.Default
            if (showLoading || previousState == null) {
                _state.value = AccountOperationsScreenState.Loading
            }

            val operationsFlow = observeAccountOperationsUseCase(userId, accountNumber, transferType)
                .getOrElse { error ->
                    _state.value = buildErrorState(
                        previousState = previousState,
                        details = AccountDetails(),
                        operationsErrorMessage = error.message?.takeUnless { it.isBlank() } ?: "Не удалось загрузить операции",
                    )
                    return@launch
                }

            if (transferType == TRANSFER_TYPE_CREDIT) {
                observeCreditScreen(previousState, operationsFlow)
            } else {
                observeBankScreen(previousState, operationsFlow)
            }
        }
    }

    fun onBackClick() {
        navigatorHolder.navigator?.navigateBack()
    }

    private suspend fun observeBankScreen(
        previousState: AccountOperationsScreenState.Default?,
        operationsFlow: Flow<List<Operation>>,
    ) {
        var detailsWarning: String? = null
        val bankAccountFlow = observeBankAccountDetailsUseCase(userId, accountNumber)
            .getOrElse { error ->
                detailsWarning = error.message?.takeUnless { it.isBlank() } ?: "Не удалось загрузить детали счета"
                null
            }

        val bankAccountStateFlow: Flow<BankAccount?> = bankAccountFlow
            ?.map<BankAccount, BankAccount?> { it }
            ?: flowOf(previousState?.bankAccount)

        contentJob = combine(bankAccountStateFlow, operationsFlow) { bankAccount, operations ->
            AccountOperationsScreenState.Default(
                accountNumber = accountNumber,
                userName = userName,
                transferType = transferType,
                bankAccount = bankAccount,
                creditAccount = null,
                isCreditExpired = null,
                operations = operations,
                warningMessage = detailsWarning,
            )
        }.collectIntoState()
    }

    private suspend fun observeCreditScreen(
        previousState: AccountOperationsScreenState.Default?,
        operationsFlow: Flow<List<Operation>>,
    ) {
        var warningMessage: String? = null
        val creditAccountFlow = observeCreditAccountDetailsUseCase(userId, accountNumber)
            .getOrElse { error ->
                warningMessage = appendWarning(
                    warningMessage,
                    error.message?.takeUnless { it.isBlank() } ?: "Не удалось загрузить детали счета",
                )
                null
            }

        val isCreditExpired = getExpiredOperationsUseCase(userId)
            .onFailure {
                warningMessage = appendWarning(
                    warningMessage,
                    it.message?.takeUnless { it.isBlank() } ?: "Не удалось загрузить статус просрочки",
                )
            }
            .getOrNull()
            ?.any { operation ->
                operation.recipientAccountNumber == accountNumber ||
                    operation.accountNumberFrom == accountNumber
            }
            ?: previousState?.isCreditExpired

        val creditAccountStateFlow: Flow<CreditAccount?> = creditAccountFlow
            ?.map<CreditAccount, CreditAccount?> { it }
            ?: flowOf(previousState?.creditAccount)

        contentJob = combine(creditAccountStateFlow, operationsFlow) { creditAccount, operations ->
            AccountOperationsScreenState.Default(
                accountNumber = accountNumber,
                userName = userName,
                transferType = transferType,
                bankAccount = null,
                creditAccount = creditAccount,
                isCreditExpired = isCreditExpired,
                operations = operations,
                warningMessage = warningMessage,
            )
        }.collectIntoState()
    }

    private fun Flow<AccountOperationsScreenState.Default>.collectIntoState(): Job =
        viewModelScope.launch {
            collect { _state.value = it }
        }

    private fun buildErrorState(
        previousState: AccountOperationsScreenState.Default?,
        details: AccountDetails,
        operationsErrorMessage: String,
    ): AccountOperationsScreenState = when {
        details.bankAccount != null || details.creditAccount != null -> AccountOperationsScreenState.Default(
            accountNumber = accountNumber,
            userName = userName,
            transferType = transferType,
            bankAccount = details.bankAccount,
            creditAccount = details.creditAccount,
            isCreditExpired = details.isCreditExpired,
            operations = emptyList(),
            warningMessage = appendWarning(
                appendWarning(details.warningMessage, details.detailsErrorMessage),
                operationsErrorMessage,
            ),
        )

        previousState != null -> previousState.copy(
            warningMessage = appendWarning(
                appendWarning(previousState.warningMessage, details.detailsErrorMessage),
                operationsErrorMessage,
            ),
        )

        else -> AccountOperationsScreenState.Error(
            message = details.detailsErrorMessage ?: operationsErrorMessage,
        )
    }

    private fun appendWarning(
        current: String?,
        addition: String?,
    ): String? {
        if (addition.isNullOrBlank()) return current
        if (current.isNullOrBlank()) return addition
        return "$current\n$addition"
    }

    private data class AccountDetails(
        val bankAccount: BankAccount? = null,
        val creditAccount: CreditAccount? = null,
        val isCreditExpired: Boolean? = null,
        val warningMessage: String? = null,
        val detailsErrorMessage: String? = null,
    )
}


