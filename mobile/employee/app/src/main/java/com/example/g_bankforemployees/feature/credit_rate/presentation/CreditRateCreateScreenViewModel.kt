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

    private val _state = MutableStateFlow(CreditRateCreateScreenState())
    val state: StateFlow<CreditRateCreateScreenState> = _state.asStateFlow()

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name, error = null) }
    }

    fun onPercentChange(percent: String) {
        _state.update { it.copy(percent = percent, error = null) }
    }

    fun onDaysChange(days: String) {
        _state.update { it.copy(days = days.filter { ch -> ch.isDigit() }, error = null) }
    }

    fun onHoursChange(hours: String) {
        _state.update { it.copy(hours = hours.filter { ch -> ch.isDigit() }, error = null) }
    }

    fun onMinutesChange(minutes: String) {
        _state.update { it.copy(minutes = minutes.filter { ch -> ch.isDigit() }, error = null) }
    }

    fun createRate() {
        val name = _state.value.name.trim()
        val percent = _state.value.percent.toIntOrNull()
        val days = _state.value.days.toIntOrNull() ?: 0
        val hours = _state.value.hours.toIntOrNull() ?: 0
        val minutes = _state.value.minutes.toIntOrNull() ?: 0
        if (name.isBlank() || percent == null || percent !in 0..100) {
            _state.update { it.copy(error = "Заполните название и ставку (0–100%)") }
            return
        }
        if (days == 0 && hours == 0 && minutes == 0) {
            _state.update { it.copy(error = "Задайте период списания (дни, часы или минуты)") }
            return
        }
        val writeOffPeriod = "${days}d${hours}h${minutes}m"
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            createCreditRateUseCase(
                CreditRateInput(
                    name = name,
                    percent = percent,
                    writeOffPeriod = writeOffPeriod,
                )
            )
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    (navigatorHolder.navigator as? com.example.g_bankforemployees.common.navigation.AppNavigator)
                        ?.navigateBackFromCreditRateCreate()
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Не удалось создать тариф",
                        )
                    }
                }
        }
    }

    fun onCreatedHandled() {
        _state.update { it.copy(created = false) }
    }

    fun onErrorDismiss() {
        _state.update { it.copy(error = null) }
    }

    fun onBackClick() {
        navigatorHolder.navigator?.navigateBack()
    }
}
