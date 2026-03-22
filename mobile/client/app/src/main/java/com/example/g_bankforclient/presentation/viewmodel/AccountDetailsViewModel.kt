package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.TokenStorage
import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.domain.models.OperationPendingException
import com.example.g_bankforclient.domain.models.Transaction
import com.example.g_bankforclient.domain.models.TransactionType
import com.example.g_bankforclient.domain.models.UserRealtimeEvent
import com.example.g_bankforclient.domain.usecase.account.CloseAccountUseCase
import com.example.g_bankforclient.domain.usecase.account.DepositUseCase
import com.example.g_bankforclient.domain.usecase.account.GetAccountDetailsUseCase
import com.example.g_bankforclient.domain.usecase.account.GetAccountTransactionsUseCase
import com.example.g_bankforclient.domain.usecase.account.GetAccountsUseCase
import com.example.g_bankforclient.domain.usecase.account.TransferToBankAccountUseCase
import com.example.g_bankforclient.domain.usecase.account.WithdrawalUseCase
import com.example.g_bankforclient.domain.usecase.exchange.GetExchangeRatesUseCase
import com.example.g_bankforclient.domain.usecase.realtime.ObserveUserRealtimeEventsUseCase
import com.example.g_bankforclient.domain.usecase.settings.ToggleAccountVisibilityUseCase
import com.example.g_bankforclient.presentation.state.AccountDetailsScreenState
import com.example.g_bankforclient.presentation.ui.utils.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AccountDetailsViewModel @Inject constructor(
    private val getAccountDetailsUseCase: GetAccountDetailsUseCase,
    private val getAccountTransactionsUseCase: GetAccountTransactionsUseCase,
    private val depositUseCase: DepositUseCase,
    private val withdrawalUseCase: WithdrawalUseCase,
    private val closeAccountUseCase: CloseAccountUseCase,
    private val transferToBankAccountUseCase: TransferToBankAccountUseCase,
    private val toggleAccountVisibilityUseCase: ToggleAccountVisibilityUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getExchangeRatesUseCase: GetExchangeRatesUseCase,
    private val tokenStorage: TokenStorage,
    private val observeUserRealtimeEventsUseCase: ObserveUserRealtimeEventsUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<AccountDetailsScreenState> = MutableStateFlow(
        value = AccountDetailsScreenState.Default(
            account = Account(id = "", name = "", balance = 0.0),
            transactions = emptyList()
        )
    )
    val state: StateFlow<AccountDetailsScreenState> = _state.asStateFlow()

    private var realtimeJob: Job? = null

    fun loadAccountDetails(accountId: String, isHidden: Boolean = false) {
        viewModelScope.launch {
            _state.value = AccountDetailsScreenState.Loading
            realtimeJob?.cancel()
            realtimeJob = null
            runCatching {
                val account = getAccountDetailsUseCase(accountId)
                val transactions = getAccountTransactionsUseCase(accountId)
                val myAccounts = runCatching { getAccountsUseCase() }.getOrDefault(emptyList())
                val rates = runCatching { getExchangeRatesUseCase() }.getOrDefault(emptyMap())
                Triple(Pair(account, transactions), myAccounts, rates)
            }.onSuccess { (accountAndTx, myAccounts, rates) ->
                val (account, transactions) = accountAndTx
                _state.value = AccountDetailsScreenState.Default(
                    account = account.copy(isHidden = isHidden),
                    transactions = transactions,
                    myAccounts = myAccounts.filter { it.id != accountId },
                    exchangeRates = rates
                )
            }.onFailure { e ->
                _state.value = AccountDetailsScreenState.Error(
                    message = e.toUserMessage("Не удалось загрузить данные счёта")
                )
            }
        }

        val userId = tokenStorage.getUserId()
        if (userId.isNullOrBlank()) return

        realtimeJob = viewModelScope.launch {
            observeUserRealtimeEventsUseCase(userId).collectLatest { event ->
                val current =
                    _state.value as? AccountDetailsScreenState.Default ?: return@collectLatest

                when (event) {
                    is UserRealtimeEvent.OperationStatusUpdate -> {
                        val relevant =
                            event.accountNumberFrom == accountId || event.recipientAccountNumber == accountId
                        if (!relevant) return@collectLatest

                        val txAccountId = when {
                            event.accountNumberFrom == accountId -> event.accountNumberFrom
                            event.recipientAccountNumber == accountId -> event.recipientAccountNumber
                            else -> accountId
                        }

                        val updatedTx = Transaction(
                            id = event.operationId,
                            accountId = txAccountId ?: accountId,
                            type = mapActionTypeToTransactionType(event.actionType),
                            amount = event.amount ?: 0.0,
                            date = event.createTime ?: Date(),
                            description = event.actionType ?: "Операция",
                            status = event.status,
                            fromAccount = event.accountNumberFrom,
                            toAccount = event.recipientAccountNumber
                        )

                        val updatedList = updateOrInsertTransaction(current.transactions, updatedTx)
                        _state.value = current.copy(transactions = updatedList)
                    }

                    is UserRealtimeEvent.OperationCreate -> {
                        val updatedList = current.transactions.map { tx ->
                            if (tx.id == event.operationId) tx.copy(status = event.newStatus) else tx
                        }
                        if (updatedList != current.transactions) {
                            _state.value = current.copy(transactions = updatedList)
                        }
                    }

                    is UserRealtimeEvent.BankAccountSumUpdate -> {
                        if (event.accountNumber != null && event.accountNumber != accountId) return@collectLatest
                        val updatedAccount = current.account.copy(balance = event.balance)
                        _state.value = current.copy(account = updatedAccount)
                    }

                    is UserRealtimeEvent.CreditAccountDeptUpdate -> Unit
                }
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
                    val cur = _state.value as? AccountDetailsScreenState.Default ?: return@onFailure
                    if (e is OperationPendingException) {
                        _state.value = cur.copy(isLoading = false, snackbarMessage = e.message)
                    } else {
                        _state.value = cur.copy(
                            isLoading = false,
                            errorMessage = e.toUserMessage("Ошибка при пополнении счёта")
                        )
                    }
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
                    val cur = _state.value as? AccountDetailsScreenState.Default ?: return@onFailure
                    if (e is OperationPendingException) {
                        _state.value = cur.copy(isLoading = false, snackbarMessage = e.message)
                    } else {
                        _state.value = cur.copy(
                            isLoading = false,
                            errorMessage = e.toUserMessage("Ошибка при снятии средств")
                        )
                    }
                }
        }
    }

    fun transfer(fromAccountId: String, toAccountId: String, amount: Double) {
        viewModelScope.launch {
            val currentState = _state.value as? AccountDetailsScreenState.Default ?: return@launch
            _state.value = currentState.copy(isLoading = true, errorMessage = null)

            runCatching { transferToBankAccountUseCase(fromAccountId, toAccountId, amount) }
                .onSuccess {
                    delay(1000)
                    reloadSilently(fromAccountId)
                }
                .onFailure { e ->
                    val cur = _state.value as? AccountDetailsScreenState.Default ?: return@onFailure
                    if (e is OperationPendingException) {
                        _state.value = cur.copy(isLoading = false, snackbarMessage = e.message)
                    } else {
                        _state.value = cur.copy(
                            isLoading = false,
                            errorMessage = e.toUserMessage("Ошибка при переводе")
                        )
                    }
                }
        }
    }

    fun toggleVisibility(accountUuid: String) {
        viewModelScope.launch {
            val currentState = _state.value as? AccountDetailsScreenState.Default ?: return@launch
            _state.value = currentState.copy(isLoading = true, errorMessage = null)

            runCatching { toggleAccountVisibilityUseCase(accountUuid) }
                .onSuccess {
                    val updatedAccount = currentState.account.copy(
                        isHidden = !currentState.account.isHidden
                    )
                    _state.value = currentState.copy(account = updatedAccount, isLoading = false)
                }
                .onFailure { e ->
                    _state.value = currentState.copy(
                        isLoading = false,
                        errorMessage = e.toUserMessage("Ошибка при изменении видимости счёта")
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
                        errorMessage = e.toUserMessage("Ошибка при закрытии счёта")
                    )
                }
        }
    }

    fun clearError() {
        val currentState = _state.value as? AccountDetailsScreenState.Default ?: return
        _state.value = currentState.copy(errorMessage = null)
    }

    fun clearSnackbar() {
        val currentState = _state.value as? AccountDetailsScreenState.Default ?: return
        _state.value = currentState.copy(snackbarMessage = null)
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
                    errorMessage = e.toUserMessage("Не удалось обновить данные")
                )
            }
        }
    }

    private fun updateOrInsertTransaction(
        transactions: List<Transaction>,
        tx: Transaction
    ): List<Transaction> {
        val idx = transactions.indexOfFirst { it.id == tx.id && it.accountId == tx.accountId }
        return if (idx >= 0) {
            val mutable = transactions.toMutableList()
            mutable[idx] = tx
            mutable
        } else {
            listOf(tx) + transactions
        }
    }

    private fun mapActionTypeToTransactionType(actionType: String?): TransactionType {
        val a = actionType ?: return TransactionType.INFO
        return when {
            a.contains("TRANSFER_RECEIVED", ignoreCase = true) -> TransactionType.DEPOSIT
            a.contains("TRANSFER_SENT", ignoreCase = true) -> TransactionType.WITHDRAWAL
            else -> TransactionType.INFO
        }
    }
}
