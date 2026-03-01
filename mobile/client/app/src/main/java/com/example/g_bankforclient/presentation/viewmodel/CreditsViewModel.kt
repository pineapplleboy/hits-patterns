package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.usecase.credit.GetCreditRatesUseCase
import com.example.g_bankforclient.domain.usecase.credit.GetCreditsUseCase
import com.example.g_bankforclient.domain.usecase.credit.TakeCreditByRateUseCase
import com.example.g_bankforclient.presentation.state.CreditsScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditsViewModel @Inject constructor(
    private val getCreditsUseCase: GetCreditsUseCase,
    private val getCreditRatesUseCase: GetCreditRatesUseCase,
    private val takeCreditByRateUseCase: TakeCreditByRateUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<CreditsScreenState> = MutableStateFlow(
        value = CreditsScreenState.Default(
            credits = emptyList(),
            creditRates = emptyList()
        )
    )
    val state: StateFlow<CreditsScreenState> = _state.asStateFlow()

    init {
        loadCreditsAndRates()
    }

    fun loadCreditsAndRates() {
        viewModelScope.launch {
            val currentState = _state.value as? CreditsScreenState.Default
            _state.value = CreditsScreenState.Default(
                credits = currentState?.credits ?: emptyList(),
                creditRates = currentState?.creditRates ?: emptyList(),
                isLoading = true
            )
            runCatching {
                val creditRates = getCreditRatesUseCase()
                val credits = getCreditsUseCase()
                Pair(credits, creditRates)
            }.onSuccess { (credits, creditRates) ->
                _state.value = CreditsScreenState.Default(
                    credits = credits,
                    creditRates = creditRates,
                    isLoading = false
                )
            }.onFailure { e ->
                _state.value = CreditsScreenState.Error(
                    message = e.message ?: "Не удалось загрузить данные"
                )
            }
        }
    }
    
    fun takeCredit(rateId: java.util.UUID, sum: Double, bankAccountNum: String) {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState is CreditsScreenState.Default) {
                _state.value = currentState.copy(isLoading = true, errorMessage = null)
            }
            runCatching { takeCreditByRateUseCase(rateId, sum, bankAccountNum) }
                .onSuccess { success ->
                    if (success) {
                        loadCreditsAndRates()
                    } else {
                        val state = _state.value
                        if (state is CreditsScreenState.Default) {
                            _state.value = state.copy(
                                isLoading = false,
                                errorMessage = "Не удалось оформить кредит"
                            )
                        }
                    }
                }
                .onFailure { e ->
                    val state = _state.value
                    if (state is CreditsScreenState.Default) {
                        _state.value = state.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Ошибка при оформлении кредита"
                        )
                    }
                }
        }
    }

    fun clearError() {
        val currentState = _state.value
        if (currentState is CreditsScreenState.Default) {
            _state.value = currentState.copy(errorMessage = null)
        }
    }
}
