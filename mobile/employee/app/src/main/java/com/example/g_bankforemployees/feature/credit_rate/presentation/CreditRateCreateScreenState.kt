package com.example.g_bankforemployees.feature.credit_rate.presentation

data class CreditRateCreateScreenState(
    val name: String = "",
    val percent: String = "",
    val days: String = "",
    val hours: String = "",
    val minutes: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val created: Boolean = false,
)
