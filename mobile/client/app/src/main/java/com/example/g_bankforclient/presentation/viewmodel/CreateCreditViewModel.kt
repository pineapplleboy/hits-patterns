package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.usecase.credit.CreateCreditUseCase
import com.example.g_bankforclient.presentation.state.CreateCreditScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCreditViewModel @Inject constructor(
    private val createCreditUseCase: CreateCreditUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<CreateCreditScreenState> = MutableStateFlow(
        value = CreateCreditScreenState.Default
    )
    val state: StateFlow<CreateCreditScreenState> = _state.asStateFlow()

    fun createCredit(name: String, amount: Double, interestRate: Double) {
        viewModelScope.launch {
            _state.value = CreateCreditScreenState.Loading
            try {
                createCreditUseCase(name, amount, interestRate)
                _state.value = CreateCreditScreenState.Success(
                    message = "Credit created successfully"
                )
            } catch (e: Exception) {
                _state.value = CreateCreditScreenState.Error(
                    message = e.message ?: "Failed to create credit"
                )
            }
        }
    }
}
