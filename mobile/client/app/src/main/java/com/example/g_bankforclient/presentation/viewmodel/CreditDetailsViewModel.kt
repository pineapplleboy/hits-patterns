package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.TokenStorage
import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.domain.models.Credit
import com.example.g_bankforclient.domain.models.OperationPendingException
import com.example.g_bankforclient.domain.models.Transaction
import com.example.g_bankforclient.domain.models.TransactionType
import com.example.g_bankforclient.domain.models.UserRealtimeEvent
import com.example.g_bankforclient.domain.usecase.account.GetRubAccountsUseCase
import com.example.g_bankforclient.domain.usecase.credit.GetCreditDetailsUseCase
import com.example.g_bankforclient.domain.usecase.credit.PayCreditUseCase
import com.example.g_bankforclient.domain.usecase.realtime.ObserveUserRealtimeEventsUseCase
import com.example.g_bankforclient.presentation.state.CreditDetailsScreenState
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
class CreditDetailsViewModel @Inject constructor(
    private val getCreditDetailsUseCase: GetCreditDetailsUseCase,
    private val getAccountsUseCase: GetRubAccountsUseCase,
    private val payCreditUseCase: PayCreditUseCase,
    private val observeUserRealtimeEventsUseCase: ObserveUserRealtimeEventsUseCase,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _state: MutableStateFlow<CreditDetailsScreenState> = MutableStateFlow(
        value = CreditDetailsScreenState.Default(
            credit = Credit("", "", 0.0, 0.0, 0.0)
        )
    )
    val state: StateFlow<CreditDetailsScreenState> = _state.asStateFlow()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: List<Account> get() = _accounts.value

    private var realtimeJob: Job? = null

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
            realtimeJob?.cancel()
            realtimeJob = null
            runCatching { getCreditDetailsUseCase(creditId) }
                .onSuccess { credit ->
                    _state.value = CreditDetailsScreenState.Default(credit = credit)
                }
                .onFailure { e ->
                    _state.value = CreditDetailsScreenState.Error(
                        message = e.toUserMessage("Не удалось загрузить данные кредита")
                    )
                }
        }

        val userId = tokenStorage.getUserId()
        if (userId.isNullOrBlank()) return

        realtimeJob = viewModelScope.launch {
            observeUserRealtimeEventsUseCase(userId).collectLatest { event ->
                val current = _state.value as? CreditDetailsScreenState.Default
                    ?: return@collectLatest

                when (event) {
                    is UserRealtimeEvent.CreditAccountDeptUpdate -> {
                        if (event.accountNumber != null && event.accountNumber != creditId) {
                            return@collectLatest
                        }
                        val updatedCredit = current.credit.copy(debt = event.balance)
                        _state.value = current.copy(credit = updatedCredit)
                    }

                    is UserRealtimeEvent.OperationStatusUpdate -> {
                        val relevant = event.accountNumberFrom == creditId
                                || event.recipientAccountNumber == creditId
                        if (!relevant) return@collectLatest

                        val txAccountId = when {
                            event.accountNumberFrom == creditId -> event.accountNumberFrom
                            event.recipientAccountNumber == creditId -> event.recipientAccountNumber
                            else -> creditId
                        }

                        val updatedTx = Transaction(
                            id = event.operationId,
                            accountId = txAccountId ?: creditId,
                            type = TransactionType.CREDIT_PAYMENT,
                            amount = event.amount ?: 0.0,
                            date = event.createTime ?: Date(),
                            description = "Платёж по кредиту",
                            status = event.status,
                            fromAccount = event.accountNumberFrom,
                            toAccount = event.recipientAccountNumber
                        )

                        val updatedList = updateOrInsertTransaction(
                            current.credit.transactions, updatedTx
                        )
                        val updatedCredit = current.credit.copy(transactions = updatedList)
                        _state.value = current.copy(credit = updatedCredit)
                    }

                    is UserRealtimeEvent.OperationCreate -> {
                        val updatedList = current.credit.transactions.map { tx ->
                            if (tx.id == event.operationId) tx.copy(status = event.newStatus) else tx
                        }
                        if (updatedList != current.credit.transactions) {
                            _state.value = current.copy(
                                credit = current.credit.copy(transactions = updatedList)
                            )
                        }
                    }

                    else -> Unit
                }
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
                    delay(1000)
                    reloadSilently(creditId)
                }
                .onFailure { e ->
                    val cur = _state.value as? CreditDetailsScreenState.Default ?: return@onFailure
                    if (e is OperationPendingException) {
                        _state.value = cur.copy(isLoading = false, snackbarMessage = e.message)
                    } else {
                        _state.value = cur.copy(
                            isLoading = false,
                            errorMessage = e.toUserMessage("Ошибка при оплате кредита")
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

    fun clearSnackbar() {
        val currentState = _state.value as? CreditDetailsScreenState.Default ?: return
        _state.value = currentState.copy(snackbarMessage = null)
    }

    private suspend fun reloadSilently(creditId: String) {
        runCatching { getCreditDetailsUseCase(creditId) }
            .onSuccess { credit ->
                val currentState = _state.value as? CreditDetailsScreenState.Default
                _state.value = (currentState ?: CreditDetailsScreenState.Default(credit = credit))
                    .copy(credit = credit, isLoading = false, errorMessage = null)
            }
            .onFailure { e ->
                val currentState = _state.value as? CreditDetailsScreenState.Default
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
        val idx = transactions.indexOfFirst { it.id == tx.id }
        return if (idx >= 0) {
            val mutable = transactions.toMutableList()
            mutable[idx] = tx
            mutable
        } else {
            listOf(tx) + transactions
        }
    }
}
