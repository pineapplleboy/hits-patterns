package com.example.g_bankforemployees.feature.credit_rate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.feature.credit_rate.domain.model.CreditRateInput
import com.example.g_bankforemployees.feature.credit_rate.domain.usecase.CreateCreditRateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreditRateCreateScreenViewModel(
    private val createCreditRateUseCase: CreateCreditRateUseCase,
    private val navigatorHolder: com.example.g_bankforemployees.common.navigation.NavigatorHolder,
) : ViewModel() {

    private val _state = MutableStateFlow<CreditRateCreateScreenState>(
        CreditRateCreateScreenState.Default()
    )
    val state: StateFlow<CreditRateCreateScreenState> = _state.asStateFlow()

    fun onNameChange(name: String) {
        val current = _state.value
        if (current !is CreditRateCreateScreenState.Default) return
        _state.value = current.copy(name = name)
    }

    fun onPercentChange(percent: String) {
        val current = _state.value
        if (current !is CreditRateCreateScreenState.Default) return
        _state.value = current.copy(percent = percent)
    }

    fun onDaysChange(days: String) {
        val current = _state.value
        if (current !is CreditRateCreateScreenState.Default) return
        _state.value = current.copy(days = days.filter { ch -> ch.isDigit() })
    }

    fun onHoursChange(hours: String) {
        val current = _state.value
        if (current !is CreditRateCreateScreenState.Default) return
        _state.value = current.copy(hours = hours.filter { ch -> ch.isDigit() })
    }

    fun onMinutesChange(minutes: String) {
        val current = _state.value
        if (current !is CreditRateCreateScreenState.Default) return
        _state.value = current.copy(minutes = minutes.filter { ch -> ch.isDigit() })
    }

    fun createRate() {
        val current = _state.value
        if (current !is CreditRateCreateScreenState.Default) return
        val name = current.name.trim()
        val percent = current.percent.toIntOrNull()
        val days = current.days.toIntOrNull() ?: 0
        val hours = current.hours.toIntOrNull() ?: 0
        val minutes = current.minutes.toIntOrNull() ?: 0
        if (name.isBlank() || percent == null || percent !in 0..100) {
            _state.value = CreditRateCreateScreenState.Error(
                message = "Р—Р°РїРѕР»РЅРёС‚Рµ РЅР°Р·РІР°РЅРёРµ Рё СЃС‚Р°РІРєСѓ (0вЂ“100%)",
            )
            return
        }
        if (days == 0 && hours == 0 && minutes == 0) {
            _state.value = CreditRateCreateScreenState.Error(
                message = "Р—Р°РґР°Р№С‚Рµ РїРµСЂРёРѕРґ СЃРїРёСЃР°РЅРёСЏ (РґРЅРё, С‡Р°СЃС‹ РёР»Рё РјРёРЅСѓС‚С‹)",
            )
            return
        }
        val writeOffPeriod = "${days}d${hours}h${minutes}m"
        viewModelScope.launch {
            _state.value = CreditRateCreateScreenState.Loading
            createCreditRateUseCase(
                CreditRateInput(
                    name = name,
                    percent = percent,
                    writeOffPeriod = writeOffPeriod,
                )
            )
                .onSuccess {
                    (navigatorHolder.navigator as? com.example.g_bankforemployees.common.navigation.AppNavigator)
                        ?.navigateBackFromCreditRateCreate()
                }
                .onFailure { e ->
                    _state.value = CreditRateCreateScreenState.Error(
                        message = e.message?.takeUnless { it.isBlank() } ?: "РќРµ СѓРґР°Р»РѕСЃСЊ СЃРѕР·РґР°С‚СЊ С‚Р°СЂРёС„",
                    )
                }
        }
    }

    fun onErrorDismiss() {
        _state.value = CreditRateCreateScreenState.Default()
    }

    fun onBackClick() {
        navigatorHolder.navigator?.navigateBack()
    }
}


