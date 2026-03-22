package com.example.g_bankforclient.presentation.state

import com.example.g_bankforclient.domain.models.Transaction

sealed interface MissedPaymentsScreenState {
    object Loading : MissedPaymentsScreenState
    data class Default(val payments: List<Transaction>) : MissedPaymentsScreenState
    data class Error(val message: String) : MissedPaymentsScreenState
}
