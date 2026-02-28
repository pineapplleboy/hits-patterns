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

    private fun loadCreditsAndRates() {
        viewModelScope.launch {
            _state.value = CreditsScreenState.Loading
            try {
                // Load credit rates first
                val creditRates = getCreditRatesUseCase()
                
                // Then load credits
                getCreditsUseCase().collect { credits ->
                    _state.value = CreditsScreenState.Default(
                        credits = credits,
                        creditRates = creditRates
                    )
                }
            } catch (e: Exception) {
                _state.value = CreditsScreenState.Error(
                    message = e.message ?: "Failed to load data"
                )
            }
        }
    }
    
    fun takeCredit(rateId: java.util.UUID, sum: Double, bankAccountNum: String) {
        viewModelScope.launch {
            try {
                val success = takeCreditByRateUseCase(rateId, sum, bankAccountNum)
                if (success) {
                    // Reload data after successful credit taking
                    loadCreditsAndRates()
                }
                // TODO: Handle success/error state in UI
            } catch (e: Exception) {
                // TODO: Handle error state in UI
            }
        }
    }
}
