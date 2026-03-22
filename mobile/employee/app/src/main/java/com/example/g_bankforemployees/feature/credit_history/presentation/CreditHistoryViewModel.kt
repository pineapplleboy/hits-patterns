package com.example.g_bankforemployees.feature.credit_history.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.GetExpiredOperationsUseCase
import com.example.g_bankforemployees.feature.credit_history.domain.model.CreditRating
import com.example.g_bankforemployees.feature.credit_history.domain.usecase.GetUserCreditRatingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder

class CreditHistoryViewModel(
    savedStateHandle: SavedStateHandle,
    private val getUserCreditRatingUseCase: GetUserCreditRatingUseCase,
    private val getExpiredOperationsUseCase: GetExpiredOperationsUseCase,
    private val navigatorHolder: NavigatorHolder,
) : ViewModel() {

    private val userId: String = savedStateHandle.get<String>("userId").orEmpty()
    private val initialUserName: String = runCatching {
        URLDecoder.decode(savedStateHandle.get<String>("userName").orEmpty(), "UTF-8")
    }.getOrElse { "" }

    private val _state = MutableStateFlow<CreditHistoryScreenState>(CreditHistoryScreenState.Loading)
    val state: StateFlow<CreditHistoryScreenState> = _state.asStateFlow()

    init {
        load()
    }

    fun load(showLoading: Boolean = true) {
        viewModelScope.launch {
            val previousState = _state.value as? CreditHistoryScreenState.Default
            if (showLoading || previousState == null) {
                _state.value = CreditHistoryScreenState.Loading
            }

            val creditRating = getUserCreditRatingUseCase(userId)
                .getOrElse { error ->
                    _state.value = buildErrorState(
                        previousState = previousState,
                        ratingErrorMessage = error.message?.takeUnless { it.isBlank() } ?: "Не удалось загрузить кредитный рейтинг",
                    )
                    return@launch
                }

            val expiredOperations = getExpiredOperationsUseCase(userId)
                .getOrElse { error ->
                    _state.value = buildErrorState(
                        previousState = previousState,
                        creditRating = creditRating,
                        operationsErrorMessage = error.message?.takeUnless { it.isBlank() } ?: "Не удалось загрузить просроченные операции",
                    )
                    return@launch
                }

            _state.value = CreditHistoryScreenState.Default(
                userName = initialUserName,
                creditRating = creditRating,
                expiredOperations = expiredOperations,
            )
        }
    }

    fun onBackClick() {
        navigatorHolder.navigator?.navigateBack()
    }

    private fun buildErrorState(
        previousState: CreditHistoryScreenState.Default?,
        creditRating: CreditRating? = null,
        ratingErrorMessage: String? = null,
        operationsErrorMessage: String? = null,
    ): CreditHistoryScreenState = when {
        previousState != null && creditRating != null && operationsErrorMessage != null -> previousState.copy(
            creditRating = creditRating,
            warningMessage = operationsErrorMessage,
        )

        previousState != null && ratingErrorMessage != null -> previousState.copy(
            warningMessage = ratingErrorMessage,
        )

        else -> CreditHistoryScreenState.Error(
            message = ratingErrorMessage ?: operationsErrorMessage.orEmpty(),
        )
    }
}


