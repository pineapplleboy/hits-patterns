package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.usecase.account.GetMissedPaymentsUseCase
import com.example.g_bankforclient.presentation.state.MissedPaymentsScreenState
import com.example.g_bankforclient.presentation.ui.utils.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissedPaymentsViewModel @Inject constructor(
    private val getMissedPaymentsUseCase: GetMissedPaymentsUseCase
) : ViewModel() {

    private val _state =
        MutableStateFlow<MissedPaymentsScreenState>(MissedPaymentsScreenState.Loading)
    val state: StateFlow<MissedPaymentsScreenState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = MissedPaymentsScreenState.Loading
            runCatching { getMissedPaymentsUseCase() }
                .onSuccess { _state.value = MissedPaymentsScreenState.Default(it) }
                .onFailure {
                    _state.value =
                        MissedPaymentsScreenState.Error(it.toUserMessage("Не удалось загрузить просроченные платежи"))
                }
        }
    }
}
