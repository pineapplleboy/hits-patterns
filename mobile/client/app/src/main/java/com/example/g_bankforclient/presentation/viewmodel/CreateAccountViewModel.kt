package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.usecase.account.CreateAccountUseCase
import com.example.g_bankforclient.domain.usecase.currency.GetCurrenciesUseCase
import com.example.g_bankforclient.presentation.state.CreateAccountScreenState
import com.example.g_bankforclient.presentation.ui.utils.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val createAccountUseCase: CreateAccountUseCase,
    private val getCurrenciesUseCase: GetCurrenciesUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<CreateAccountScreenState> = MutableStateFlow(
        CreateAccountScreenState.Default(currenciesLoading = true)
    )
    val state: StateFlow<CreateAccountScreenState> = _state.asStateFlow()

    init {
        loadCurrencies()
    }

    fun loadCurrencies() {
        viewModelScope.launch {
            val current = _state.value as? CreateAccountScreenState.Default ?: return@launch
            _state.value = current.copy(currenciesLoading = true)
            runCatching { getCurrenciesUseCase() }
                .onSuccess { list ->
                    val selectedId = list.firstOrNull()?.id
                    _state.value = CreateAccountScreenState.Default(
                        currencies = list,
                        selectedCurrencyId = selectedId,
                        currenciesLoading = false
                    )
                }
                .onFailure { e ->
                    _state.value = CreateAccountScreenState.Error(
                        message = e.toUserMessage("Не удалось загрузить список валют")
                    )
                }
        }
    }

    fun selectCurrency(currencyId: Int) {
        val current = _state.value as? CreateAccountScreenState.Default ?: return
        _state.value = current.copy(selectedCurrencyId = currencyId)
    }

    fun createAccount() {
        viewModelScope.launch {
            val current = _state.value as? CreateAccountScreenState.Default ?: return@launch
            val currencyId = current.selectedCurrencyId
            if (currencyId == null) {
                _state.value = CreateAccountScreenState.Error(
                    message = "Выберите валюту счёта"
                )
                return@launch
            }
            _state.value = CreateAccountScreenState.Loading
            runCatching { createAccountUseCase(currencyId) }
                .onSuccess {
                    _state.value = CreateAccountScreenState.Success(
                        message = "Счет успешно открыт"
                    )
                }
                .onFailure { e ->
                    _state.value = CreateAccountScreenState.Error(
                        message = e.toUserMessage("Не удалось открыть счет")
                    )
                }
        }
    }
}
