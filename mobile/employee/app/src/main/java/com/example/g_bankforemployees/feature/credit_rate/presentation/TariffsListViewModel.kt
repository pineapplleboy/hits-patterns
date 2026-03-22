package com.example.g_bankforemployees.feature.credit_rate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.feature.credit_rate.domain.usecase.GetCreditRatesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TariffsListViewModel(
    private val getCreditRatesUseCase: GetCreditRatesUseCase,
    private val navigatorHolder: com.example.g_bankforemployees.common.navigation.NavigatorHolder,
) : ViewModel() {

    private val _state: MutableStateFlow<TariffsListScreenState> = MutableStateFlow(TariffsListScreenState.Loading)
    val state: StateFlow<TariffsListScreenState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = TariffsListScreenState.Loading
            getCreditRatesUseCase()
                .onSuccess { rates ->
                    _state.value = TariffsListScreenState.Default(creditRates = rates)
                }
                .onFailure { e ->
                    _state.value = TariffsListScreenState.Error(
                        message = e.message?.takeUnless { it.isBlank() } ?: "РќРµ СѓРґР°Р»РѕСЃСЊ Р·Р°РіСЂСѓР·РёС‚СЊ С‚Р°СЂРёС„С‹",
                    )
                }
        }
    }

    fun onBackClick() {
        navigatorHolder.navigator?.navigateBack()
    }

    fun onCreateRateClick() {
        navigatorHolder.navigator?.navigateToCreditRateCreate()
    }
}



